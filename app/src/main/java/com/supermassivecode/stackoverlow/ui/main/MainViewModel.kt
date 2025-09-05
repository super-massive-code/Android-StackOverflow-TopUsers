package com.supermassivecode.stackoverlow.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supermassivecode.stackoverlow.data.local.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val users: List<UiUser> = emptyList(),
    val message: String? = null,
    val loading: Boolean = false
)

class MainViewModel(
    private val repo: UserRepo = UserRepo()
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        _uiState.update { UiState() }
        loadData()
    }

    private fun loadData() {
        _uiState.update { UiState(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
          val users = repo.getTopUsers().map {
                UiUser(
                    name = it.displayName,
                    imageUrl = it.profileImage,
                    reputationScore = it.reputation
                )
            }
            _uiState.update { UiState(
                users = users
            ) }
        }
    }

    fun onFollowToggle(uiUser: UiUser) {
        print("")
    }
}