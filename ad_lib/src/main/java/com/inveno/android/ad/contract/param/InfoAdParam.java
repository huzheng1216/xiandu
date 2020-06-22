package com.inveno.android.ad.contract.param;

import android.app.Activity;
import android.content.Context;

import com.donews.b.main.info.DoNewsAdNativeData;
import com.donews.b.start.DoNewsAdView;

import java.util.List;

public class InfoAdParam extends PlaintAdParam {

    private int viewType;
    private String scenario;
    private String adSpaceId;
    private Integer width;
    private Integer height;
    private int index;
    private int displayType;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getAdSpaceId() {
        return adSpaceId;
    }

    public void setAdSpaceId(String adSpaceId) {
        this.adSpaceId = adSpaceId;
    }

    public int getDisplayType() {
        return displayType;
    }

    public void setDisplayType(int displayType) {
        this.displayType = displayType;
    }

    public interface AdTemplateListener {
        void OnFailed(String reason);

        void Success(List<Object> adList);
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public static final class InfoAdParamBuilder {
        private Context context;
        private Integer width;
        private Integer height;
        private int index;
        private AdTemplateListener adCommonListener;
        private int viewType;
        private String scenario;
        private String adSpaceId;
        private int displayType;

        private InfoAdParamBuilder() {
        }

        public static InfoAdParamBuilder newBuilder() {
            return new InfoAdParamBuilder();
        }

        public InfoAdParamBuilder withContext(Context context) {
            this.context = context;
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

        public InfoAdParamBuilder withAdIndex(int index) {
            this.index = index;
            return this;
        }

        public InfoAdParamBuilder withAdCommonListener(AdTemplateListener adCommonListener) {
            this.adCommonListener = adCommonListener;
            return this;
        }

        public InfoAdParamBuilder withViewType(int viewType) {
            this.viewType = viewType;
            return this;
        }

        public InfoAdParamBuilder withScenario(String scenario) {
            this.scenario = scenario;
            return this;
        }

        public InfoAdParamBuilder withAdSpaceId(String adSpaceId) {
            this.adSpaceId = adSpaceId;
            return this;
        }

        public InfoAdParamBuilder withDisplayType(int displayType) {
            this.displayType = displayType;
            return this;
        }

        public InfoAdParam build() {
            InfoAdParam infoAdParam = new InfoAdParam();
            infoAdParam.setContext(context);
            infoAdParam.setWidth(width);
            infoAdParam.setHeight(height);
            infoAdParam.setIndex(index);
            infoAdParam.setAdTemplateListener(adCommonListener);
            infoAdParam.setViewType(viewType);
            infoAdParam.setScenario(scenario);
            infoAdParam.setAdSpaceId(adSpaceId);
            infoAdParam.setDisplayType(displayType);
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
