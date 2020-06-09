package com.inveno.android.basics.service.event

interface EventListener {
    fun onEvent(name:String,arg:String)
}