package com.mikore.ppqr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikore.ppqr.R
import com.mikore.ppqr.adapter.AccountAdapter
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.databinding.AccountLayoutBinding
import com.mikore.ppqr.viewmodel.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: AccountLayoutBinding? = null
    private val binding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModels()

    private val adapter = AccountAdapter { account -> confirmDelete(account) }

    private var bottomSheetOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AccountLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.accountList.layoutManager = LinearLayoutManager(requireContext())
        binding.accountList.adapter = adapter

        binding.fabAdd.setOnClickListener {
            AddDialog().show(parentFragmentManager, "add_dialog")
        }
        binding.fabGen.setOnClickListener {
            GenDialog().show(parentFragmentManager, "gen_dialog")
        }
        binding.fabGroup.setOnClickListener { toggleFabMenu() }

        viewLifecycleOwner.lifecycleScope.launch {
            accountViewModel.accounts.collect { accounts ->
                adapter.submitList(accounts)
                binding.notFound.visibility =
                    if (accounts.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun toggleFabMenu() {
        bottomSheetOpen = !bottomSheetOpen
        val show = if (bottomSheetOpen) View.VISIBLE else View.GONE
        binding.fabGen.visibility = show
        binding.fabAdd.visibility = show
        binding.fabGenText.visibility = show
        binding.fabAddText.visibility = show
        binding.fabGroup.setImageResource(
            if (bottomSheetOpen) R.drawable.ic_close else R.drawable.ic_down
        )
    }

    private fun confirmDelete(account: AppAccount) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirm_title)
            .setMessage(R.string.delete_account_confirm)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                accountViewModel.deleteAccount(account.uid)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
