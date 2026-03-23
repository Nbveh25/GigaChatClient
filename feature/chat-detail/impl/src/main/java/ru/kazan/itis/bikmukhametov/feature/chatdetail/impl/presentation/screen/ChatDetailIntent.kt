package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

internal sealed interface ChatDetailIntent {
    data class InputTextChanged(val value: String) : ChatDetailIntent
    data object SendClicked : ChatDetailIntent
    data object ClearInputClicked : ChatDetailIntent
    data object RetryClicked : ChatDetailIntent
    data class ShareAssistantText(val text: String) : ChatDetailIntent
}
