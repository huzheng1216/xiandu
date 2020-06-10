package com.inveno.android.basics.service.third.network

import android.net.Uri
import com.inveno.android.basics.appcompat.context.ContextHolder.Companion.getAppContext
import com.inveno.android.basics.service.callback.BaseStatefulCallBack
import com.inveno.android.basics.service.callback.StatefulCallBack
import com.inveno.android.basics.service.third.json.JsonUtil.Companion.toJson
import com.inveno.android.basics.service.third.network.impl.OkHttpExecutor
import com.inveno.android.basics.service.third.network.impl.ProgressRequestBody
import com.inveno.android.basics.service.third.network.impl.okhttp.UriRequestBody
import com.inveno.android.basics.service.third.network.param.FileUploadParam
import okhttp3.MediaType
import okhttp3.MultipartBody
import org.slf4j.LoggerFactory

internal object HttpUploadFileUtil {
    private const val DEBUG = true
    private val logger =
        LoggerFactory.getLogger(HttpUtil::class.java)

    @JvmStatic
    fun uploadFile(
        needThread: Boolean,
        url: String,
        args: FileUploadParam,
        progressListener: ProgressListener
    ): StatefulCallBack<HttpResponse> {
        if (DEBUG) {
            synchronized(HttpUtil::class.java) {
                logger.info("uploadFile request id:{}", args.hashCode())
                logger.info("uploadFile request url:{}", url)
                logger.info(
                    "uploadFile request args:{}",
                    toJson(args)
                )
            }
        }
        return object : BaseStatefulCallBack<HttpResponse>() {
            private var session: OkHttpExecutor.Session? =
                null

            override fun execute() {
                val me: BaseStatefulCallBack<HttpResponse> = this
                if (needThread) {
                    Runnable {
                        session =
                            executeTask(url, args, progressListener, me)
                    }
                } else {
                    session = executeTask(url, args, progressListener, me)
                }
            }
        }
    }

    private fun executeTask(
        url: String,
        fileUploadParam: FileUploadParam,
        progressListener: ProgressListener,
        me: BaseStatefulCallBack<HttpResponse>
    ): OkHttpExecutor.Session? {
        var session: OkHttpExecutor.Session? =
            null
        try {
            val okHttpExecutor =
                HttpUtil.executor
            val mimeType = fileUploadParam.mimeType
            val requestBodyBuilder = MultipartBody.Builder()
            //            for(Map.Entry<String,Object> entry : args.entrySet()){
//                requestBodyBuilder.addFormDataPart(entry.getKey(),entry.getValue().toString());
//            }
            requestBodyBuilder.addFormDataPart("content_type", fileUploadParam.fileType.toString())
            requestBodyBuilder.addFormDataPart(
                fileUploadParam.fileKey, System.currentTimeMillis().toString(),
                UriRequestBody.createUriRequestBody(
                    getAppContext(),
                    Uri.parse(fileUploadParam.uri),
                    MediaType.get(mimeType)
                )
            )
            session = okHttpExecutor.executeForSession(
                "POST",
                url,
                ProgressRequestBody(requestBodyBuilder.build(), progressListener)
            )
            val httpResponse = okHttpExecutor.executeSessionForResult(session)
            if (DEBUG) {
                synchronized(HttpUtil::class.java) {
                    logger.info(
                        "uploadFile response id:{}",
                        fileUploadParam.hashCode()
                    )
                    logger.info(
                        "uploadFile response body:{}",
                        String(httpResponse.data),
                        true
                    )
                }
            }
            me.invokeSuccess(httpResponse)
        } catch (e: Exception) {
            logger.error(
                "uploadFile exception url: " + url + " type:" + e.message,
                e
            )
            if (!session!!.userCancel) {
                me.invokeFail(900, "网络错误")
            } else {
                me.invokeFail(800, "用户取消")
            }
        }
        return session
    }
}