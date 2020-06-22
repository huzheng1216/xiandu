package com.inveno.xiandu.invenohttp.api.coin;

import android.text.TextUtils;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.android.basics.service.thread.ThreadUtil;
import com.inveno.xiandu.bean.coin.CoinDetail;
import com.inveno.xiandu.bean.coin.MissionDataList;
import com.inveno.xiandu.bean.coin.UserCoin;
import com.inveno.xiandu.invenohttp.bacic_data.HttpUrl;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;

import org.json.JSONArray;

import java.util.LinkedHashMap;

/**
 * @author yongji.wang
 * @date 2020/6/19 13:41
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class CoinApi extends BaseSingleInstanceService {
    protected static final boolean MODULE_DEBUG = false;

    public StatefulCallBack<UserCoin> queryCoin() {
        if (MODULE_DEBUG) {
            return new BaseStatefulCallBack<UserCoin>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            UserCoin userCoin = new UserCoin(5888, 500, 600, 800);
                            invokeSuccess(userCoin);
                        }
                    });
                }
            };
        } else {
            LinkedHashMap<String, Object> mParams = ServiceContext.bacicParamService().getBaseParam();
            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<UserCoin>newCallBack(new TypeReference<UserCoin>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.QUERY_COIN))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }


    public StatefulCallBack<CoinDetail> getCoinDetail(int page) {
        if (MODULE_DEBUG) {
            return new BaseStatefulCallBack<CoinDetail>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            CoinDetail userCoin = new CoinDetail();
                            invokeSuccess(userCoin);
                        }
                    });
                }
            };
        } else {
            LinkedHashMap<String, Object> mBaseParams = ServiceContext.bacicParamService().getBaseParam();
            LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
            mParams.put("page", page);
            mParams.putAll(mBaseParams);

            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<CoinDetail>newCallBack(new TypeReference<CoinDetail>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.QUERY_COIN_DETAILS))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }

    public StatefulCallBack<MissionDataList> getMission(JSONArray type_id) {
        if (MODULE_DEBUG) {
            return new BaseStatefulCallBack<MissionDataList>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            CoinDetail userCoin = new CoinDetail();
                        }
                    });
                }
            };
        } else {
            LinkedHashMap<String, Object> mBaseParams = ServiceContext.bacicParamService().getBaseParam();
            LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
            mParams.put("type_id", type_id);
            mParams.putAll(mBaseParams);

            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<MissionDataList>newCallBack(new TypeReference<MissionDataList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_MISSION))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }


    public StatefulCallBack<MissionDataList> completeMission(String mission_id) {
        if (MODULE_DEBUG) {
            return new BaseStatefulCallBack<MissionDataList>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                            CoinDetail userCoin = new CoinDetail();
                        }
                    });
                }
            };
        } else {
            LinkedHashMap<String, Object> mBaseParams = ServiceContext.bacicParamService().getBaseParam();
            LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
            mParams.put("mission_id", mission_id);
            mParams.putAll(mBaseParams);

            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<MissionDataList>newCallBack(new TypeReference<MissionDataList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.COMPLETE_MISSION))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }
}
