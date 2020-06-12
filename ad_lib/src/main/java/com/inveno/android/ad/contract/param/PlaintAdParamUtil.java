package com.inveno.android.ad.contract.param;

public class PlaintAdParamUtil {
    public static <T extends PlaintAdParam> T setPositionId(T adParam,String positionId) {
        adParam.setPositionId(positionId);
        return adParam;
    }
}
