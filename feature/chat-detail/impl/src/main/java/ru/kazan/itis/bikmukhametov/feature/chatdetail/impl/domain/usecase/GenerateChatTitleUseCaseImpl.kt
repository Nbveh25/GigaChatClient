package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.domain.usecase

import jakarta.inject.Inject
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase.GenerateChatTitleUseCase

class GenerateChatTitleUseCaseImpl @Inject constructor() : GenerateChatTitleUseCase {

    override fun invoke(assistantText: String, currentTitle: String): String {
        val line = assistantText.replace('\n', ' ').trim()

        if (line.isEmpty()) {
            return currentTitle.ifBlank { DEFAULT_CHAT_TITLE }
        }

        val words = line.split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .take(TITLE_MAX_WORDS)

        var title = words.joinToString(" ")

        if (title.length > TITLE_MAX_CHARS) {
            title = title.take(TITLE_MAX_CHARS)
                .trimEnd { it.isWhitespace() || it == ',' || it == '.' }

            if (title.isEmpty()) title = line.take(TITLE_MAX_CHARS)
        }

        return title
    }

    private companion object {
        const val DEFAULT_CHAT_TITLE = "Новый чат" 
        const val TITLE_MAX_WORDS = 6
        const val TITLE_MAX_CHARS = 20
    }
}
