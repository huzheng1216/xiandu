package com.inveno.android.basics.service.third.network

abstract class HttpExecutor {
    @Throws(Exception::class)
    abstract fun execute(
        method: String,
        url: String,
        type: String,
        data: ByteArray
    )

    @Throws(Exception::class)
    abstract fun executeForResult(
        method: String,
        url: String,
        type: String,
        data: ByteArray
    ): HttpResponse
}