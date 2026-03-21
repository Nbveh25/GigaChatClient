package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.UUID
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.R
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.component.ChatListRow
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.component.ChatListTopBar

private data class ChatListRowUi(val id: String, val title: String)

@Composable
fun ChatListScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToChat: (chatId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val seedChats = remember {
        listOf(
            ChatListRowUi("preview-1", "Пример чата"),
            ChatListRowUi("preview-2", "Ещё один чат"),
        )
    }
    var searchFieldText by remember { mutableStateOf("") }
    var appliedQuery by remember { mutableStateOf<String?>(null) }

    val displayedChats = remember(appliedQuery, seedChats) {
        val q = appliedQuery
        if (q.isNullOrBlank()) seedChats else seedChats.filter { it.title.contains(q, ignoreCase = true) }
    }

    fun submitSearch() {
        val trimmed = searchFieldText.trim()
        appliedQuery = trimmed.takeIf { it.isNotEmpty() }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ChatListTopBar(
                searchFieldText = searchFieldText,
                onSearchFieldTextChange = { searchFieldText = it },
                onOpenDrawer = onOpenDrawer,
                onSubmitSearch = { submitSearch() },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToChat(UUID.randomUUID().toString()) },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.chat_list_fab_new),
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (displayedChats.isEmpty()) {
                Text(
                    text = stringResource(R.string.chat_list_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    items(
                        items = displayedChats,
                        key = { it.id },
                    ) { chat ->
                        ChatListRow(
                            title = chat.title,
                            onClick = { onNavigateToChat(chat.id) },
                        )
                    }
                }
            }
        }
    }
}
