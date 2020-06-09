package com.inveno.android.basics.service.third.network.impl.okhttp

import android.content.Context
import android.net.Uri
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.internal.Util
import okio.BufferedSink
import okio.Okio
import okio.Source
import java.io.IOException

class UriRequestBody(
    var context: Context,
    var uri: Uri?,
    var contentType: MediaType?
) : RequestBody() {
    override fun contentType(): MediaType? {
        return contentType
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        val `is` = context.contentResolver.openInputStream(uri!!)
        val len = `is`!!.available().toLong()
        `is`.close()
        return len
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        var source: Source? = null
        try {
            source = Okio.source(context.contentResolver.openInputStream(uri!!))
            sink.writeAll(source)
        } finally {
            Util.closeQuietly(source)
        }
    }

    companion object {
        fun createUriRequestBody(
            context: Context,
            uri: Uri?,
            contentType: MediaType?
        ): RequestBody {
            return UriRequestBody(
                context,
                uri,
                contentType
            )
        }
    }

}