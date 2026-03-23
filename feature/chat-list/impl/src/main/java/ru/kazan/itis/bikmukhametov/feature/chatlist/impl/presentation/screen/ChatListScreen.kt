package ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.component.ChatListFloatingActionButton
import ru.kazan.itis.bikmukhametov.feature.chatlist.impl.presentation.component.ChatListTopBar

@Composable
fun ChatListScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToChat: (chatId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ChatListEffect.NavigateToChat -> onNavigateToChat(effect.chatId)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ChatListTopBar(
                searchFieldText = uiState.searchFieldText,
                onSearchFieldTextChange = { viewModel.onIntent(ChatListIntent.SearchTextChanged(it)) },
                onSearchClick = { viewModel.onIntent(ChatListIntent.SearchClicked) },
                onOpenDrawer = onOpenDrawer,
            )
        },
        floatingActionButton = {
            ChatListFloatingActionButton(
                isCreating = uiState.isCreatingChat,
                onClick = { viewModel.onIntent(ChatListIntent.CreateNewChatClicked) }
            )
        },
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            val isInitialLoading = uiState.isChatListLoading && uiState.chats.isEmpty()

            when {
                isInitialLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.chats.isEmpty() -> {

                    ChatListEmptyState(
                        isSearchActive = uiState.isSearchActive,
                        searchQuery = uiState.searchFieldText
                    )
                    
                }

                else -> {

                    ChatListContent(
                        chats = uiState.chats,
                        canLoadMore = uiState.canLoadMore,
                        isNextPageLoading = uiState.isNextPageLoading,
                        onLoadNextPage = { viewModel.onIntent(ChatListIntent.LoadNextPage) },
                        onChatClick = { id ->
                            viewModel.onIntent(ChatListIntent.ChatItemClicked(id))
                        }
                    )

                }
            }
        }
    }
}
