package ru.kazan.itis.bikmukhametov.common.util.error

import java.io.IOException
import javax.inject.Inject
import retrofit2.HttpException
import ru.kazan.itis.bikmukhametov.common.R
import ru.kazan.itis.bikmukhametov.common.util.resource.StringResourceProvider

class ErrorMessageMapperImpl @Inject constructor(
    private val stringResourceProvider: StringResourceProvider,
) : ErrorMessageMapper {

    override fun map(throwable: Throwable): String {
        return when (throwable) {
            is IOException -> stringResourceProvider.getString(R.string.common_error_network)
            is HttpException -> mapHttpError(throwable.code())
            else -> throwable.message
                ?.trim()
                ?.takeIf(String::isNotEmpty)
                ?: stringResourceProvider.getString(R.string.common_error_unknown)
        }
    }

    private fun mapHttpError(code: Int): String {
        val resId = when (code) {
            400 -> R.string.common_error_http_400
            401 -> R.string.common_error_http_401
            403 -> R.string.common_error_http_403
            404 -> R.string.common_error_http_404
            408 -> R.string.common_error_http_408
            in 500..599 -> R.string.common_error_http_5xx
            else -> R.string.common_error_unknown
        }
        return stringResourceProvider.getString(resId)
    }
}
