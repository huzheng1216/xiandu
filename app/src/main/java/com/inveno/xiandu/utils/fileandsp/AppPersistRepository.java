package com.inveno.xiandu.utils.fileandsp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.util.Consumer;

import com.inveno.android.basics.appcompat.context.ContextHolder;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.app.context.InstanceContext;
import com.inveno.android.basics.service.thread.ThreadUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class AppPersistRepository extends BaseSingleInstanceService {

    public static AppPersistRepository get() {
        return InstanceContext.get().getInstance(AppPersistRepository.class);
    }

    private SharedPreferences sharedPreferences;
    private Map<String, SharedPreferences> sharedPreferenceMap = new HashMap<>();
    private File mPersistRootFile;
    private File mShareRootFile;

    @Override
    protected void onCreate() {
        install(ContextHolder.Companion.getAppContext());
        super.onCreate();
    }

    public void install(Context context) {
        sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE);
        mPersistRootFile = new File(context.getExternalFilesDir(null), "persist");
        mShareRootFile = new File(context.getExternalFilesDir(null), "Share");
        ensureRootFileExist();
    }

    public void save(String key, String value) {
        ThreadUtil.execOnIo(new Runnable() {
            @Override
            public void run() {
                sharedPreferences.edit().putString(key, value).commit();
            }
        });
    }

    public String get(String key) {
        return sharedPreferences.getString(key, "");
    }


    public void saveTo(String name, String key, String value) {
        ThreadUtil.execOnIo(new Runnable() {
            @Override
            public void run() {
                ensure(name).edit().putString(key, value).commit();
            }
        });
    }

    private void ensureRootFileExist() {
        if (!mPersistRootFile.exists()) {
            mPersistRootFile.mkdirs();
        }
    }

//    public void saveToFile(String fileName, String value) {
//        saveToFile(fileName, value.getBytes());
//    }
//
//    public void saveToFile(String fileName, byte[] value) {
//        ThreadUtil.execOnIo(new Runnable() {
//            @Override
//            public void run() {
//                ensureRootFileExist();
//                try {
//                    Files.write(value, new File(mPersistRootFile, fileName));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    public void readFromFile(String fileName, Consumer<String> callBack) {
        ThreadUtil.execOnIo(new Runnable() {
            @Override
            public void run() {
                ensureRootFileExist();
                try {
                    File file = new File(mPersistRootFile, fileName);
                    if (!file.exists()) {
                        callBack.accept(null);
                        return;
                    }
                    byte[] content = new byte[(int) file.length()];
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        fileInputStream.read(content);
                    }
                    callBack.accept(new String(content));
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.accept(null);
                }
            }
        });
    }

    public String getFrom(String name, String key) {
        return ensure(name).getString(key, "");
    }

    private SharedPreferences ensure(String name) {
        SharedPreferences sharedPreference = sharedPreferenceMap.get(name);
        if (sharedPreference == null) {
            sharedPreference = ContextHolder.Companion.getAppContext().getSharedPreferences(name, Context.MODE_PRIVATE);
            sharedPreferenceMap.put(name, sharedPreference);
        }
        return sharedPreference;
    }

    public File getDir(String name) {
        File dir = new File(mPersistRootFile, name);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public File getSdcardDir(String name) {
        File dir = new File("/sdcard", name);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public File getShareDir(String name) {
        File dir = new File(mShareRootFile, name);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
