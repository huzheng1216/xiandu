package com.inveno.android.basics.service.util

import android.os.Handler
import android.os.HandlerThread
import android.widget.Toast
import com.inveno.android.basics.appcompat.context.ContextHolder

class ThreadExceptionUtil {
    companion object{
        private val systemHandler = Thread.getDefaultUncaughtExceptionHandler()

        private val handlerThread : HandlerThread = HandlerThread("exception")

        fun init(){
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                executeExceptionReport(thread, throwable)
            }
            handlerThread.start()
        }

        private fun executeExceptionReport(thread:Thread, throwable:Throwable ){
            throwable.printStackTrace()
            Handler(handlerThread.looper).post {
                ReportUtil.reportException(throwable)
                Toast.makeText(ContextHolder.getAppContext(),"哎呀，出了点问题",Toast.LENGTH_SHORT).show()
                Thread.sleep(3000)
                systemHandler.uncaughtException(thread,throwable)
            }
        }
    }
}