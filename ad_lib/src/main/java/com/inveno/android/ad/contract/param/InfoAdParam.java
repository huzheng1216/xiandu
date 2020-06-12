package com.inveno.android.ad.contract.param;

import android.app.Activity;

import com.donews.b.main.info.DoNewsAdNativeData;
import com.donews.b.start.DoNewsAdView;

import java.util.List;

public class InfoAdParam extends PlaintAdParam {


    private Integer width;
    private Integer height;
    private List<Integer> adIndexList;
    private AdTemplateListener adTemplateListener = DEFAULT_AD_TEMPLATE_LISTENER;


    public AdTemplateListener getAdTemplateListener() {
        return adTemplateListener;
    }

    public void setAdTemplateListener(AdTemplateListener adTemplateListener) {
        this.adTemplateListener = adTemplateListener;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public List<Integer> getAdIndexList() {
        return adIndexList;
    }

    public void setAdIndexList(List<Integer> adIndexList) {
        this.adIndexList = adIndexList;
    }

    public interface AdTemplateListener {
        void OnFailed(String reason);

        void Success(List<Object> adList);
    }


    public static final class InfoAdParamBuilder {
        private Activity activity;
        private Integer width;
        private Integer height;
        private List<Integer> adIndexList;
        private AdTemplateListener adCommonListener;

        private InfoAdParamBuilder() {
        }

        public static InfoAdParamBuilder anInfoAdParam() {
            return new InfoAdParamBuilder();
        }

        public InfoAdParamBuilder withActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public InfoAdParamBuilder withWidth(Integer width) {
            this.width = width;
            return this;
        }

        public InfoAdParamBuilder withHeight(Integer height) {
            this.height = height;
            return this;
        }

        public InfoAdParamBuilder withAdIndexList(List<Integer> adIndexList) {
            this.adIndexList = adIndexList;
            return this;
        }

        public InfoAdParamBuilder withAdCommonListener(AdTemplateListener adCommonListener) {
            this.adCommonListener = adCommonListener;
            return this;
        }

        public InfoAdParam build() {
            InfoAdParam infoAdParam = new InfoAdParam();
            infoAdParam.setActivity(activity);
            infoAdParam.setWidth(width);
            infoAdParam.setHeight(height);
            infoAdParam.setAdIndexList(adIndexList);
            infoAdParam.setAdTemplateListener(adCommonListener);
            return infoAdParam;
        }
    }

    private static final AdTemplateListener DEFAULT_AD_TEMPLATE_LISTENER = new AdTemplateListener() {

        @Override
        public void OnFailed(String reason) {

        }

        @Override
        public void Success(List<Object> adList) {

        }
    };
}
