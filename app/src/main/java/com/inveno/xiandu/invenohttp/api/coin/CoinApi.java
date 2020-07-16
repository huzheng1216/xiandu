package com.inveno.xiandu.invenohttp.api.coin;

import android.text.TextUtils;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.CommonHttpStatefulCallBack;
import com.inveno.android.basics.service.callback.common.DefaultHttpStatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.android.basics.service.thread.ThreadUtil;
import com.inveno.xiandu.bean.coin.CoinDetail;
import com.inveno.xiandu.bean.coin.CompeleteMissionData;
import com.inveno.xiandu.bean.coin.MissionDataList;
import com.inveno.xiandu.bean.coin.ReadTime;
import com.inveno.xiandu.bean.coin.UserCoin;
import com.inveno.xiandu.bean.coin.UserCoinOut;
import com.inveno.xiandu.invenohttp.api.user.VaricationCodeAPI;
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

    public StatefulCallBack<UserCoinOut> queryCoin() {
        if (MODULE_DEBUG) {
            return new BaseStatefulCallBack<UserCoinOut>() {
                @Override
                public void execute() {
                    ThreadUtil.Installer.install();
                    ThreadUtil.runOnUi(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            };
        } else {
            LinkedHashMap<String, Object> mParams = ServiceContext.bacicParamService().getBaseParam();
            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<UserCoinOut>newCallBack(new TypeReference<UserCoinOut>() {
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

    public StatefulCallBack<MissionDataList> getMission(int[] type_ids) {
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
            mParams.put("type_ids", type_ids);
            mParams.putAll(mBaseParams);

            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<MissionDataList>newCallBack(new TypeReference<MissionDataList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_MISSION))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }

    public StatefulCallBack<CompeleteMissionData> completeMission(String mission_id) {
        if (MODULE_DEBUG) {
            return new BaseStatefulCallBack<CompeleteMissionData>() {
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
                    .<CompeleteMissionData>newCallBack(new TypeReference<CompeleteMissionData>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.COMPLETE_MISSION))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
    }


    public StatefulCallBack<ReadTime> readTime() {
        if (MODULE_DEBUG) {
            return new BaseStatefulCallBack<ReadTime>() {
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

            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<ReadTime>newCallBack(new TypeReference<ReadTime>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.READ_TIME))
                    .withArg(mBaseParams)
                    .buildCallerCallBack();
        }
    }
}
