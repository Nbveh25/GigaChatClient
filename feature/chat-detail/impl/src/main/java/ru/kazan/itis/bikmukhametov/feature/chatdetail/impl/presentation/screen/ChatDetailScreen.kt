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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.R
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.component.ChatDetailInputBar
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.component.ChatDetailTopBar
import ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.component.ChatMessageBubble

/**
 * Экран переписки с AI. Состояние и сценарии — моки; позже сюда подключится MVI / GigaChat API.
 */
@Composable
fun ChatDetailScreen(
    chatId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    chatTitle: String = stringResource(R.string.chat_detail_screen_title),
) {
    val context = LocalContext.current
    var uiState by remember(chatId) {
        mutableStateOf(ChatDetailMocks.initialState(chatTitle = chatTitle))
    }
    var failNextGenerationOnce by remember(chatId) { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
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

    fun shareAssistantText(text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(sendIntent, null))
    }

    fun runMockGeneration(userText: String, isRetry: Boolean) {
        if (userText.isBlank() || uiState.isGenerating) return
        scope.launch {
            uiState = uiState.copy(
                isGenerating = true,
                generationError = false,
                pendingRetryText = null,
            )
            delay(1200)
            if (failNextGenerationOnce && !isRetry) {
                failNextGenerationOnce = false
                uiState = uiState.copy(
                    isGenerating = false,
                    generationError = true,
                    pendingRetryText = userText,
                )
                return@launch
            }
            uiState = uiState.copy(
                isGenerating = false,
                generationError = false,
                pendingRetryText = null,
                messages = uiState.messages + ChatMessageUi(
                    role = ChatMessageRole.Assistant,
                    text = ChatDetailMocks.mockAssistantReply(userText),
                ),
            )
        }
    }

    fun send() {
        val trimmed = uiState.inputText.trim()
        if (trimmed.isEmpty() || uiState.isGenerating) return
        uiState = uiState.copy(
            messages = uiState.messages + ChatMessageUi(role = ChatMessageRole.User, text = trimmed),
            inputText = "",
            generationError = false,
            pendingRetryText = null,
        )
        runMockGeneration(trimmed, isRetry = false)
    }

    fun retry() {
        val pending = uiState.pendingRetryText ?: return
        runMockGeneration(pending, isRetry = true)
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
                onTextChange = { uiState = uiState.copy(inputText = it) },
                onSend = ::send,
                onClear = { uiState = uiState.copy(inputText = "") },
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
                    onShareAssistantText = { shareAssistantText(it) },
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.chat_detail_error_generation),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(onClick = ::retry) {
                            Text(stringResource(R.string.chat_detail_retry))
                        }
                    }
                }
            }
        }
    }
}
