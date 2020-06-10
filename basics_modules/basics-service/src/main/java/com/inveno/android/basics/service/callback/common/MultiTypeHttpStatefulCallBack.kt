package com.inveno.android.basics.service.callback.common

import com.inveno.android.basics.service.callback.common.CommonHttpStatefulCallBack.Companion.newCallBack
import com.inveno.android.basics.service.third.json.JsonUtil
import java.lang.reflect.Type

object MultiTypeHttpStatefulCallBack {
    fun <T> newCallBack(type: Type): CommonHttpStatefulCallBack<T> {
        return newCallBack(MultiTypeDataParser(type))
    }

    fun <T> newCallBackOnRoot(type: Type): CommonHttpStatefulCallBack<T> {
        return newCallBack(MultiTypeDataParser<T>(type)).parseFromRoot()
    }

    private class MultiTypeDataParser<T>(private val type: Type) :
        ResponseDataParser<T> {
        override fun parse(jsonString: String): T {
            return JsonUtil.parseObject(jsonString, type)!!
        }

    }
}