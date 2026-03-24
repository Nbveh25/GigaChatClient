package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import jakarta.inject.Inject
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GenerateChatTitleUseCase

internal class GenerateChatTitleUseCaseImpl @Inject constructor() : GenerateChatTitleUseCase {

    override fun invoke(
        assistantText: String,
        currentTitle: String,
        fallbackUserMessage: String?,
    ): String {
        val fromAssistant = titleFromPlainText(stripHtml(assistantText))
        if (fromAssistant != null) return fromAssistant

        val fromUser = fallbackUserMessage?.let { titleFromPlainText(it) }
        if (fromUser != null) return fromUser

        return currentTitle.ifBlank { DEFAULT_CHAT_TITLE }
    }

    /** Убирает теги (в т.ч. `<img src="uuid">`), чтобы заголовок не начинался с разметки. */
    private fun stripHtml(text: String): String =
        HTML_TAG_REGEX.replace(text, " ").replace('\n', ' ').trim()

    private fun titleFromPlainText(text: String): String? {
        val line = text.replace('\n', ' ').trim()
        if (line.isEmpty()) return null
        // Не использовать «текст», если после очистки остались только UUID/мусор
        if (line.length < 2 && !line.any { it.isLetter() }) return null

        val words = line.split(Regex("\\s+"))
            .filter { it.isNotEmpty() && !it.startsWith("<") }
            .take(TITLE_MAX_WORDS)

        if (words.isEmpty()) return null

        var title = words.joinToString(" ")
        if (title.length > TITLE_MAX_CHARS) {
            title = title.take(TITLE_MAX_CHARS)
                .trimEnd { it.isWhitespace() || it == ',' || it == '.' }

            if (title.isEmpty()) title = line.take(TITLE_MAX_CHARS)
        }

        return title.ifBlank { null }
    }

    private companion object {
        const val DEFAULT_CHAT_TITLE = "Новый чат"
        const val TITLE_MAX_WORDS = 6
        const val TITLE_MAX_CHARS = 20
        private val HTML_TAG_REGEX = Regex("<[^>]+>", RegexOption.DOT_MATCHES_ALL)
    }
}
