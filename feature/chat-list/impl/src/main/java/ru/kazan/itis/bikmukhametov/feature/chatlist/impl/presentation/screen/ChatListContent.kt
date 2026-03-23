package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.kazan.itis.bikmukhametov.feature.chatlist.api.model.ChatModel
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.component.ChatListRow

@Composable
fun ChatListContent(
    chats: List<ChatModel>,
    onChatClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(
            items = chats,
            key = { it.id },
        ) { chat ->
            ChatListRow(
                title = chat.title,
                onClick = { onChatClick(chat.id) },
            )
        }
    }
}
