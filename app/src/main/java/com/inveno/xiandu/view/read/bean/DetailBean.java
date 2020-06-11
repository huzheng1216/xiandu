package com.inveno.xiandu.view.read.bean;

import java.util.List;

/**
 * Created by newbiechen on 17-4-29.
 */

public class DetailBean<T> {
    private T detail;

    public DetailBean(T details) {
        this.detail = details;
    }

    public T getDetail() {
        return detail;
    }

}
