package com.inveno.xiandu.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.inveno.xiandu.BuildConfig;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xu.li<AthenaLightenedMyPath@gmail.com> on 9/1/15.
 */
public class EntryActivity extends Activity implements IWXAPIEventHandler {

    public static boolean isAuth = false;
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, BuildConfig.WeChatAppID);

        Log.i("iiii-EntryActivity", "启动EntryActivity");
        if (api == null) {
            startMainActivity();
        } else {
            api.handleIntent(getIntent(), this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("iiiii-EntryActivity", "onNewIntent");
        setIntent(intent);

        if (api == null) {
            startMainActivity();
        } else {
            api.handleIntent(intent, this);
        }
    }

    @Override
    public void onResp(BaseResp resp) {

        Log.i("iiii-EntryActivity", resp.toString());

        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                Log.i("iiii-EntryActivity", "onResp: 分享成功");

                if (isAuth) {
                    isAuth = false;

                } else {
                    Toast.makeText(EntryActivity.this, "分享成功", Toast
                            .LENGTH_SHORT).show();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                Log.i("iiii-EntryActivity", "onResp: 分享取消");

                Toast.makeText(EntryActivity.this, "分享取消", Toast
                        .LENGTH_SHORT).show();

                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //分享拒绝
                Log.i("iiii-EntryActivity", "onResp: 分享拒绝");

                Toast.makeText(EntryActivity.this, "分享取消", Toast
                        .LENGTH_SHORT).show();

                break;

            default:
                break;

        }

        finish();
    }

    @Override
    public void onReq(BaseReq req) {

        finish();
    }

    /**
     * 获取微信appid
     */
    protected String getAppId() throws PackageManager.NameNotFoundException {
        String appId = null;
        ApplicationInfo appInfo = this.getPackageManager()
                .getApplicationInfo(this.getPackageName(), PackageManager
                        .GET_META_DATA);
        appId = appInfo.metaData.getString("WEICHAT_APPKEY");
        return appId;
    }

    protected void startMainActivity() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(getApplicationContext().getPackageName());
        getApplicationContext().startActivity(intent);
    }


}
