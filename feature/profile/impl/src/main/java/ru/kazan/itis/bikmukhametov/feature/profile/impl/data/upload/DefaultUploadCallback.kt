package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.upload

import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

/**
 * Базовый класс, чтобы не переопределять все методы интерфейса каждый раз
 */
@Suppress("EmptyFunctionBlock")
open class DefaultUploadCallback : UploadCallback {
    override fun onStart(requestId: String) {}
    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
    override fun onSuccess(requestId: String, resultData: Map<*, *>) {}
    override fun onError(requestId: String, error: ErrorInfo) {}
    override fun onReschedule(requestId: String, error: ErrorInfo) {}
}
