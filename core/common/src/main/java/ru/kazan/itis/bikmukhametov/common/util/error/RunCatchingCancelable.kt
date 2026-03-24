package ru.kazan.itis.bikmukhametov.common.util.error

import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

@Suppress("TooGenericExceptionCaught")
suspend inline fun <T> runCatchingCancelable(crossinline block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        val body = e.response()?.errorBody()?.string() ?: e.message()
        Result.failure(IOException("Ошибка сервера (${e.code()})"))
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
