package com.supermassivecode.stackoverlow.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.supermassivecode.stackoverlow.data.local.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val users: List<UiUser> = emptyList(),
    val message: String? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class MainViewModel(
    application: Application
): AndroidViewModel(application) {
    private val repo: UserRepo = UserRepo(application.applicationContext)
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.update { it.copy(loading = true, error = null) }

        viewModelScope.launch {
            repo.getLatestTopUsers()
                .onSuccess { users ->
                    val uiUsers = users.map { user ->
                        UiUser(
                            userId = user.userId,
                            name = user.displayName,
                            imageUrl = user.profileImage,
                            reputationScore = user.reputation,
                            followed = repo.isFollowing(user.userId)
                        )
                    }
                    _uiState.update {
                        it.copy(
                            users = uiUsers,
                            loading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                }
        }
    }

    fun onFollowToggle(user: UiUser) {
        val isNowFollowing = repo.toggleFollow(userId = user.userId)

        _uiState.update { currentState ->
            currentState.copy(
                users = currentState.users.map { uiUser ->
                    if (uiUser.userId == user.userId) {
                        uiUser.copy(followed = isNowFollowing)
                    } else {
                        uiUser
                    }
                }
            )
        }
    }
}
