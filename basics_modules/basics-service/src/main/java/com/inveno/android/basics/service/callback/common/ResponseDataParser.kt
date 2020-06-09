package com.inveno.android.basics.service.callback.common

interface ResponseDataParser<T> {
    fun parse(jsonString: String): T
}