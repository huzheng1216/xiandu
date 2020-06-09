package com.inveno.android.basics.appcompat.router

import android.app.Activity

interface RouteWatcher {
    fun onPageRoute(fromActivity: Activity,path:String)
}