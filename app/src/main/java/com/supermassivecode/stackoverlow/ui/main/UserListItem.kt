package com.supermassivecode.stackoverlow.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserListItem(
    user: UiUser,
    onFollowToggle: (UiUser) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
      modifier = modifier
          .fillMaxWidth()
          .padding(12.dp)
          .clickable { onFollowToggle(user) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: add image

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
           Text(text = user.name, style = MaterialTheme.typography.titleMedium)
           Spacer(modifier = Modifier.height(4.dp))
           Text(
               text = "Reputation: ${user.reputationScore}",
               style = MaterialTheme.typography.bodyMedium,
               color = MaterialTheme.colorScheme.onSurfaceVariant
           )
        }

        TextButton(
            onClick = { onFollowToggle(user) }
        ) {
            Text(text = if (user.followed) "Unfollow" else "Follow")
        }
    }
}