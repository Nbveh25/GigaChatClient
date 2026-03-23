package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.R

@Composable
fun ChatListEmptyState(
    isSearchActive: Boolean,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val emptyText = if (isSearchActive) {
            stringResource(R.string.chat_list_search_empty, searchQuery)
        } else {
            stringResource(R.string.chat_list_empty)
        }

        Text(
            text = emptyText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
