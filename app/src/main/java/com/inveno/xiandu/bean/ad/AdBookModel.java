package com.inveno.xiandu.bean.ad;

import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.xiandu.bean.book.Bookbrack;

public class AdBookModel extends Bookbrack {

    private IndexedAdValueWrapper wrapper;

    public AdBookModel() {
    }

    public AdBookModel(IndexedAdValueWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public IndexedAdValueWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(IndexedAdValueWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public int getIndex(){
        if (wrapper!=null){
            return wrapper.getIndex();
        }
        return 0;
    }
}
