package com.inveno.android.basics.service.third.json

import com.alibaba.fastjson.JSON
import java.lang.reflect.Type
import kotlin.reflect.KClass

class JsonUtil {
    companion object {
//        private val JSON_WORKER = JSON
        fun <T> parseObject(json:String, clazz: Class<T>):T?{
//            return MOSHI.adapter<T>(clazz).fromJson(json)
            return JSON.parseObject(json,clazz)
        }


//        fun <T> parseObject(json:String, clazz: JSONTypeReference<T>):T?{
////            return MOSHI.adapter<T>(clazz).fromJson(json)
//            return JSON.parseObject(json,clazz)
//        }

        fun <T> parseObject(json:String, clazz: Type):T?{
//            return MOSHI.adapter<T>(clazz).fromJson(json)
            return JSON.parseObject(json,clazz)
        }

        fun <T:Any> parseObject(json:String, clazz: KClass<T>):T?{
//            return MOSHI.adapter<T>(clazz).fromJson(json)
            return JSON.parseObject(json,clazz.javaObjectType)
        }

        fun <T> toJson(value:T, clazz: Class<T>):String{
            return JSON.toJSONString(value)
        }

        fun toJson(any:Any):String{
            return JSON.toJSONString(any)
        }
    }
}

