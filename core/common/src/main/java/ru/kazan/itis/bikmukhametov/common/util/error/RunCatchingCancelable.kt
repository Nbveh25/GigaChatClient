package ru.kazan.itis.bikmukhametov.common.util.error

import kotlin.coroutines.cancellation.CancellationException

@Suppress("TooGenericExceptionCaught")
inline fun <T, R> T.runCatchingCancelable(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        // Пробрасываем отмену выше, чтобы корутина корректно завершилась
        throw e
    } catch (e: Throwable) {
        // Все остальные ошибки ловим в Result
        Result.failure(e)
    }
}
