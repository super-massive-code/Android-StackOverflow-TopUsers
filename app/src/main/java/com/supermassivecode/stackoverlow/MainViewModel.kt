package com.supermassivecode.stackoverlow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supermassivecode.stackoverlow.data.local.UserRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val repo: UserRepo = UserRepo()
): ViewModel() {

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getTopUsers()
        }
    }
}