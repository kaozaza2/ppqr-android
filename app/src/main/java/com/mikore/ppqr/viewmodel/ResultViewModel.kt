package com.mikore.ppqr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.database.AppHistory
import com.mikore.ppqr.database.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repo: AppRepo
) : ViewModel() {

    private val _account = MutableStateFlow<AppAccount?>(null)
    val account: StateFlow<AppAccount?> = _account

    fun loadAccount(uid: String?) {
        if (uid.isNullOrEmpty()) return
        viewModelScope.launch { _account.value = repo.getAccount(uid) }
    }

    fun saveHistory(account: AppAccount, desc: String?, amount: String?) {
        viewModelScope.launch {
            repo.saveHistory(AppHistory(account.uid, desc, amount))
        }
    }
}
