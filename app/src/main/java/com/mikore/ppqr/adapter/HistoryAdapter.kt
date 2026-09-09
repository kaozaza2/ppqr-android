package com.mikore.ppqr.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mikore.ppqr.R
import com.mikore.ppqr.databinding.HistoryItemBinding
import com.mikore.ppqr.viewmodel.HistoryRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val onClick: (HistoryRow) -> Unit,
    private val onDelete: (HistoryRow) -> Unit
) : ListAdapter<HistoryRow, HistoryAdapter.ViewHolder>(DIFF) {

    private val dateFormat = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HistoryItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, dateFormat, onClick, onDelete)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: HistoryItemBinding,
        private val dateFormat: SimpleDateFormat,
        private val onClick: (HistoryRow) -> Unit,
        private val onDelete: (HistoryRow) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(row: HistoryRow) {
            val context = binding.root.context
            val account = row.account
            val title = if (account != null) {
                if (account.name.isNullOrEmpty()) account.no
                else "${account.name} (${account.no})"
            } else {
                row.history.accountId
            }
            binding.historyTitle.text = title
            binding.historyDesc.text = row.history.description
                ?: context.getString(R.string.no_description)
            binding.historyDate.text = dateFormat.format(Date(row.history.time))

            val showAmount = !row.history.amount.isNullOrEmpty()
            binding.historyAmount.text = row.history.amount?.let {
                "$it ${context.getString(R.string.baht)}"
            }
            binding.historyAmount.visibility =
                if (showAmount) View.VISIBLE else View.GONE

            binding.historyFrameBg.setOnClickListener { onClick(row) }
            binding.historyBin.setOnClickListener { onDelete(row) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<HistoryRow>() {
            override fun areItemsTheSame(oldItem: HistoryRow, newItem: HistoryRow) =
                oldItem.history.uid == newItem.history.uid

            override fun areContentsTheSame(oldItem: HistoryRow, newItem: HistoryRow) =
                oldItem.history == newItem.history && oldItem.account == newItem.account
        }
    }
}
