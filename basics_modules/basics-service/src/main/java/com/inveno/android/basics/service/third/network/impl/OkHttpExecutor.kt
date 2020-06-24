package com.inveno.android.basics.service.third.network.impl

import android.text.TextUtils
import com.inveno.android.basics.service.app.info.AppInfoHolder
import com.inveno.android.basics.service.third.network.HttpExecutor
import com.inveno.android.basics.service.third.network.HttpResponse
import com.inveno.android.basics.service.third.network.ProgressListener
import okhttp3.*
import java.io.InputStream
import java.util.concurrent.TimeUnit

class OkHttpExecutor : HttpExecutor() {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(3 * 1000.toLong(), TimeUnit.MILLISECONDS)
        .readTimeout(3 * 1000.toLong(), TimeUnit.MILLISECONDS)
        .writeTimeout(3 * 1000.toLong(), TimeUnit.MILLISECONDS)
        .retryOnConnectionFailure(false)
        .addInterceptor{
            val request = it.request()
            val requestBuilder = request.newBuilder()
            val requestHeadBuilder = request.headers().newBuilder()
            requestHeadBuilder.addUnsafeNonAscii("user-agent",AppInfoHolder.getWebViewInfo().userAgent)
            requestBuilder.headers(requestHeadBuilder.build())
            it.proceed(requestBuilder.build())
        }
        .build()

    @Throws(Exception::class)
    override fun execute(
        method: String,
        url: String,
        type: String,
        data: ByteArray
    ) {
        var body: RequestBody? = null
        if (!TextUtils.isEmpty(type) && data != null) {
            body = RequestBody.create(MediaType.get(type), data)
        }
        val request = Request.Builder()
            .method(method, body)
            .url(url)
            .build()
        okHttpClient.newCall(request).execute()
    }

    @Throws(Exception::class)
    override fun executeForResult(
        method: String,
        url: String,
        type: String,
        data: ByteArray
    ): HttpResponse {
        var body: RequestBody? = null
        if (!TextUtils.isEmpty(type) && data != null) {
            body = RequestBody.create(MediaType.get(type), data)
        }
        val request = Request.Builder()
            .method(method, body)
            .url(url)
            .build()

        val response = okHttpClient.newCall(request).execute()
        return if (response.code() == 200) {
            val responseBody = response.body()
            HttpResponse(responseBody!!.bytes(), responseBody.contentType().toString())
        } else {
            throw Exception(response.code().toString())
        }
    }

    @Throws(Exception::class)
    fun executeForResult(
        method: String?,
        url: String?,
        body: RequestBody?
    ): HttpResponse {
        val request = Request.Builder()
            .method(method, body)
            .url(url)
            .build()
        val response = okHttpClient.newCall(request).execute()
        return if (response.code() == 200) {
            val responseBody = response.body()
            HttpResponse(responseBody!!.bytes(), responseBody.contentType().toString())
        } else {
            throw Exception(response.code().toString())
        }
    }

    @Throws(Exception::class)
    fun executeForSession(
        method: String?,
        url: String?,
        body: RequestBody?
    ): Session {
        val session =
            Session()
        val request = Request.Builder()
            .method(method, body)
            .url(url)
            .build()
        session.request = request
        session.call = okHttpClient.newCall(request)
        return session
    }

    @Throws(Exception::class)
    fun executeSessionForResult(session: Session): HttpResponse {
        val response = session.call!!.execute()
        session.response = response
        return if (response.code() == 200) {
            val responseBody = response.body()
            HttpResponse(responseBody!!.bytes(), responseBody.contentType().toString())
        } else {
            throw Exception(response.code().toString())
        }
    }

    @Throws(Exception::class)
    fun executeSessionWithListenerForStream(
        session: Session,
        progressListener: ProgressListener
    ): InputStream {
        val response = session.call!!.execute()
        session.response = response
        val responseBody: ResponseBody = ProgressResponseBody(response.body()!!, progressListener)
        return responseBody.byteStream()
    }

    @Throws(Exception::class)
    fun cancelSession(session: Session) {
        session.call!!.cancel()
    }

    class Session {
        var request: Request? = null
        var call: Call? = null
        var response: Response? = null
        @JvmField
        var userCancel = false

    }

}