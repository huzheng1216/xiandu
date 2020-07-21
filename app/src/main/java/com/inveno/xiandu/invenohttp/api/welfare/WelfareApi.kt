package com.inveno.xiandu.invenohttp.api.welfare

import com.alibaba.fastjson.TypeReference
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService
import com.inveno.android.basics.service.callback.BaseStatefulCallBack
import com.inveno.android.basics.service.callback.StatefulCallBack
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack.newCallBack
import com.inveno.android.basics.service.thread.ThreadUtil
import com.inveno.xiandu.BuildConfig
import com.inveno.xiandu.bean.welfare.InvideBean
import com.inveno.xiandu.invenohttp.bacic_data.HttpUrl
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext
import java.util.*

/**
 * @author yongji.wang
 * @date 2020/7/17
 * @更新说明：
 * @更新时间：2020/7/17
 * @Version：1.0.0
 */
class WelfareApi : BaseSingleInstanceService() {
    protected val MODULE_DEBUG = false

    fun getInviteCode(): StatefulCallBack<InvideBean>? {
        if (MODULE_DEBUG) {
            return object : BaseStatefulCallBack<InvideBean>() {
                override fun execute() {
                    ThreadUtil.Installer.install()
                    ThreadUtil.runOnUi {
                        invokeSuccess(InvideBean("AB15CDLF"))
                    }
                }
            }
        } else {
            val getCodeData = ServiceContext.bacicParamService().baseParam
            return newCallBack<InvideBean>(object : TypeReference<InvideBean>() {}.type)
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_INVITE_CODE))
                    .withArg(getCodeData)
                    .buildCallerCallBack()
        }
    }

    fun bindInviteCode(inviteCode: String): StatefulCallBack<String>? {
        if (MODULE_DEBUG) {
            return object : BaseStatefulCallBack<String>() {
                override fun execute() {
                    ThreadUtil.Installer.install()
                    ThreadUtil.runOnUi {
                        invokeSuccess("")
                    }
                }
            }
        } else {
            val getCodeData = ServiceContext.bacicParamService().baseParam
            val mParams = LinkedHashMap<String, Any>()
            mParams["code"] = inviteCode
            mParams.putAll(getCodeData)
            return newCallBack<String>(object : TypeReference<String>() {}.type)
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.BIND_INVITE_CODE))
                    .withArg(mParams)
                    .buildCallerCallBack()
        }
    }
}