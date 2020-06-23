package com.inveno.xiandu.bean.ad;

import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.xiandu.bean.BaseDataBean;

public class AdModel extends BaseDataBean {

    private IndexedAdValueWrapper wrapper;

    public AdModel() {
    }

    public AdModel(IndexedAdValueWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public IndexedAdValueWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(IndexedAdValueWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public int getType() {
        return wrapper.getViewType();
    }
}
