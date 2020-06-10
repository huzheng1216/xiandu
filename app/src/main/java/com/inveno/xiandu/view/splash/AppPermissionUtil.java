package com.inveno.xiandu.view.splash;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 权限申请工具
 * Created by yunlong.yang on 2018/5/7.
 */

public class AppPermissionUtil {

    private static final List<String> PERMISSION_LIST = Arrays.asList(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE);

    private static int REQUEST_CODE = 1000;

    public static void checkPermission(Activity activity, Runnable actionWhenAllPermissionGet, boolean needReq){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> PERMISSION_LOSS_LIST = new ArrayList<>();
            for (String permission : PERMISSION_LIST) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    PERMISSION_LOSS_LIST.add(permission);
                }
            }
            if (!PERMISSION_LOSS_LIST.isEmpty()) {
                if(!needReq) return;
                String[] permissionArray = new String[PERMISSION_LOSS_LIST.size()];
                PERMISSION_LOSS_LIST.toArray(permissionArray);
                ActivityCompat.requestPermissions(activity, permissionArray, REQUEST_CODE = new Random().nextInt(10000));
            } else {
                actionWhenAllPermissionGet.run();
            }
        }else{
            List<String> PERMISSION_LOSS_LIST = new ArrayList<>();
            for (String permission : PERMISSION_LIST) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    PERMISSION_LOSS_LIST.add(permission);
                }
            }
            if (!PERMISSION_LOSS_LIST.isEmpty()) {
                Log.i("AppPermissionUtil","权限不足："+PERMISSION_LOSS_LIST);
            }else{
                Log.i("AppPermissionUtil","权限都有");
            }
            actionWhenAllPermissionGet.run();
        }

    }

    public static boolean onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(REQUEST_CODE == requestCode){
            for(int grantResult: grantResults){
                if(grantResult!= PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            return true;
        }else{
            return true;
        }
    }
}
