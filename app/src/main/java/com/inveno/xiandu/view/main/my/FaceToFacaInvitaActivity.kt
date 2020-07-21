package com.inveno.xiandu.view.main.my

import android.os.Bundle
import android.text.TextUtils
import com.inveno.xiandu.R
import com.inveno.xiandu.bean.welfare.InvideBean
import com.inveno.xiandu.invenohttp.instancecontext.APIContext
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext
import com.inveno.xiandu.utils.SPUtils
import com.inveno.xiandu.utils.Toaster
import com.inveno.xiandu.view.TitleBarBaseActivity
import kotlinx.android.synthetic.main.activity_share_face_to_face.*

/**
 * @author yongji.wang
 * @date 2020/7/17
 * @更新说明：
 * @更新时间：2020/7/17
 * @Version：1.0.0
 */
class FaceToFacaInvitaActivity : TitleBarBaseActivity() {
    private var inviteCode: String = ""

    override fun layoutID(): Int {
        return R.layout.activity_share_face_to_face
    }

    override fun getCenterText(): String {
        return "面对面邀请"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar(R.color.white, true)
    }

    override fun initView() {
        super.initView()
        getInvitaCode()
    }

    /**
     * 获取邀请码
     */
    fun getInvitaCode() {
        inviteCode = SPUtils.getInformain("invite_code" + ServiceContext.userService().userPid, "")
        if (TextUtils.isEmpty(inviteCode)) {
            APIContext.getWelfareApi().getInviteCode()
                    ?.onSuccess { data: InvideBean ->
                        inviteCode = data.invite_code
                        //邀请码是唯一的，获取一次就保存到本地
                        SPUtils.setInformain("invite_code" + ServiceContext.userService().userPid, data.invite_code)

                        share_invite_code.setText(inviteCode)
                    }
                    ?.onFail { code, message ->
                        Toaster.showToastShort(this, "获取邀请码失败")
                    }
                    ?.execute()
        } else {
            share_invite_code.setText(inviteCode)
        }
    }
}