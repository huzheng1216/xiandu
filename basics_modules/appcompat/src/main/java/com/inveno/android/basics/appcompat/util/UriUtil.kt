package com.inveno.android.basics.appcompat.util

import android.content.Context
import android.net.Uri

class UriUtil {
    companion object{
        fun toPath( context: Context, uri: Uri):String {
            var path = ""
            val cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor?.let {
                if (it.moveToFirst()) {
                    try {
                        path = cursor.getString(cursor.getColumnIndex("_data"));
                    } catch (e:Exception) {
                        e.printStackTrace();
                    }
                }
                it.close();
            }
            return path;
        }
    }


}