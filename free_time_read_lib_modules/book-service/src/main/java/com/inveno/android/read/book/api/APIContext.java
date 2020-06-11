package com.inveno.android.read.book.api;

import com.inveno.android.read.book.api.book.BookAPI;
import com.inveno.android.basics.service.app.context.InstanceContext;

public class APIContext {
    public static BookAPI book() {
        return InstanceContext.get().getInstance(BookAPI.class);
    }
}
