package com.mikore.ppqr.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mikore.ppqr.Contracts
import com.mikore.ppqr.activity.ResultActivity
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.databinding.AccountItemBinding
import com.mikore.ppqr.fragment.AddDialog

class AccountAdapter(
    private val onDeleteClick: (AppAccount) -> Unit
) : ListAdapter<AppAccount, AccountAdapter.AccountViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = AccountItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AccountViewHolder(private val binding: AccountItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(account: AppAccount) {
            binding.accountName.text = account.name
                ?: "PromptPay ending ${account.no.takeLast(3)}"
            binding.accountNo.text = account.no
            binding.accountTypeChip.text = account.type.asText()

            var sectionExpanded = false
            binding.root.setOnClickListener {
                sectionExpanded = !sectionExpanded
                binding.accountSection2.visibility =
                    if (sectionExpanded) View.VISIBLE else View.GONE
            }

            binding.customAmount.setOnCheckedChangeListener { _, isChecked ->
                binding.customAmountLayout.visibility =
                    if (isChecked) View.VISIBLE else View.GONE
            }

            binding.editButton.setOnClickListener {
                val activity = binding.root.context as? FragmentActivity
                if (activity != null) {
                    AddDialog.newInstance(account)
                        .show(activity.supportFragmentManager, "add_account")
                }
            }

            binding.deleteButton.setOnClickListener { onDeleteClick(account) }

            binding.generateButton.setOnClickListener {
                val args = Bundle().apply {
                    putString(Contracts.KEY_ACCOUNT_ID, account.uid)
                    putBoolean(Contracts.KEY_SAVE_HISTORY, true)
                    val amount = binding.customAmountField.text.toString()
                    if (amount.isEmpty()) {
                        putBoolean(Contracts.KEY_HAVE_AMOUNT, false)
                    } else {
                        putBoolean(Contracts.KEY_HAVE_AMOUNT, true)
                        putString(Contracts.KEY_AMOUNT, amount)
                    }
                    val desc = binding.customDescField.text.toString()
                    if (desc.isEmpty()) {
                        putBoolean(Contracts.KEY_HAVE_DESCRIPTION, false)
                    } else {
                        putBoolean(Contracts.KEY_HAVE_DESCRIPTION, true)
                        putString(Contracts.KEY_DESCRIPTION, desc)
                    }
                }
                binding.root.context.startActivity(
                    Intent(binding.root.context, ResultActivity::class.java).putExtras(args)
                )
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AppAccount>() {
            override fun areItemsTheSame(oldItem: AppAccount, newItem: AppAccount) =
                oldItem.uid == newItem.uid

            override fun areContentsTheSame(oldItem: AppAccount, newItem: AppAccount) =
                oldItem == newItem
        }
    }
}
