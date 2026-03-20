package ru.kazan.itis.bikmukhametov.common.util.resource

import android.content.Context
import jakarta.inject.Inject

internal class StringResourceProviderImpl @Inject constructor(
    private val context: Context
) : StringResourceProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}
