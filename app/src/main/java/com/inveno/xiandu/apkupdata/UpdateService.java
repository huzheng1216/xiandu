package com.inveno.xiandu.apkupdata;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.inveno.xiandu.BuildConfig;
import com.inveno.xiandu.R;
import com.inveno.xiandu.utils.ActivityManager;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.dialog.UpdataApkDialog;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @author yongji.wang
 * @date 2020/7/6 19:56
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class UpdateService extends Service {
    private static String savePath = "/sdcard/updateAPK/"; //apk保存到SD卡的路径
    private static final int DOWNLOADING = 1; //表示正在下载
    private static final int DOWNLOADED = 2; //下载完毕
    private static final int DOWNLOAD_FAILED = 3; //下载失败

    private static boolean mIsDownload = false; //是否正在下载
    private int mProgress;//下载进度
    private boolean mCancelFlag = false; //取消下载标志位

    private String downloadUrl = "";//下载地址
    private String updataVersion = "";//升级版本号
    private String instruction = ""; //升级信息
    private String mSaveFileName;
    private UpdataApkDialog updataApkDialog;

    /**
     * 更新UI的handler
     */
    private MyHandle mHandler = new MyHandle(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadUrl = intent.getStringExtra("downloadUrl");
        updataVersion = intent.getStringExtra("appVersion");
        instruction = intent.getStringExtra("instruction");

        mSaveFileName = savePath + getPackageName() + updataVersion + ".apk";
        updata();
        return super.onStartCommand(intent, flags, startId);
    }


    private void updata() {
        //应用更新提示
        File file = new File(mSaveFileName);
        if (!file.exists()) {
            downloadAPK();
        } else {
            showNotifiDialog();
        }
    }


    /**
     * 下载apk的线程
     */
    public void downloadAPK() {
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        .setDeniedMessage(getResources().getString(R.string
                                .read_permissions))
                        .setDeniedCloseBtn(getResources().getString(R.string.close))
                        .setDeniedSettingBtn(getResources().getString(R.string
                                .goto_setting))
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mIsDownload = true;
                                    URL url = new URL(downloadUrl);
                                    HttpURLConnection conn = (HttpURLConnection) url
                                            .openConnection();
                                    conn.connect();

                                    int length = conn.getContentLength();
                                    InputStream is = conn.getInputStream();

                                    File file = new File(savePath);
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    }
                                    String apkFile = mSaveFileName;
                                    File ApkFile = new File(apkFile);
                                    FileOutputStream fos = new FileOutputStream(ApkFile);

                                    int count = 0;
                                    byte buf[] = new byte[1024];

                                    do {
                                        int numRead = is.read(buf);
                                        count += numRead;
                                        mProgress = (int) (((float) count / length) * 100);
                                        //更新进度
                                        mHandler.sendEmptyMessage(DOWNLOADING);
                                        if (numRead <= 0) {
                                            //下载完成通知安装
                                            mHandler.sendEmptyMessage(DOWNLOADED);
                                            break;
                                        }
                                        fos.write(buf, 0, numRead);
                                    }
                                    while (!mCancelFlag); //点击取消就停止下载.

                                    fos.close();
                                    is.close();
                                } catch (Exception e) {
                                    mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
                        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    static class MyHandle extends Handler {
        private WeakReference<UpdateService> mUpdate;

        public MyHandle(UpdateService updateService) {
            mUpdate = new WeakReference<>(updateService);
        }

        @Override
        public void handleMessage(Message msg) {
            UpdateService updateService = mUpdate.get();
            switch (msg.what) {
                case DOWNLOADING:
                    LogUtils.H("下载进度：" + updateService.mProgress);
                    break;
                case DOWNLOADED:
                    mIsDownload = false;
                    updateService.showNotifiDialog();
                    break;
                case DOWNLOAD_FAILED:
                    mIsDownload = false;
                    break;
                default:
                    break;
            }
        }
    }


    public void showNotifiDialog() {
        // TODO: 2020/7/7 这里应该弹窗提醒安装
        UpdataApkDialog.Builder builder = new UpdataApkDialog.Builder(ActivityManager.getAppManager().currentActivity());
        builder.setTitle(getResources().getString(R.string.new_version) + updataVersion);
        builder.setContext(instruction);
        builder.setCancelable(false);
        builder.setNeedAdvice(false);
        builder.setUpdataButtonListener("已下载，立即安装", new UpdataApkDialog.Builder.OnClickListener() {
            @Override
            public void onClick(View v) {
                updataApkDialog.dismiss();
                //调用安装
                installAPK();
            }
        });
        builder.setCancelButtonListener("下次再说", new UpdataApkDialog.Builder.OnClickListener() {
            @Override
            public void onClick(View v) {
                //记录更新版本号
                SPUtils.setInformain(updataVersion, true);
                updataApkDialog.dismiss();
            }
        });

        updataApkDialog = builder.create();
        updataApkDialog.show();
    }

    /**
     * 下载完成后自动安装apk
     */
    public void installAPK() {
        File apkFile = new File(mSaveFileName);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent
                    .FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID +
                    ".updatafileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd" +
                    ".android.package-archive");
        }
        startActivity(intent);
        //如果不加，最后不会提示完成、打开。但是加固的包不能加这行代码
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
