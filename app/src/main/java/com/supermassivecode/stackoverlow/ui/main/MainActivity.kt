package com.supermassivecode.stackoverlow.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.supermassivecode.stackoverlow.ui.theme.StackoverlowTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StackoverlowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val uiState = viewModel.uiState.collectAsState().value
                    if (uiState.loading) {
                        Loading(modifier = Modifier.padding())
                    } else {
                        MainList(
                            userList = uiState.users,
                            onFollowToggle = viewModel::onFollowToggle,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Loading" },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            strokeWidth = 3.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Preview(showBackground = true, name = "MainList – Light")
@Composable
fun MainListPreview() {
    StackoverlowTheme {
        val sample = listOf(
            UiUser(
                userId = 1,
                name = "Trev Jones",
                imageUrl = "",
                reputationScore = 12_345,
                followed = false
            ),
            UiUser(
                userId = 2,
                name = "Terry Jones",
                imageUrl = "",
                reputationScore = 8_760,
                followed = true
            ),
            UiUser(
                userId = 3,
                name = "Gary Potter",
                imageUrl = "",
                reputationScore = 2_101,
                followed = false
            )
        )

        MainList(
            userList = sample,
            onFollowToggle = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
