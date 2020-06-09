package com.inveno.android.basics.service.util

import android.util.Log

object LogSupportUtil {
    fun e(tag: String?, content: String) {
        var content = content
        content = content.trim { it <= ' ' }
        var index = 0
        val maxLength = 4000
        var sub: String
        while (index < content.length) {
            // java的字符不允许指定超过总的长度end
            sub = if (content.length <= index + maxLength) {
                content.substring(index)
            } else {
                content.substring(index, index + maxLength)
            }
            index += maxLength
            Log.e(tag, sub.trim { it <= ' ' })
        }
    }
}
