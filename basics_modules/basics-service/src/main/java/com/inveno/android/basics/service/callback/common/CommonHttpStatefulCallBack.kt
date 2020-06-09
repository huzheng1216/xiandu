package com.inveno.android.basics.service.callback.common

import com.alibaba.fastjson.JSONObject
import com.inveno.android.basics.service.callback.BaseStatefulCallBack
import com.inveno.android.basics.service.callback.StatefulCallBack
import com.inveno.android.basics.service.third.json.JsonUtil
import com.inveno.android.basics.service.third.network.HttpResponse
import com.inveno.android.basics.service.third.network.HttpUtil
import java.util.*

class CommonHttpStatefulCallBack<T> {
    private var url: String? = null
    private var args: LinkedHashMap<String, Any>? = null
    private var callerCallBack: BaseStatefulCallBack<T>? = null
    private var httpCallBack: StatefulCallBack<HttpResponse>? = null
    private var responseDataParser: ResponseDataParser<T>
    private var parseFromRoot = false
    private var originType = true

    private constructor(tClass: Class<T>) {
        responseDataParser = ExactlyResponseDataParser(tClass)
    }

    constructor(responseDataParser: ResponseDataParser<T>) {
        this.responseDataParser = responseDataParser
    }

    fun atUrl(url: String): CommonHttpStatefulCallBack<T> {
        this.url = url
        return this
    }

    fun withArg(args: LinkedHashMap<String, Any>): CommonHttpStatefulCallBack<T> {
        this.args = args
        return this
    }

    fun parseFromRoot(): CommonHttpStatefulCallBack<T> {
        parseFromRoot = true
        return this
    }

    fun originType(): CommonHttpStatefulCallBack<T> {
        originType = true
        return this
    }

    fun buildCallerCallBack(): StatefulCallBack<T> {
        httpCallBack = HttpUtil.postJson(url!!, args!!)
        callerCallBack = object : BaseStatefulCallBack<T>() {
            override fun execute() {
                httpCallBack!!.execute()
            }
        }
        httpCallBack!!.onSuccess { httpResponse: HttpResponse ->
            val response =
                JSONObject.parseObject(String(httpResponse.data))
            if (response.getIntValue("code") == 200) {
                if (parseFromRoot) {
                    callerCallBack!!.invokeSuccess(
                        responseDataParser.parse(response.toJSONString())
                    )
                } else {
                    callerCallBack!!.invokeSuccess(
                        responseDataParser.parse(getDataString(response))
                    )
                }
            } else {
                callerCallBack!!.invokeFail(
                    response.getIntValue("code"),
                    response.getString("msg")
                )
            }
            null
        }
        httpCallBack!!.onFail { integer: Int?, s: String? ->
            callerCallBack!!.invokeFail(integer!!, s!!)
            null
        }
        return callerCallBack!!
    }

    private fun getDataString(jsonObject: JSONObject): String {
        return try {
            if (originType) {
                jsonObject.getString("data")
            } else {
                jsonObject.getJSONObject("data").toJSONString()
            }
        } catch (e: Exception) {
            "{}"
        }
    }

    private class ExactlyResponseDataParser<T>(private val tClass: Class<T>) :
        ResponseDataParser<T> {
        override fun parse(jsonString: String): T {
            return JsonUtil.parseObject(jsonString, tClass)!!
        }

    }

    companion object {
        fun <T> newCallBack(cls: Class<T>): CommonHttpStatefulCallBack<T> {
            return CommonHttpStatefulCallBack(cls)
        }

        @JvmStatic
        fun <T> newCallBack(responseDataParser: ResponseDataParser<T>): CommonHttpStatefulCallBack<T> {
            return CommonHttpStatefulCallBack(responseDataParser)
        }
    }
}