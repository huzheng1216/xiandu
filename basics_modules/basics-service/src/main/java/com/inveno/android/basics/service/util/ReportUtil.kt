package com.inveno.android.basics.service.util

import android.util.Base64
import com.inveno.android.basics.service.app.info.AppInfoHolder
import com.inveno.android.basics.service.third.network.HttpUtil
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

class ReportUtil {
    companion object{
        fun reportLog(log:String){
            val json = getBaseReportJson()
            json.put("message",log)
            HttpUtil.postJson("https://heyworld.online/data/log",json).execute()
        }

        fun reportLog(data:Any){
            val json = getBaseReportJson()
            json.put("data",data)
            HttpUtil.postJson("https://heyworld.online/data/log",json).execute()
        }

        fun reportException(e:Throwable) {
            val json = getBaseReportJson()
            json.put("message","exception:"+e.message)
            val stringWriter = StringWriter()
            e.printStackTrace(PrintWriter(stringWriter))
            json.put("stack_trace",Base64.encodeToString(stringWriter.toString().toByteArray(),Base64.NO_WRAP))
            HttpUtil.postJsonNow("https://heyworld.online/data/log",json)
        }

        private fun getBaseReportJson(): LinkedHashMap<String, Any> {
            val json = LinkedHashMap<String, Any>()
            json.put("app",AppInfoHolder.getAppInfo().packageName)
            json.put("version",AppInfoHolder.getAppInfo().versionName)
            return json
        }
    }
}