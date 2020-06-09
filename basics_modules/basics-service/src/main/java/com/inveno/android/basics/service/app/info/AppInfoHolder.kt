package com.inveno.android.basics.service.app.info

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.webkit.WebView

class AppInfoHolder {
    companion object{
        private lateinit var appInfo:AppInfo

        private lateinit var webViewInfo: AppWebViewInfo
        fun install(context:Context){
            val packageName = context.packageName
            val packageInfo = context.packageManager.getPackageInfo(packageName,0)!!
            appInfo = AppInfo(context.packageName,
                getSafeVersionCode(packageInfo),
                packageInfo.versionName)

            val webView = WebView(context)
            webViewInfo = AppWebViewInfo(webView.settings.userAgentString)
            webView.removeAllViews()
            webView.destroy()
        }

        fun getAppInfo():AppInfo{
            return appInfo
        }

        fun getWebViewInfo():AppWebViewInfo{
            return webViewInfo
        }

        private fun getSafeVersionCode(packageInfo: PackageInfo):Long{
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    return  packageInfo.longVersionCode
                }else{
                    return packageInfo.versionCode.toLong()
                }
            }catch (e:Exception){
                return packageInfo.versionCode.toLong()
            }
        }
    }
}