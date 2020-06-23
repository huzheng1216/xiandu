package com.inveno.android.basics.service.callback

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class BaseStatefulCallBack<B> : StatefulCallBack<B> {
    private var successCallBack: (data: B) -> Unit = {}
    private var failCallBack: ((code: Int, message: String) -> Unit)? = null

    fun invokeSuccess(data: B){
        GlobalScope.launch(Dispatchers.Main.immediate){
            try {
                successCallBack(data)
            }catch (e: Exception){
                invokeFail(900, e.message.toString())
            }
        }
    }

    fun invokeFail(code: Int, message: String){
        GlobalScope.launch(Dispatchers.Main.immediate){
            failCallBack?.let { it(code, message) }
        }
    }

    override fun onSuccess(successCallBack: (data: B) -> Unit): StatefulCallBack<B> {
        this.successCallBack = successCallBack
        return this
    }

    override fun onFail(failCallBack: (code: Int, message: String) -> Unit): StatefulCallBack<B> {
        this.failCallBack = failCallBack
        return this
    }

}

abstract class BaseProgressStatefulCallBack<B> : BaseStatefulCallBack<B>(),
    ProgressStatefulCallBack<B> {
    private lateinit var mProgressListener: (now: Long, all: Long) -> Unit
    override fun onProgressChange(progressListener: (now: Long, all: Long) -> Unit): ProgressStatefulCallBack<B> {
        this.mProgressListener = progressListener
        return this
    }

    fun invokeProgressChange(now: Long, all: Long){
        GlobalScope.launch(Dispatchers.Main.immediate){
            mProgressListener(now, all)
        }
    }
}