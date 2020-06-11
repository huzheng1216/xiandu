package com.inveno.xiandu.invenohttp.api.coin;

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
 * @date 2020/6/11 17:46
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class ConsumerCoinAPI extends BaseSingleInstanceService {
    protected static final boolean MODULE_DEBUG = false;

    public StatefulCallBack<String> consumerCoin(String uid, int pid, int trader_id, int coin) {
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
            LinkedHashMap<String, Object> earnCoinParams = ServiceContext.bacicParamService().getBaseParam();
            earnCoinParams.put("uid", uid);
            earnCoinParams.put("pid", pid);
            earnCoinParams.put("trader_id", trader_id);
            earnCoinParams.put("coin", coin);
            return DefaultHttpStatefulCallBack.INSTANCE
                    .newCallBack()
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.CONSUMER_COIN))
                    .withArg(earnCoinParams)
                    .buildCallerCallBack();
        }
    }
}
