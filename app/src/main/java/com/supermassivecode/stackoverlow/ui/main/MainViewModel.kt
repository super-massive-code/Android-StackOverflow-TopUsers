package com.supermassivecode.stackoverlow.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.application
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
    application: Application
): AndroidViewModel(application) {
    private val repo: UserRepo = UserRepo(application.applicationContext)
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        _uiState.update { UiState() }
        loadData()
    }

    private fun loadData() {
        _uiState.update { UiState(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
          val users = repo.getLatestTopUsers().map {
                UiUser(
                    userId = it.userId,
                    name = it.displayName,
                    imageUrl = it.profileImage,
                    reputationScore = it.reputation,
                    followed = repo.isFollowing(it.userId)
                )
            }
            _uiState.update { UiState(
                users = users
            ) }
        }
    }

    fun onFollowToggle(user: UiUser) {
        repo.toggleFollow(userId = user.userId)
    }
}