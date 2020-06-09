package com.inveno.android.basics.service.callback.common


object DefaultHttpStatefulCallBack {
    fun newCallBack(): CommonHttpStatefulCallBack<String> {
        return CommonHttpStatefulCallBack.newCallBack(
            DefaultStringResponse())
            .originType()
    }

    private class DefaultStringResponse : ResponseDataParser<String> {
        override fun parse(jsonString: String): String {
            return "ok"
        }
    }
}