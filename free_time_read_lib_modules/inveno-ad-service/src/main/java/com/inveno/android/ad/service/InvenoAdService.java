package com.inveno.android.ad.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.inveno.android.ad.InvenoADAgent;
import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.config.AdViewType;
import com.inveno.android.ad.config.ScenarioManifest;
import com.inveno.android.ad.contract.param.InfoAdParam;
import com.inveno.android.ad.contract.param.PlaintAdParamUtil;
import com.inveno.android.ad.contract.param.RewardAdParam;
import com.inveno.android.ad.contract.param.SplashAdParam;
import com.inveno.android.ad.contract.utils.AllotAdViewType;
import com.inveno.android.api.bean.AdConfigData;
import com.inveno.android.api.bean.Rule;
import com.inveno.android.api.bean.Rule_list;
import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static com.inveno.android.ad.config.ScenarioManifest.BOOK_DETAIL;
import static com.inveno.android.ad.config.ScenarioManifest.BOOK_SHELF;
import static com.inveno.android.ad.config.ScenarioManifest.BOY_GIRL_BOTTOM;
import static com.inveno.android.ad.config.ScenarioManifest.CATEGORY;
import static com.inveno.android.ad.config.ScenarioManifest.EDITOR_RECOMMEND;
import static com.inveno.android.ad.config.ScenarioManifest.GUESS_YOU_LIKE;
import static com.inveno.android.ad.config.ScenarioManifest.RANKING_LIST;
import static com.inveno.android.ad.config.ScenarioManifest.READER_BETWEEN;
import static com.inveno.android.ad.config.ScenarioManifest.READER_BOTTOM;
import static com.inveno.android.ad.config.ScenarioManifest.READ_FOOT_TRACE;
import static com.inveno.android.ad.config.ScenarioManifest.SEARCH;
import static com.inveno.android.ad.config.ScenarioManifest.SPLASH;

public class InvenoAdService {
    public StatefulCallBack<String> init(final Context applicationContext) {
        BaseStatefulCallBack<String> callback = new BaseStatefulCallBack<String>() {
            @Override
            public void execute() {
                InvenoADAgent.onApplicationCreate(applicationContext);
                initADConfig(this);
            }
        };
        return callback;
    }

    public StatefulCallBack<String> requestSplashAd(SplashAdParam splashAdParam) {
        String adSpaceId = getAdSpaceId(ScenarioManifest.SPLASH);
        PlaintAdParamUtil.setPositionId(splashAdParam, adSpaceId);
        return InvenoADAgent.getAdApi().requestSplashAD(splashAdParam);
    }

    public StatefulCallBack<String> requestRewardAd(RewardAdParam rewardAdParam) {
        String adSpaceId = getAdSpaceId(ScenarioManifest.REWARD_VIEDO);
        PlaintAdParamUtil.setPositionId(rewardAdParam, adSpaceId);
        return InvenoADAgent.getAdApi().requestRewardVideoAD(rewardAdParam);
    }

    public Integer getPos(String scenario){
        try {
            Rule_list rule_list = InvenoServiceContext.ad().getRuleList(scenario);
            if (rule_list != null) {
                Rule rule = rule_list.getRule().get(0).get(0);
                return rule.getPos();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public StatefulCallBack<IndexedAdValueWrapper> requestInfoAd(String scenario, Context context) {
        try {

            Rule_list rule_list = InvenoServiceContext.ad().getRuleList(scenario);
            if (rule_list != null) {
                Rule rule = rule_list.getRule().get(0).get(0);
//                Log.i("requestInfoAd", "rule" + JSON.toJSONString(rule_list));

//                zheShiGeKeng(scenario, rule);
                InfoAdParam infoAdParam = InfoAdParam.InfoAdParamBuilder.newBuilder()
                        .withContext(context)
                        .withAdIndex(rule.getPos())
                        .withWidth(rule.getWidth())
                        .withHeight(rule.getHeight())
                        .withAdSpaceId(rule.getAdspace_id())
                        .withDisplayType(rule.getDisplay_type())
                        .withScenario(scenario)
                        .withViewType(AllotAdViewType.allot(scenario, rule.getDisplay_type()))
                        .build();
                PlaintAdParamUtil.setPositionId(infoAdParam, rule.getAdspace_id());

                return InvenoADAgent.getAdApi().requestInfoAD(infoAdParam);
            }
        } catch (Exception e) {
            final String errorMsg = e.getMessage();
            return new BaseStatefulCallBack<IndexedAdValueWrapper>() {
                @Override
                public void execute() {
                    invokeFail(600, errorMsg);
                }
            };
        }
        return new BaseStatefulCallBack<IndexedAdValueWrapper>() {
            @Override
            public void execute() {
                invokeFail(600, "no config");
            }
        };
    }

    public List<StatefulCallBack<IndexedAdValueWrapper>> requestInfoAdList(String scenario, Context context) {
        List<StatefulCallBack<IndexedAdValueWrapper>> resultList = new ArrayList<>();
        try {
            Rule_list rule_list = InvenoServiceContext.ad().getRuleList(scenario);
//            Log.i("requestInfoAd", "rule" + JSON.toJSONString(rule_list));
            if (rule_list != null) {
                List<Rule> rules = rule_list.getRule().get(0);
                for (int i = 0; i < rules.size(); i++) {
                    Rule rule = rules.get(i);
//                    zheShiGeKeng(scenario, rule);
                    InfoAdParam infoAdParam = InfoAdParam.InfoAdParamBuilder.newBuilder()
                            .withContext(context)
                            .withAdIndex(rule.getPos())
                            .withWidth(rule.getWidth())
                            .withHeight(rule.getHeight())
                            .withAdSpaceId(rule.getAdspace_id())
                            .withDisplayType(rule.getDisplay_type())
                            .withScenario(scenario)
                            .withViewType(AllotAdViewType.allot(scenario, rule.getDisplay_type()))
                            .build();
                    PlaintAdParamUtil.setPositionId(infoAdParam, rule.getAdspace_id());

                    resultList.add(InvenoADAgent.getAdApi().requestInfoAD(infoAdParam));
                }
            }else {
                resultList.add(new BaseStatefulCallBack<IndexedAdValueWrapper>() {
                    @Override
                    public void execute() {
                        invokeFail(600, "no config");
                    }
                });
            }
        } catch (Exception e) {
            final String errorMsg = e.getMessage();
            resultList.add(new BaseStatefulCallBack<IndexedAdValueWrapper>() {
                @Override
                public void execute() {
                    invokeFail(600, errorMsg);
                }
            });
        }
        return resultList;
    }

    private String getAdSpaceId(String scenario) {
        Rule_list rule_list = InvenoServiceContext.ad().getRuleList(scenario);
        String adSpaceId = "";
        try {
            adSpaceId = rule_list.getRule().get(0).get(0).getAdspace_id();
        } catch (Exception e) {
        }
        return adSpaceId;
    }

    private void initADConfig(final BaseStatefulCallBack<String> statefulCallBack) {
//        AdConfigData adConfigData = InvenoServiceContext.ad().getAdConfigData();
//        if (adConfigData == null) {
            InvenoServiceContext.ad().requestAdConfig()
                    .onSuccess(new Function1<AdConfigData, Unit>() {
                        @Override
                        public Unit invoke(AdConfigData configData) {
                            statefulCallBack.invokeSuccess("ok");
                            return null;
                        }
                    })
                    .onFail(new Function2<Integer, String, Unit>() {
                        @Override
                        public Unit invoke(Integer integer, String s) {
                            statefulCallBack.invokeFail(integer, s);
                            return null;
                        }
                    })
                    .execute();
//        } else {
//            statefulCallBack.invokeSuccess("ok");
//        }

    }
}
