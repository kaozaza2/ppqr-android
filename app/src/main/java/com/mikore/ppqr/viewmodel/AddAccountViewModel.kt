package com.mikore.ppqr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikore.ppqr.database.AccountType
import com.mikore.ppqr.database.AppAccount
import com.mikore.ppqr.database.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AddAccountViewModel @Inject constructor(
    private val repo: AppRepo
) : ViewModel() {

    sealed class UiEvent {
        object Saved : UiEvent()
        data class Error(val message: String) : UiEvent()
        data class ConfirmReplace(val no: String, val name: String) : UiEvent()
    }

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events

    fun save(no: String, name: String, existingUid: String? = null) {
        viewModelScope.launch {
            val existing = repo.getAccountByNo(no)
            val isSameAccount = existing?.uid == existingUid && existingUid != null

            if (isSameAccount || existing == null) {
                doSave(existingUid, name, no)
            } else {
                _events.emit(UiEvent.ConfirmReplace(no, name))
            }
        }
    }

    fun confirmReplace(no: String, name: String) {
        viewModelScope.launch {
            val existing = repo.getAccountByNo(no)
            doSave(existing?.uid, name, no)
        }
    }

    private suspend fun doSave(uid: String?, name: String, no: String) {
        val type = AccountType.fromLength(no.length)
        val account = AppAccount(
            uid = uid ?: java.util.UUID.randomUUID().toString(),
            name = name.ifEmpty { null },
            no = no,
            type = type
        )
        when (val result = repo.saveAccount(account)) {
            is AppRepo.Result.Success -> _events.emit(UiEvent.Saved)
            is AppRepo.Result.Error -> _events.emit(UiEvent.Error(result.exception.message ?: "Error"))
        }
    }
}
