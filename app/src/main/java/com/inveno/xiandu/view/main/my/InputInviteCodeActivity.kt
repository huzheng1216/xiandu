package com.inveno.xiandu.view.main.my

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.inveno.xiandu.R
import com.inveno.xiandu.invenohttp.instancecontext.APIContext
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext
import com.inveno.xiandu.utils.SPUtils
import com.inveno.xiandu.utils.Toaster
import com.inveno.xiandu.view.TitleBarBaseActivity
import kotlinx.android.synthetic.main.activity_input_invite_code.*

/**
 * @author yongji.wang
 * @date 2020/7/17
 * @更新说明：
 * @更新时间：2020/7/17
 * @Version：1.0.0
 */
class InputInviteCodeActivity : TitleBarBaseActivity() {


    override fun layoutID(): Int {
        return R.layout.activity_input_invite_code
    }

    override fun getCenterText(): String {
        return "填写邀请码"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar(R.color.white, true)
    }

    override fun initView() {
        super.initView()
        invite_code_input_comfig.setOnClickListener(View.OnClickListener {
            val inviteCode = invite_code_input_edit.text.toString()
            val myInviteCode = SPUtils.getInformain("invite_code" + ServiceContext.userService().userPid, "0")
            if (myInviteCode != inviteCode) {
                APIContext.getWelfareApi().bindInviteCode(inviteCode)
                        ?.onSuccess { data: String ->
                            //绑定成功
                            Toaster.showToastShort(this, "绑定成功")
                        }
                        ?.onFail { code, message ->
                            //绑定失败
                            Toaster.showToastShort(this, "绑定失败")
                        }
                        ?.execute()
            }else{
                Toaster.showToastShort(this, "请正确填写邀请码")
            }
        })
        setCursorColor(invite_code_input_edit)
    }

    /**
     * 反射设置光标颜色
     *
     * @param mEditText
     */
    private fun setCursorColor(mEditText: EditText) {
        try {
            val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            f.isAccessible = true
            f[mEditText] = R.drawable.my_cursor
        } catch (ignored: Exception) {
        }
    }
}