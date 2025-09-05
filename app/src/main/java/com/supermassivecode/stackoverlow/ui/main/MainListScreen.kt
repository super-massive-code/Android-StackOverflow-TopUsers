package com.supermassivecode.stackoverlow.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun  MainList(
    userList: List<UiUser>,
    onFollowToggle: (UiUser) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        userList.forEach { userItem ->
            UserListItem(userItem, onFollowToggle, modifier)
        }
    }
}