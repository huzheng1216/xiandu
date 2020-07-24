package com.inveno.xiandu.invenohttp.api.welfare

import com.alibaba.fastjson.TypeReference
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService
import com.inveno.android.basics.service.callback.BaseStatefulCallBack
import com.inveno.android.basics.service.callback.StatefulCallBack
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack.newCallBack
import com.inveno.android.basics.service.thread.ThreadUtil
import com.inveno.xiandu.BuildConfig
import com.inveno.xiandu.bean.welfare.ActivityBeanList
import com.inveno.xiandu.bean.welfare.InvideBean
import com.inveno.xiandu.bean.welfare.RechangeBean
import com.inveno.xiandu.bean.welfare.RechangeBeanList
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


    fun getActivityList(): StatefulCallBack<ActivityBeanList>? {
        if (MODULE_DEBUG) {
            return null
        } else {
            val getCodeData = ServiceContext.bacicParamService().baseParam
            return newCallBack<ActivityBeanList>(object : TypeReference<ActivityBeanList>() {}.type)
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_ACTIVITY))
                    .withArg(getCodeData)
                    .buildCallerCallBack()
        }
    }

    /**
     * 手机充值
     * @param recharge_id 充值ID 1充值10元  2充值20元 3充值30元  4充值50元
     * @param telephone 手机号
     * @param operator 运营商 1移动 2联通 3电信
     * @return
     */
    fun topUpTelephone(recharge_id: Int, telephone: String, operator: Int): StatefulCallBack<String>? {
//        if (MODULE_DEBUG) {
//            return null
//        } else {
        val getCodeData = ServiceContext.bacicParamService().baseParam
        val mParams = LinkedHashMap<String, Any>()
        mParams["recharge_id"] = recharge_id
        mParams["telephone"] = telephone
        mParams["operator"] = operator
        mParams.putAll(getCodeData)
        return newCallBack<String>(object : TypeReference<String>() {}.type)
                .atUrl(HttpUrl.getHttpUri(HttpUrl.TOP_UP_TELEPHONE))
                .withArg(mParams)
                .buildCallerCallBack()
//        }
    }

    /**
     * 手机充值
     * @param page 页码
     * @return
     */
    fun getRechangeInfo(page: Int): StatefulCallBack<RechangeBeanList>? {
        if (MODULE_DEBUG) {
            return object : BaseStatefulCallBack<RechangeBeanList>() {
                override fun execute() {
                    ThreadUtil.Installer.install()
                    ThreadUtil.runOnUi {
                        val rechangeBeanList = RechangeBeanList()
                        val list = arrayListOf<RechangeBean>()
                        list.add(RechangeBean(10, 1595494514000, 1, 100000, 1))
                        list.add(RechangeBean(50, 1595494414000, 2, 500000, 2))
                        list.add(RechangeBean(30, 1595493514000, 3, 300000, 3))
                        rechangeBeanList.recharge_list = list
                        rechangeBeanList.page = 2
                        invokeSuccess(rechangeBeanList)
                    }
                }
            }
        } else {
            val getCodeData = ServiceContext.bacicParamService().baseParam
            val mParams = LinkedHashMap<String, Any>()
            mParams["page"] = page
            mParams.putAll(getCodeData)
            return newCallBack<RechangeBeanList>(object : TypeReference<RechangeBeanList>() {}.type)
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.RECHANGE_INFO))
                    .withArg(mParams)
                    .buildCallerCallBack()
        }
    }
}