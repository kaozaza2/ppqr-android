package com.mikore.ppqr.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.mikore.ppqr.R
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.database.AppHistory
import com.mikore.ppqr.databinding.QrPopupBinding
import com.mikore.ppqr.utility.PromptPayUtil

class QRPopup(
    private val history: AppHistory,
    private val account: AppAccount
) : DialogFragment() {

    private var _binding: QrPopupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = QrPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameText = if (account.name.isNullOrEmpty()) {
            account.no
        } else {
            "${account.name} (${account.no})"
        }
        binding.textView11.text = nameText

        val amountVal = if (history.amount.isNullOrEmpty()) {
            getString(R.string.not_specified)
        } else {
            "${history.amount} ${getString(R.string.baht)}."
        }
        binding.textView12.text = getString(R.string.amount_format, amountVal)

        val descVal = history.description
            ?.takeIf { it.isNotEmpty() }
            ?: getString(R.string.not_specified)
        binding.textView13.text = getString(R.string.note_format, descVal)

        val data = PromptPayUtil().generateQRData(account.no, history.amount)
        binding.imageView7.setImageBitmap(generateQR(data, 300))

        binding.qrDismiss.setOnClickListener { dismiss() }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
