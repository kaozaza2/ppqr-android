package com.mikore.ppqr.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.mikore.ppqr.R
import com.mikore.ppqr.adapter.PagerAdapter
import com.mikore.ppqr.databinding.MainLayoutBinding
import com.mikore.ppqr.fragment.AppDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.pagerMain.adapter = PagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pagerMain) { tab, position ->
            tab.text = getString(if (position == 0) R.string.tab_accounts else R.string.tab_history)
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.about_menu) {
            AppDialog().show(supportFragmentManager, "about")
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
