package com.inveno.android.basics.service.util

import org.junit.Test

class LogSupportUtilTest {

    @Test
    fun e() {
        val logContentBuilder = StringBuilder()
        repeat(3999){
            logContentBuilder.append("1")
        }
        logContentBuilder.append("2")

        repeat(3999){
            logContentBuilder.append("3")
        }
        logContentBuilder.append("4")
        LogSupportUtil.e("Test",logContentBuilder.toString())
        LogSupportUtil.e("Test","1234")
    }
}