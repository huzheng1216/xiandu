package com.inveno.android.basics.service.third.network.impl

import com.inveno.android.basics.service.third.network.ProgressListener
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import org.slf4j.LoggerFactory
import java.io.IOException

class ProgressRequestBody(
    /**
     * 实际的带包装请求体
     */
    private val mRequestBody: RequestBody,
    /**
     * 传输进度监听
     */
    private val mOnFileTransferredListener: ProgressListener
) : RequestBody() {

    private var mBufferedSink: BufferedSink? = null
    override fun contentType(): MediaType? {
        return mRequestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mRequestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (mBufferedSink == null) {
            mBufferedSink = Okio.buffer(sink(sink))
        }
        mRequestBody.writeTo(mBufferedSink)
        mBufferedSink!!.flush()
    }

    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            var contentLength = 0L
            var bytesWritten = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    contentLength = contentLength()
                }
                bytesWritten += byteCount
                mOnFileTransferredListener.update(
                    bytesWritten,
                    contentLength,
                    bytesWritten >= contentLength
                )
                logger.info(
                    "update bytesWritten:{},contentLength:{},bytesWritten>=contentLength:{}",
                    bytesWritten,
                    contentLength,
                    bytesWritten >= contentLength
                )
            }
        }
    }

    companion object {
        private val logger =
            LoggerFactory.getLogger(ProgressRequestBody::class.java)
    }

}