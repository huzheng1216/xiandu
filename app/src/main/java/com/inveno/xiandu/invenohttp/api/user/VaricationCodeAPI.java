package com.inveno.xiandu.invenohttp.api.user;

import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.DefaultHttpStatefulCallBack;
import com.inveno.android.basics.service.thread.ThreadUtil;
import com.inveno.xiandu.invenohttp.bacic_data.HttpUrl;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;

import java.util.LinkedHashMap;

/**
 * @author yongji.wang
 * @date 2020/6/8 16:14
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class VaricationCodeAPI extends BaseSingleInstanceService {

    protected static final boolean MODULE_DEBUG = false;

    public StatefulCallBack<String> getVaricationCode(String phoneNum, String type) {
        if (MODULE_DEBUG) {
            return new BaseStatefulCallBack<String>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            invokeSuccess("");
                        }
                    });
                }
            };
        } else {
            LinkedHashMap<String, Object> getCodeData = ServiceContext.bacicParamService().getBaseParam();
            getCodeData.put("phone_num", phoneNum);
            getCodeData.put("type", type);
            return DefaultHttpStatefulCallBack.INSTANCE
                    .newCallBack()
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_CODE))
                    .withArg(getCodeData)
                    .buildCallerCallBack();
        }
    }
}
