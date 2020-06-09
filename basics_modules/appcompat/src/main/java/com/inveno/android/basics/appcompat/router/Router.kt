package com.inveno.android.basics.appcompat.router

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RouteTarget(var mClass: Class<out Activity>) {

}

class Router {

    companion object{
        val logger : Logger = LoggerFactory.getLogger(Router::class.java)
    }

    private val routeTargetMap = mutableMapOf<String, RouteTarget>()

    private val routeWatcherList = mutableListOf<RouteWatcher>()

    fun <T:Activity>register(name:String,cls:Class<T>){
        routeTargetMap[name] = RouteTarget(cls)
    }

    fun addWatcher(routeWatcher: RouteWatcher){
        routeWatcherList.add(routeWatcher)
    }

    fun go(fromActivity:Activity,path:String){
        logger.info("go url:$path")
        routeWatcherList.forEach {
            it.onPageRoute(fromActivity, path)
        }
        val intent = Intent(fromActivity,routeTargetMap[path]!!.mClass)
        fromActivity.startActivity(intent)
    }

    fun goInstall(fromActivity:Activity,uriString:String){
        routeWatcherList.forEach {
            it.onPageRoute(fromActivity, "install?uri:$uriString")
        }
        val intent =  Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(Uri.parse(uriString), "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse(uriString), "application/vnd.android.package-archive");
        }
        fromActivity.startActivity(intent);
    }

    fun goWithMap(fromActivity:Activity,path:String,map:Map<String,Any>){
        routeWatcherList.forEach {
            it.onPageRoute(fromActivity, "$path @$map")
        }
        val intent = Intent(fromActivity,routeTargetMap[path]!!.mClass)
        map.forEach{
            if(it.value is Parcelable){
                intent.putExtra(it.key,it.value as Parcelable)
            }
        }
        fromActivity.startActivity(intent)
    }

    fun goViewVideo(fromActivity:Activity,uriString:String){
        routeWatcherList.forEach {
            it.onPageRoute(fromActivity, "view_video uri:$uriString")
        }
        //实现播放视频的跳转逻辑(调用原生视频播放器)
        val intent = Intent(Intent.ACTION_VIEW)
        val uri =
            Uri.parse(uriString)
        intent.setDataAndType(uri, "video/*")
        //给所有符合跳转条件的应用授权
        //给所有符合跳转条件的应用授权
        val resInfoList: List<ResolveInfo> = fromActivity.getPackageManager()
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            fromActivity.grantUriPermission(
                packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
        fromActivity.startActivity(intent)
    }
}