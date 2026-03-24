package ru.kazan.itis.bikmukhametov.common.util.error

fun interface ErrorMessageMapper {
    fun map(throwable: Throwable): String
}
