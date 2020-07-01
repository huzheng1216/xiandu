package com.inveno.xiandu.bean.ad;

import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.book.ReadTrack;

public class AdReadTrackModel extends ReadTrack {

    private IndexedAdValueWrapper wrapper;

    public AdReadTrackModel() {
    }

    public AdReadTrackModel(IndexedAdValueWrapper wrapper) {
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
