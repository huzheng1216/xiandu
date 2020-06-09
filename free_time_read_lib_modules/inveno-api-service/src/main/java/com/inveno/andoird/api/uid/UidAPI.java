package com.inveno.andoird.api.uid;

import com.inveno.andoird.api.bean.UidData;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;

public class UidAPI extends BaseSingleInstanceService {

    public BaseStatefulCallBack<UidData> requestUid(){
        return null;
    }
}
