package com.mikore.ppqr.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mikore.ppqr.R
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.databinding.DialogAddBinding
import com.mikore.ppqr.viewmodel.AddAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddDialog : DialogFragment() {

    private val addAccountViewModel: AddAccountViewModel by viewModels()

    private var _binding: DialogAddBinding? = null
    private val binding get() = _binding!!

    private var editUid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            editUid = args.getString(ARG_UID)
            binding.addNameEdit.setText(args.getString(ARG_NAME))
            binding.addNoEdit.setText(args.getString(ARG_NO))
        }

        binding.addBtnCancel.setOnClickListener { dismiss() }
        binding.addBtnSave.setOnClickListener { onSaveClicked() }

        viewLifecycleOwner.lifecycleScope.launch {
            addAccountViewModel.events.collectLatest { event ->
                when (event) {
                    is AddAccountViewModel.UiEvent.Saved -> dismiss()
                    is AddAccountViewModel.UiEvent.Error -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                    }
                    is AddAccountViewModel.UiEvent.ConfirmReplace -> {
                        showReplaceDialog(event.no, event.name)
                    }
                }
            }
        }
    }

    private fun onSaveClicked() {
        val no = binding.addNoEdit.text.toString().trim()
        val name = binding.addNameEdit.text.toString().trim()
        if (no.isEmpty() || no.length !in 10..15) {
            Toast.makeText(requireContext(), R.string.enter_valid_account, Toast.LENGTH_LONG).show()
            return
        }
        addAccountViewModel.save(no, name, editUid)
    }

    private fun showReplaceDialog(no: String, name: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.account_exists)
            .setMessage(R.string.replace_account)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                addAccountViewModel.confirmReplace(no, name)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_UID = "uid"
        private const val ARG_NAME = "name"
        private const val ARG_NO = "no"

        fun newInstance(account: AppAccount? = null): AddDialog {
            val dialog = AddDialog()
            account?.let {
                dialog.arguments = Bundle().apply {
                    putString(ARG_UID, it.uid)
                    putString(ARG_NAME, it.name)
                    putString(ARG_NO, it.no)
                }
            }
            return dialog
        }
    }
}
