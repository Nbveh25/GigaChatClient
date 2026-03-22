package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.R
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.component.ChatDetailInputBar
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.component.ChatDetailTopBar
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.component.ChatMessageBubble

@Composable
fun ChatDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val extraBottomItems =
        listOf(
            uiState.isGenerating,
            uiState.generationError && uiState.pendingRetryText != null,
        ).count { it }

    LaunchedEffect(uiState.messages.size, extraBottomItems) {
        val lastIndex = uiState.messages.size + extraBottomItems - 1
        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ChatDetailEffect.ShareText -> {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                    }
                    context.startActivity(Intent.createChooser(sendIntent, null))
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ChatDetailTopBar(
                title = uiState.chatTitle,
                onBack = onBack,
            )
        },
        bottomBar = {
            ChatDetailInputBar(
                text = uiState.inputText,
                onTextChange = { viewModel.onIntent(ChatDetailIntent.InputTextChanged(it)) },
                onSend = { viewModel.onIntent(ChatDetailIntent.SendClicked) },
                onClear = { viewModel.onIntent(ChatDetailIntent.ClearInputClicked) },
                sendEnabled = uiState.inputText.trim().isNotEmpty(),
                isSending = uiState.isGenerating,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(
                items = uiState.messages,
                key = { it.id },
            ) { message ->
                ChatMessageBubble(
                    message = message,
                    onShareAssistantText = {
                        viewModel.onIntent(ChatDetailIntent.ShareAssistantText(it))
                    },
                )
            }
            if (uiState.isGenerating) {
                item(key = "generating") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.chat_detail_generating),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            if (uiState.generationError && uiState.pendingRetryText != null) {
                item(key = "error_retry") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.chat_detail_error_generation),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        uiState.generationErrorMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.onIntent(ChatDetailIntent.RetryClicked) }) {
                            Text(stringResource(R.string.chat_detail_retry))
                        }
                    }
                }
            }
        }
    }
}
