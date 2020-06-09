package com.inveno.android.basics.service.callback

interface StatefulCallBack<D> {
    fun onSuccess(successCallBack:(data:D)->Unit): StatefulCallBack<D>
    fun onFail(failCallBack:(code:Int,message:String)->Unit): StatefulCallBack<D>
    fun execute()
}

interface ProgressStatefulCallBack<D> : StatefulCallBack<D> {
    fun onProgressChange(progressListener:(now:Long,all:Long)->Unit): ProgressStatefulCallBack<D>
}