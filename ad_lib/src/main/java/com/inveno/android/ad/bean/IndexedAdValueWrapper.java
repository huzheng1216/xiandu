package com.inveno.android.ad.bean;

import com.donews.b.main.info.DoNewsAdNativeData;

public class IndexedAdValueWrapper {

    private String scenario;
    private String adSpaceId;
    private int displayType;
    private int index;
    private DoNewsAdNativeData adValue;
    private int viewType;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Object getAdValue() {
        return adValue;
    }

    public void setAdValue(DoNewsAdNativeData adValue) {
        this.adValue = adValue;
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

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public String toString() {
        return "IndexedAdValueWrapper{" +
                "scenario='" + scenario + '\'' +
                ", adSpaceId='" + adSpaceId + '\'' +
                ", displayType=" + displayType +
                ", index=" + index +
                ", adValue=" + adValue +
                ", viewType=" + viewType +
                '}';
    }
}
