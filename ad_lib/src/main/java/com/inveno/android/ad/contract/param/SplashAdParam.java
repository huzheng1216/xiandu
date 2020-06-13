package com.inveno.android.ad.contract.param;

import android.app.Activity;
import android.view.ViewGroup;

import com.inveno.android.ad.contract.listener.SplashAdListener;

public class SplashAdParam extends UiAdParam{
    private SplashAdListener splashAdListener;

    public SplashAdListener getSplashAdListener() {
        return splashAdListener;
    }

    public void setSplashAdListener(SplashAdListener splashAdListener) {
        this.splashAdListener = splashAdListener;
    }


    public static final class SplashAdParamBuilder {
        private ViewGroup containerView;
        private Activity activity;
        private SplashAdListener splashAdListener;

        public SplashAdParamBuilder() {
        }

        public static SplashAdParamBuilder aSplashAdParam() {
            return new SplashAdParamBuilder();
        }

        public SplashAdParamBuilder withContainerView(ViewGroup containerView) {
            this.containerView = containerView;
            return this;
        }

        public SplashAdParamBuilder withActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public SplashAdParamBuilder withSplashAdListener(SplashAdListener splashAdListener) {
            this.splashAdListener = splashAdListener;
            return this;
        }

        public SplashAdParam build() {
            SplashAdParam splashAdParam = new SplashAdParam();
            splashAdParam.setContainerView(containerView);
            splashAdParam.setActivity(activity);
            splashAdParam.setSplashAdListener(splashAdListener);
            return splashAdParam;
        }
    }
}
