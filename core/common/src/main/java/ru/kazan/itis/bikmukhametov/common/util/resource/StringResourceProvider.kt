package ru.kazan.itis.bikmukhametov.common.util.resource

interface StringResourceProvider {
    fun getString(resId: Int): String

    fun getString(resId: Int, vararg formatArgs: Any): String
}