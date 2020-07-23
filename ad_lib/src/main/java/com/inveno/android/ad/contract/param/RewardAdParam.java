package com.inveno.android.ad.contract.param;

import android.app.Activity;

import com.inveno.android.ad.contract.listener.RewardVideoAdListener;

public class RewardAdParam extends PlaintAdParam {
    private RewardVideoAdListener rewardVideoAdListener;

    public RewardVideoAdListener getRewardVideoAdListener() {
        return rewardVideoAdListener;
    }

    public void setRewardVideoAdListener(RewardVideoAdListener rewardVideoAdListener) {
        this.rewardVideoAdListener = rewardVideoAdListener;
    }


    public static final class RewardAdParamBuilder {
        private String positionId;
        private Activity activity;
        private RewardVideoAdListener rewardVideoAdListener;

        private RewardAdParamBuilder() {
        }

        public static RewardAdParamBuilder aRewardAdParam() {
            return new RewardAdParamBuilder();
        }

        public RewardAdParamBuilder withActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public RewardAdParamBuilder withRewardVideoAdListener(RewardVideoAdListener rewardVideoAdListener) {
            this.rewardVideoAdListener = rewardVideoAdListener;
            return this;
        }

        public RewardAdParam build() {
            RewardAdParam rewardAdParam = new RewardAdParam();
            rewardAdParam.setActivity(activity);
            rewardAdParam.setContext(activity);
            rewardAdParam.setRewardVideoAdListener(rewardVideoAdListener);
            return rewardAdParam;
        }
    }
}
