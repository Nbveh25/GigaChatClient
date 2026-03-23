package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.screen

internal sealed interface ChatDetailEffect {
    data class ShareText(val text: String) : ChatDetailEffect
}
