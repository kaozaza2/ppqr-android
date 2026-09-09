package com.mikore.ppqr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mikore.ppqr.R
import com.mikore.ppqr.database.AccountType
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.database.AppHistory
import com.mikore.ppqr.databinding.DialogQuickGenBinding

class GenDialog : DialogFragment() {

    private var _binding: DialogQuickGenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogQuickGenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.quickBtnCancel.setOnClickListener { dismiss() }

        binding.quickBtnGen.setOnClickListener {
            val no = binding.quickNoEdit.text.toString().trim()
            val am = binding.quickAmountEdit.text.toString().trim()
            val ds = binding.quickDescEdit.text.toString().trim()
            if (no.isEmpty() || no.length !in 10..15) {
                Toast.makeText(requireContext(), R.string.enter_valid_account, Toast.LENGTH_LONG).show()
            } else {
                val history = AppHistory(
                    accountId = "",
                    description = ds.ifEmpty { getString(R.string.quick_generate) },
                    amount = am.ifEmpty { null }
                )
                val account = AppAccount(
                    uid = "",
                    name = null,
                    no = no,
                    type = AccountType.fromLength(no.length)
                )
                QRPopup(history, account).show(parentFragmentManager, "quick_gen")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
