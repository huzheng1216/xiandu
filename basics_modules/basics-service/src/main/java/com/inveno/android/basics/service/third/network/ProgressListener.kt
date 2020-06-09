package com.inveno.android.basics.service.third.network

interface ProgressListener {
    fun update(
        bytesRead: Long,
        contentLength: Long,
        done: Boolean
    )
}