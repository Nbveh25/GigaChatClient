package ru.kazan.itis.bikmukhametov.api.resource

import java.io.InputStream

interface ImageResourceProvider {
    fun openInputStream(uriString: String): InputStream?

    fun getFileName(uriString: String): String?
}
