package com.mikore.ppqr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.database.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repo: AppRepo
) : ViewModel() {

    val accounts: StateFlow<List<AppAccount>> = repo.getAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasAccounts: StateFlow<Boolean> = accounts.map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun deleteAccount(uid: String) {
        viewModelScope.launch { repo.deleteAccount(uid) }
    }
}
