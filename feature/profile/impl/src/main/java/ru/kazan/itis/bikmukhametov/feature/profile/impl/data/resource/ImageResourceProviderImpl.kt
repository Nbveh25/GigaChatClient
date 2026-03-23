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

    override fun openInputStream(uriString: String): InputStream? {
        return try {
            val uri = uriString.toUri()
            contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            null
        }
    }

    override fun getFileName(uriString: String): String? {
        return try {
            val uri = uriString.toUri()
            var name: String? = null
            
            if (uri.scheme == CONTENT_SCHEME) {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            name = cursor.getString(nameIndex)
                        }
                    }
                }
            }
            
            name ?: uri.lastPathSegment
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        private const val CONTENT_SCHEME = "content"
    }
}

