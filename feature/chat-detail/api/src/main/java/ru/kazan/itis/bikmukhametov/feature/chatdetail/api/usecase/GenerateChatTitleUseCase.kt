package ru.kazan.itis.bikmukhametov.feature.chatdetail.api.usecase

interface GenerateChatTitleUseCase {
    /**
     * @param fallbackUserMessage последнее сообщение пользователя — если ответ ассистента только с &lt;img&gt; и т.п.
     */
    operator fun invoke(
        assistantText: String,
        currentTitle: String,
        fallbackUserMessage: String? = null,
    ): String
}
