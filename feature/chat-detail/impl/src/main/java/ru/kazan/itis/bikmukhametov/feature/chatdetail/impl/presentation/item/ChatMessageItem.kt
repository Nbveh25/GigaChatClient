package ru.kazan.itis.bikmukhametov.feature.chatdetail.impl.presentation.item

import android.util.Base64
import ru.kazan.itis.bikmukhametov.feature.chatdetail.api.model.ChatMessageModel
import java.util.UUID

data class ChatMessageItem(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatMessageRole,
    val text: String,
    val imageFileId: String? = null,
    val imageBytes: ByteArray? = null,
)

private val imageTagRegex = Regex("<img\\s+src=[\"']([^\"']+)[\"'][^>]*/?>")
private val uuidRegex = Regex(
    "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"
)

/**
 * GigaChat может вернуть картинку как data URL в атрибуте src: `data:image/jpeg;base64,...`.
 * Плейсхолдеры вида `{{text2image(...)}}` не являются валидным base64 — возвращаем null.
 */
private fun decodeDataUrlImageBytes(src: String): ByteArray? {
    if (!src.startsWith("data:image/", ignoreCase = true)) return null
    val marker = ";base64,"
    val idx = src.indexOf(marker, ignoreCase = true)
    if (idx == -1) return null
    val payload = src.substring(idx + marker.length).trim()
    if (payload.isEmpty()) return null
    if (payload.contains("{{") || payload.contains("}}")) return null
    return try {
        val decoded = Base64.decode(payload, Base64.DEFAULT)
        decoded.takeIf { it.isNotEmpty() }
    } catch (_: IllegalArgumentException) {
        null
    }
}

internal fun ChatMessageModel.toItem(imagesByFileId: Map<String, ByteArray>): ChatMessageItem {
    val rawSrc = imageTagRegex.find(text)?.groupValues?.getOrNull(1)?.trim()
    val textWithoutImageTag = text.replace(imageTagRegex, "").trim()

    val fileId = rawSrc?.takeIf { uuidRegex.matches(it) }
    val dataUrlBytes = rawSrc?.let(::decodeDataUrlImageBytes)

    return ChatMessageItem(
        id = id,
        role = if (role == "assistant") ChatMessageRole.Assistant else ChatMessageRole.User,
        text = textWithoutImageTag,
        imageFileId = fileId,
        imageBytes = dataUrlBytes ?: fileId?.let(imagesByFileId::get),
    )
}
