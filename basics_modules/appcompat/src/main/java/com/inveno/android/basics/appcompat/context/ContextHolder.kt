package com.inveno.android.basics.appcompat.context

import android.content.Context

class ContextHolder {
    companion object {
        private lateinit var sAppContext : Context

        fun getAppContext():Context{
            return sAppContext
        }

        class Operate{
            companion object {
                fun setAppContext(appContext:Context){
                    sAppContext = appContext
                }
            }
        }
    }
}