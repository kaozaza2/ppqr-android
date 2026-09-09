package com.mikore.ppqr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.database.AppHistory
import com.mikore.ppqr.database.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryRow(
    val history: AppHistory,
    val account: AppAccount?
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: AppRepo
) : ViewModel() {

    val rows: StateFlow<List<HistoryRow>> =
        combine(repo.getHistories(), repo.getAccounts()) { histories, accounts ->
            val accountMap = accounts.associateBy { it.uid }
            histories.map { history ->
                HistoryRow(history, accountMap[history.accountId])
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteHistory(uid: String) {
        val item = rows.value.find { it.history.uid == uid } ?: return
        viewModelScope.launch { repo.deleteHistory(item.history) }
    }
}
