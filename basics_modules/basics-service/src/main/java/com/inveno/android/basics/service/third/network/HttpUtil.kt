package com.inveno.android.basics.service.third.network

import com.inveno.android.basics.service.callback.BaseProgressStatefulCallBack
import com.inveno.android.basics.service.callback.BaseStatefulCallBack
import com.inveno.android.basics.service.callback.ProgressStatefulCallBack
import com.inveno.android.basics.service.callback.StatefulCallBack
import com.inveno.android.basics.service.io.Streams
import com.inveno.android.basics.service.third.json.JsonUtil
import com.inveno.android.basics.service.third.network.HttpUploadFileUtil.uploadFile
import com.inveno.android.basics.service.third.network.impl.OkHttpExecutor
import com.inveno.android.basics.service.third.network.param.FileUploadParam
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.util.*

object HttpUtil {
    private const val DEBUG = true
    private val logger = LoggerFactory.getLogger(HttpUtil::class.java)
    private val sExecutor: HttpExecutor = OkHttpExecutor()

    @JvmStatic
    fun postForm(
        url: String,
        args: LinkedHashMap<String, Any>
    ): StatefulCallBack<HttpResponse> {
        if (DEBUG) {
            synchronized(HttpUtil::class.java) {
                logger.info(
                        "postForm url:{} args:{} id:{}",
                        url,
                        args,
                        args.hashCode()
                )
            }
        }
        return object : BaseStatefulCallBack<HttpResponse>() {
            override fun execute() {
                GlobalScope.launch {
                    val contentBuilder = StringBuilder()
                    for ((key, value) in args) {
                        contentBuilder.append(key).append("=").append(
                                URLEncoder.encode(value.toString(),"utf-8"))
                            .append("&")
                    }
                    if (contentBuilder.length > 0) {
                        contentBuilder.setLength(contentBuilder.length - 1)
                    }
                    try {
                        val response =
                            sExecutor.executeForResult(
                                "POST",
                                url,
                                "application/x-www-form-urlencoded",
                                contentBuilder.toString().toByteArray()
                            )

                        if (DEBUG) {
                            synchronized(HttpUtil::class.java) {
                                logger.info(
                                        "response body:{},id:{}",
                                        String(response!!.data),
                                        args.hashCode()
                                )
                            }
                        }
                        if(response==null){
                            invokeFail(900, "response null")
                        }else{
                            invokeSuccess(response)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        logger.error(
                            "postForm to $url",
                            e
                        )
                        invokeFail(900, e.message!!)
                    }
                }
            }
        }
    }

    fun getSync(url: String):HttpResponse?{
        try {
            if (DEBUG) {
                synchronized(HttpUtil::class.java) {
                    logger.info(
                        "getSync request id:{}, url:{}",
                        url.hashCode(),
                        url
                    )
                }
            }
            val response =
                sExecutor.executeForResult(
                    "GET",
                    url,
                    "",
                    ByteArray(0)
                )
            if (DEBUG) {
                synchronized(HttpUtil::class.java) {
                    logger.info(
                        "getSync response id:{}",
                        url.hashCode()
                    )
                    logger.info(
                        "getSync response body:{}",
                        String(response!!.data)
                    )
                }
            }
            if(response==null){
                return null
            }else{
                return response
            }
        } catch (e: Exception) {
            logger.error(
                "get url:{},exception:{}",
                url,
                e.message
            )
            return null
            //                        BaseCallBackInvoker.get(this).onExceptionOccur(e);
        }
    }

    operator fun get(url: String): StatefulCallBack<HttpResponse> {
        return object : BaseStatefulCallBack<HttpResponse>() {
            override fun execute() {
                GlobalScope.launch{
                    try {
                        val response =
                            sExecutor.executeForResult(
                                "GET",
                                url,
                                "",
                                ByteArray(0)
                            )
                        if (DEBUG) {
                            synchronized(HttpUtil::class.java) {
                                logger.info(
                                    "get response id:{}",
                                    url.hashCode()
                                )
                                logger.info(
                                    "get response body:{}",
                                    String(response!!.data)
                                )
                            }
                        }
                        if(response==null){
                            invokeFail(900, "response null")
                        }else{
                            invokeSuccess(response)
                        }
                    } catch (e: Exception) {
                        logger.error(
                            "get url:{},exception:{}",
                            url,
                            e.message
                        )
                        invokeFail(900, e.message!!)
                        //                        BaseCallBackInvoker.get(this).onExceptionOccur(e);
                    }
                }
            }
        }
    }

    @JvmStatic
    fun postJson(
        url: String,
        args: LinkedHashMap<String, Any>
    ): StatefulCallBack<HttpResponse> {
        if (DEBUG) {
            synchronized(HttpUtil::class.java) {
                logger.info(
                    "postJson request id:{}",
                    args.hashCode()
                )
                logger.info(
                    "postJson request url:{}",
                    url
                )
                logger.info(
                    "postJson request args:{}",
                    JsonUtil.toJson(args)
                )
            }
        }
        return object : BaseStatefulCallBack<HttpResponse>() {
            override fun execute() {
                GlobalScope.launch {
                    try {
                        val requestContent: ByteArray =
                            JsonUtil.toJson(args).toByteArray()
                        val response =
                            sExecutor.executeForResult(
                                "POST",
                                url,
                                "application/json;encode=utf-8",
                                requestContent
                            )
                        if (DEBUG) {
                            synchronized(HttpUtil::class.java) {
                                logger.info(
                                    "postJson response id:{}",
                                    args.hashCode()
                                )
                                logger.info(
                                    "postJson response body:{}",
                                    String(response!!.data),
                                    true
                                )
                            }
                        }
                        if(response==null){
                            invokeFail(900, "response null")
                        }else{
                            invokeSuccess(response)
                        }
                    } catch (e: Exception) {
                        logger.error(
                            "postJson exception url: " + url + " type:" + e.message,
                            e
                        )
                        invokeFail(900, e.message!!)
                    }
                }
            }
        }
    }

    @JvmStatic
    fun postJsonNow(
        url: String,
        args: LinkedHashMap<String, Any>
    ): HttpResponse?{
        try {
            val requestContent: ByteArray =
                JsonUtil.toJson(args).toByteArray()
            val response =
                sExecutor.executeForResult(
                    "POST",
                    url,
                    "application/json;encode=utf-8",
                    requestContent
                )
            if (DEBUG) {
                synchronized(HttpUtil::class.java) {
                    logger.info(
                        "postJson response id:{}",
                        args.hashCode()
                    )
                    logger.info(
                        "postJson response body:{}",
                        String(response!!.data),
                        true
                    )
                }
            }
            if(response==null){
                return null
            }else{
                return response
            }
        } catch (e: Exception) {
            logger.error(
                "postJson exception url: " + url + " type:" + e.message,
                e
            )
            return null
        }
    }

    fun uploadFile(
        url: String,
        args: FileUploadParam,
        progressListener: ProgressListener?
    ): StatefulCallBack<HttpResponse> {
        return uploadFile(true, url!!, args, progressListener!!)
    }

    fun downloadFile(
        url: String,
        tempFile:String,
        targetFile:String
    ): ProgressStatefulCallBack<String> {
        return object : BaseProgressStatefulCallBack<String>() {
            override fun execute() {
                GlobalScope.launch {
                    val okHttpExecutor =
                        executor
                    try {
                            val session =
                                okHttpExecutor.executeForSession("GET", url, null)
                            val inputStream =
                                okHttpExecutor.executeSessionWithListenerForStream(session,object :ProgressListener{
                                    override fun update(
                                        bytesRead: Long,
                                        contentLength: Long,
                                        done: Boolean
                                    ) {
                                        invokeProgressChange(bytesRead,contentLength)
                                    }

                                })
                            val apkTempFile = File(tempFile)
                            if (!apkTempFile.exists()) {
                                apkTempFile.createNewFile()
                            }
                            val fileOutputStream =
                                FileOutputStream(apkTempFile)
                            Streams.copy(inputStream, fileOutputStream)
                            inputStream.close()
                            fileOutputStream.close()
                            apkTempFile.renameTo(File(targetFile))
                            invokeSuccess(targetFile)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        logger.error(
                            "downloadFile url:{},exception:{}",
                            url,
                            e.message
                        )
                        invokeFail(900, e.message!!)
                    }
                }
            }
        }
    }

    val executor: OkHttpExecutor
        get() = sExecutor as OkHttpExecutor

    object Inner {
        fun uploadFileThisThread(
            url: String,
            args: FileUploadParam,
            progressListener: ProgressListener?
        ): StatefulCallBack<HttpResponse> {
            return uploadFile(false, url!!, args, progressListener!!)
        }
    }
}