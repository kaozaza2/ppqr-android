package com.mikore.ppqr.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.mikore.ppqr.Contracts
import com.mikore.ppqr.R
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.databinding.ResultLayoutBinding
import com.mikore.ppqr.utility.PromptPayUtil
import com.mikore.ppqr.viewmodel.ResultViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.math.min
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResultActivity : AppCompatActivity(),
    NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ResultLayoutBinding
    private val resultViewModel: ResultViewModel by viewModels()

    @Inject
    lateinit var promptPay: PromptPayUtil

    private var qrBitmap: Bitmap? = null
    private var titleText: String = ""
    private var descText: String = ""
    private var amountText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ResultLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.resultToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rsBnv.setOnItemSelectedListener(this)

        val uid = intent.getStringExtra(Contracts.KEY_ACCOUNT_ID)
        val saveHistory = intent.getBooleanExtra(Contracts.KEY_SAVE_HISTORY, false)
        val haveAmount = intent.getBooleanExtra(Contracts.KEY_HAVE_AMOUNT, false)
        val amount = if (haveAmount) intent.getStringExtra(Contracts.KEY_AMOUNT) else null
        val haveDesc = intent.getBooleanExtra(Contracts.KEY_HAVE_DESCRIPTION, false)
        val desc = if (haveDesc) intent.getStringExtra(Contracts.KEY_DESCRIPTION) else null

        amountText = getString(
            R.string.amount_format,
            if (haveAmount) {
                "$amount ${getString(R.string.baht)}."
            } else {
                getString(R.string.not_specified)
            }
        )
        descText = getString(
            R.string.note_format,
            desc ?: getString(R.string.not_specified)
        )

        resultViewModel.loadAccount(uid)
        observeAccount(uid, saveHistory, desc, amount)
    }

    private fun observeAccount(uid: String?, saveHistory: Boolean, desc: String?, amount: String?) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                resultViewModel.account.collect { account ->
                    if (account == null) return@collect

                    titleText = if (account.name.isNullOrEmpty()) {
                        account.no
                    } else {
                        "${account.no} (${account.name})"
                    }
                    binding.textView7.text = titleText
                    binding.textView8.text = amountText
                    binding.textView9.text = descText

                    try {
                        val data = promptPay.generateQRData(account.no, amount)
                        qrBitmap = generateQR(
                            data,
                            min(binding.imageView2.width, 800).coerceAtLeast(300)
                        )
                        qrBitmap?.let { binding.imageView2.setImageBitmap(it) }
                        if (saveHistory) {
                            resultViewModel.saveHistory(account, desc, amount)
                        }
                    } catch (e: WriterException) {
                        Toast.makeText(this@ResultActivity, R.string.enter_valid_account, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun generateQR(data: String, size: Int): Bitmap {
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )
        val matrix = QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                pixels[y * size + x] = if (matrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
        return bitmap
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rs_close -> finish()
            R.id.rs_save -> {
                if (storeImage() != null) {
                    Toast.makeText(this, R.string.saved_success, Toast.LENGTH_SHORT).show()
                }
            }
            R.id.rs_share -> {
                val uri = storeImage()
                if (uri != null) {
                    val it = Intent(Intent.ACTION_SEND).apply {
                        type = "image/jpeg"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(it, getString(R.string.share_image)))
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun storeImage(): Uri? {
        val image = exportToBitmap() ?: return null
        try {
            val date = System.currentTimeMillis()
            val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-", Locale.ENGLISH)
                .format(date) + getString(R.string.app_name).replace(" ", "-")
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpeg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, date)
                put(MediaStore.Images.Media.DATE_MODIFIED, date)
                if (Build.VERSION.SDK_INT >= 29) {
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "${android.os.Environment.DIRECTORY_PICTURES}/${getString(R.string.app_name)}"
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
            val collection = if (Build.VERSION.SDK_INT >= 29) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val insertUri = contentResolver.insert(collection, values) ?: return null
            contentResolver.openOutputStream(insertUri, "w")?.use {
                image.compress(Bitmap.CompressFormat.JPEG, 85, it)
            }
            if (Build.VERSION.SDK_INT >= 29) {
                val clear = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
                contentResolver.update(insertUri, clear, null, null)
            }
            return insertUri
        } catch (e: Exception) {
            return null
        }
    }

    private fun exportToBitmap(): Bitmap? {
        val qr = qrBitmap ?: return null
        val base = BitmapFactory.decodeResource(resources, R.drawable.optimize)
        return base.copy(Bitmap.Config.ARGB_8888, true).applyCanvas {
            val qrScaled = Bitmap.createScaledBitmap(qr, 300, 300, true)
            drawBitmap(qrScaled, 50f, 50f, null)

            val paint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.FILL
                textSize = 18f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            drawText(titleText, width / 2f, 360f, paint)
            paint.textSize = 14f
            paint.color = Color.rgb(0x32, 0x32, 0x32)
            drawText(descText, width / 2f, 375f, paint)
            drawText(amountText, width / 2f, 390f, paint)
        }
    }
}
