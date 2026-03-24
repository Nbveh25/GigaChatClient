package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.resource

import android.content.ContentResolver
import android.provider.OpenableColumns
import java.io.InputStream
import javax.inject.Inject
import androidx.core.net.toUri
import ru.kazan.itis.bikmukhametov.api.resource.ImageResourceProvider

internal class ImageResourceProviderImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : ImageResourceProvider {

    override fun openInputStream(uriString: String): InputStream? = runCatching {
        contentResolver.openInputStream(uriString.toUri())
    }.getOrNull()

    override fun getFileName(uriString: String): String? = runCatching {
        val uri = uriString.toUri()

        if (uri.scheme == CONTENT_SCHEME) {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index != -1) cursor.getString(index) else null
                    } else null
                }
        } else {
            uri.lastPathSegment
        }
    }.getOrNull() ?: uriString.toUri().lastPathSegment

    private companion object {
        private const val CONTENT_SCHEME = "content"
    }
}

