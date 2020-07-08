package com.inveno.xiandu.apkupdata;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.inveno.android.basics.service.third.json.JsonUtil;
import com.inveno.android.device.param.provider.AndroidParamProviderHolder;
import com.inveno.xiandu.BuildConfig;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.Result;
import com.inveno.xiandu.bean.updata.UpdateInfo;
import com.inveno.xiandu.invenohttp.bacic_data.HttpUrl;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.FileUtils;
import com.inveno.xiandu.utils.SPUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.view.dialog.IosTypeDialog;
import com.inveno.xiandu.view.dialog.UpdataApkDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.PostRequest;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class UpdateApkManager {
    private static String savePath = "/sdcard/updateAPK/"; //apk保存到SD卡的路径
    public static int OKGO_DEFAUL_TIMEOUT = 60000;

    private static final int DOWNLOADING = 1; //表示正在下载
    private static final int DOWNLOADED = 2; //下载完毕
    private static final int DOWNLOAD_FAILED = 3; //下载失败

    private static boolean mIsDownload = false; //是否正在下载

    private int mProgress;
    private boolean mCancelFlag = false; //取消下载标志位
    private String mSaveFileName;//完整路径名
    private String mAppName, mVersionName;
    private UpdateInfo mUpdateInfo;
    private Context mContext;
    private Resources mRes;
    private AlertDialog mUpdateDialog;
    private ProgressDialog mProgressDialog;
    private UpdateListener mUpdateListener;
    private boolean isSettingCheck = false;// 如果是检查页面的检查，都要弹框，都要忽略更新的按钮
    private UpdataApkDialog updataApkDialog;

    private Intent updataServiceIntent;
    /**
     * 更新UI的handler
     */
    private MyHandle mHandler = new MyHandle(this);

    public static class Builder {
        Context mContext;
        UpdateApkManager mUpdateApkManager;

        public Builder(Context context) {
            this.mContext = context;
            mUpdateApkManager = new UpdateApkManager(context);
//            savePath = context.getFilesDir().getAbsolutePath() + "/updateAPK/";
        }

        public UpdateApkManager updateListener(UpdateListener listener) {
            mUpdateApkManager.setUpdateListener(listener);
            return mUpdateApkManager;
        }

        public UpdateApkManager setSettingCheck(boolean isSettingCheck) {
            mUpdateApkManager.setSettingCheck(isSettingCheck);
            return mUpdateApkManager;
        }

        public void update() {
            mUpdateApkManager.update();
        }
    }

    /**
     * 构造函数
     */
    public UpdateApkManager(Context context) {
        this.mContext = context;
        mRes = mContext.getResources();
    }

    /**
     * 显示更新对话框
     */
    public void showNoticeDialog(int type, String network) {

        String cancelStr = "";
        String updataStr = "";

        if (type != 1) {
            cancelStr = "下次再说";
        }
        //判断APK包是否存在
        File file = new File(mSaveFileName);
        if (file.exists()) {
            updataStr = "已下载，立即安装";
        } else {
            updataStr = "立即更新";
        }
        UpdataApkDialog.Builder builder = new UpdataApkDialog.Builder(mContext);
        builder.setTitle(mRes.getString(R.string.new_version) + mUpdateInfo.getVersion());
        builder.setContext(mUpdateInfo.getInstruction());
        builder.setCancelable(false);
        if (file.exists()) {
            builder.setNeedAdvice(false);
        } else {
            //wifi环境不需要提示wifi下载
            if (network.equals("1")) {
                builder.setNeedAdvice(false);
            } else {
                builder.setNeedAdvice(true);
            }
        }
        builder.setUpdataButtonListener(updataStr, new UpdataApkDialog.Builder.OnClickListener() {
            @Override
            public void onClick(View v) {
                updataApkDialog.dismiss();
                if (!file.exists()) {
                    showDownloadDialog();
                    if (mUpdateListener != null) {
                        mUpdateListener.acceptUpdate(null);
                    }
                } else {
                    //调用安装
                    installAPK();
                }
            }
        });
        builder.setCancelButtonListener(cancelStr, new UpdataApkDialog.Builder.OnClickListener() {
            @Override
            public void onClick(View v) {
                //记录更新版本号
                SPUtils.setInformain(mUpdateInfo.getVersion(), true);
                if (mUpdateListener != null) {
                    mUpdateListener.rejectUpdate(null);
                }
                updataApkDialog.dismiss();
            }
        });

        updataApkDialog = builder.create();
        updataApkDialog.show();
    }

    /**
     * 显示进度条对话框
     */
    public void showDownloadDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(mRes.getString(R.string.update_downloading));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(0);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        //下载apk
        downloadAPK();
    }

    /**
     * 下载apk的线程
     */
    public void downloadAPK() {
        Acp.getInstance(mContext).request(new AcpOptions.Builder()
                        .setPermissions(
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        .setDeniedMessage(mRes.getString(R.string
                                .read_permissions))
                        .setDeniedCloseBtn(mRes.getString(R.string.close))
                        .setDeniedSettingBtn(mRes.getString(R.string
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
                                    URL url = new URL(mUpdateInfo.getLink());
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

    static class MyHandle extends Handler {
        private WeakReference<UpdateApkManager> mUpdate;

        public MyHandle(UpdateApkManager updateApkManager) {
            mUpdate = new WeakReference<>(updateApkManager);
        }

        @Override
        public void handleMessage(Message msg) {
            UpdateApkManager updateApkManager = mUpdate.get();
            switch (msg.what) {
                case DOWNLOADING:
                    updateApkManager.mProgressDialog.setProgress(updateApkManager.mProgress);
                    break;
                case DOWNLOADED:
                    if (updateApkManager.mProgressDialog != null)
                        updateApkManager.mProgressDialog.dismiss();
                    mIsDownload = false;
                    updateApkManager.installAPK();
                    break;
                case DOWNLOAD_FAILED:
                    Toast.makeText(updateApkManager.mContext, updateApkManager.mRes.getString(R
                            .string.download_failure_check_net_zone), Toast.LENGTH_LONG).show();
                    if (updateApkManager.mUpdateListener != null) {
                        updateApkManager.mUpdateListener.rejectUpdate(updateApkManager.mRes
                                .getString(R.string.download_failed));
                    }
                    updateApkManager.mProgressDialog.dismiss();
                    mIsDownload = false;
                    break;
                default:
                    break;
            }
        }
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
            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID +
                    ".updatafileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd" +
                    ".android.package-archive");
        }
        mContext.startActivity(intent);
        //如果不加，最后不会提示完成、打开。但是加固的包不能加这行代码
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void update() {

        if (mIsDownload) {
            return;
        }
        String url = HttpUrl.getHttpUri(HttpUrl.UPDATA_URL);
        //如果使用默认的 60秒,以下三行也不需要传
        OkGo.getInstance().setConnectTimeout(OKGO_DEFAUL_TIMEOUT)  //全局的连接超时时间
                .setReadTimeOut(OKGO_DEFAUL_TIMEOUT)     //全局的读取超时时间
                .setWriteTimeOut(OKGO_DEFAUL_TIMEOUT);  //全局的写入超时时间
        PostRequest post = OkGo.post(url);

        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo info = null;
        try {
            info = packageManager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mVersionName = info.versionName;
        mAppName = info.packageName;
        JSONObject json = new JSONObject();
        try {
//            uid	YES	string	用户uid
//            referrer	YES	int	referrer值
//            version	YES	string	当前版本
            LinkedHashMap<String, Object> mParams = ServiceContext.bacicParamService().getBaseParam();
            json = new JSONObject(JsonUtil.Companion.toJson(mParams));
            json.put("referrer", BuildConfig.referrer);
            json.put("version", BuildConfig.VERSION_NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("updateapi", "jsonObject,s:" + json);
        post.upJson(json);
        post.execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.i("updateapi", "onSuccessresult,s:" + s);
//                upgrade	YES	int	是否需要升级,0:不需要(为0时，其他字段不下发)1:需要
//                instruction	YES	string	升级说明
//                link	YES	string	下载地址
//                type	YES	int	升级类型,1:强制升级2:非强制升级
//                version	YES	string	升级版本
//                compatible_version	YES	string	最小兼容版本
                mIsDownload = false;
//                s = "{\"code\":200,\"upack\":null,\"message\":\"OK\"," +
//                        "\"data\":{\"upgrade\":1,\"instruction\":\"测试升级弹窗\",\"version\":\"1.1.2\",\"link\":\"http://q12.majiang2018.com/7mmfxs.apk\"}," +
//                        "\"server_time\":1594018890746}";
                String dataStr = "";
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.has("data")) {
                        dataStr = jsonObject.getJSONObject("data").toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(dataStr) && !dataStr.equals("{}")) {
                    mUpdateInfo = JsonUtil.Companion.parseObject(dataStr, UpdateInfo.class);
//                    Result<UpdateInfo> result = Result.fromJson(dataStr, UpdateInfo.class);
//                    mUpdateInfo = result.getData();

                    mSaveFileName = savePath + mContext.getPackageName() + mUpdateInfo.getVersion() + ".apk";
                    // 检查网络环境，wifi的话，直接后台下载完后提醒
                    String network = AndroidParamProviderHolder.get().device().getNetwork();
                    if (isSettingCheck) {
                        // 如果是设置页的肯定直接弹出了
                        //检查本地是否有数据，直接弹出安装
                        showNoticeDialog(mUpdateInfo.getType(), network);
                    } else {
                        if (mUpdateInfo != null && mUpdateInfo.getUpgrade() == 1 && mUpdateInfo.getVersion().compareTo
                                (mVersionName) > 0 && !SPUtils.getInformain(mUpdateInfo.getVersion(), false)) {
                            //WiFi环境后台默认下载
                            if (network.equals("1")) {
                                //强制升级直接弹出
                                if (mUpdateInfo.getType() == 1) {
                                    showNoticeDialog(mUpdateInfo.getType(), network);
                                } else {
                                    //启动一个服务进行下载
                                    updataServiceIntent = new Intent(mContext, UpdateService.class);
                                    updataServiceIntent.putExtra("downloadUrl", mUpdateInfo.getLink());
                                    updataServiceIntent.putExtra("appVersion", mUpdateInfo.getVersion());
                                    updataServiceIntent.putExtra("instruction", mUpdateInfo.getInstruction());

                                    mContext.startService(updataServiceIntent);
                                }
                            } else {
                                //非WIFI环境，弹出提醒
                                showNoticeDialog(mUpdateInfo.getType(), network);
                            }
                        } else {
                            if (mUpdateListener != null) {
                                mUpdateListener.rejectUpdate(null);
                            }
                        }
                    }
                } else {
                    if (mUpdateListener != null) {
                        mUpdateListener.rejectUpdate(null);
                    }
                    if (isSettingCheck) {
                        Toaster.showToastCenterShort(mContext, "当前是最新版");
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                Log.i("updateapi", "onError,s:" + e);
                if (mUpdateListener != null) {
                    mUpdateListener.rejectUpdate(null);
                }
                mIsDownload = false;
            }
        });
    }

    public void stopService() {
        if (updataServiceIntent != null) {
            mContext.stopService(updataServiceIntent);
        }
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.mUpdateListener = updateListener;
    }

    public void setSettingCheck(boolean isSettingCheck) {
        this.isSettingCheck = isSettingCheck;
    }

    public interface UpdateListener {
        void acceptUpdate(String msg);

        void rejectUpdate(String errorMsg);
    }
}
