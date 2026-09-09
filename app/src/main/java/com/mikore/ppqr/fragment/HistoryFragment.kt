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
import com.mikore.ppqr.adapter.HistoryAdapter
import com.mikore.ppqr.database.AccountType
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.databinding.HistoryLayoutBinding
import com.mikore.ppqr.viewmodel.HistoryRow
import com.mikore.ppqr.viewmodel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: HistoryLayoutBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by viewModels()

    private val adapter = HistoryAdapter(
        onClick = { row -> showQrPopup(row) },
        onDelete = { row -> confirmDelete(row) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HistoryLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.historyList.layoutManager = LinearLayoutManager(requireContext())
        binding.historyList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            historyViewModel.rows.collect { rows ->
                adapter.submitList(rows)
            }
        }
    }

    private fun showQrPopup(row: HistoryRow) {
        val account = row.account ?: AppAccount(
            uid = row.history.accountId,
            name = null,
            no = row.history.accountId,
            type = AccountType.EWALLET
        )
        QRPopup(row.history, account).show(parentFragmentManager, "dialog_qr")
    }

    private fun confirmDelete(row: HistoryRow) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_confirm_title)
            .setMessage(R.string.delete_history_confirm)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                historyViewModel.deleteHistory(row.history.uid)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
