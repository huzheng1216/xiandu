package com.inveno.xiandu.invenohttp.api.user;

import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.CommonHttpStatefulCallBack;
import com.inveno.android.basics.service.callback.common.DefaultHttpStatefulCallBack;
import com.inveno.android.basics.service.callback.common.ResponseDataParser;
import com.inveno.android.basics.service.thread.ThreadUtil;
import com.inveno.xiandu.invenohttp.bacic_data.HttpUrl;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * @author yongji.wang
 * @date 2020/6/8 16:14
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class VaricationCodeAPI extends BaseSingleInstanceService {

    protected static final boolean MODULE_DEBUG = true;

    public StatefulCallBack<String> getVaricationCode(String phoneNum, String type) {
        if (MODULE_DEBUG) {
            LinkedHashMap<String, Object> getCodeData = ServiceContext.bacicParamService().getBaseParam();
            LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
            mParams.put("phone_num", phoneNum);
            mParams.put("type", type);
            mParams.putAll(getCodeData);
            return CommonHttpStatefulCallBack.Companion.newCallBack(new DefaultStringResponse())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_CODE))
                    .withArg(mParams)
                    .buildCallerCallBack();
        } else {
            LinkedHashMap<String, Object> getCodeData = ServiceContext.bacicParamService().getBaseParam();
            LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
            mParams.put("phone_num", phoneNum);
            mParams.put("type", type);
            mParams.putAll(getCodeData);
            return DefaultHttpStatefulCallBack.INSTANCE
                    .newCallBack()
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_CODE))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }

    public static class DefaultStringResponse implements ResponseDataParser<String> {
        @Override
        public String parse(@NotNull String jsonString) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                return jsonObject.getString("phoneCode");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "ok";
        }
    }
}
