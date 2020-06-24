package com.inveno.xiandu.view.main.my;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.inveno.android.basics.service.event.EventService;
import com.inveno.android.basics.service.third.json.JsonUtil;
import com.inveno.xiandu.R;
import com.inveno.xiandu.bean.user.UserInfo;
import com.inveno.xiandu.config.ARouterPath;
import com.inveno.xiandu.invenohttp.api.user.LoginAPI;
import com.inveno.xiandu.invenohttp.bacic_data.EventConstant;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.GlideUtils;
import com.inveno.xiandu.utils.Toaster;
import com.inveno.xiandu.utils.fileandsp.AppPersistRepository;
import com.inveno.xiandu.view.TitleBarBaseActivity;
import com.inveno.xiandu.view.dialog.IosTypeDialog;

import java.util.LinkedHashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * @author yongji.wang
 * @date 2020/6/9 17:14
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
@Route(path = ARouterPath.ACTIVITY_USERINFO)
public class UserinfoActivity extends TitleBarBaseActivity {

    private ImageView mine_user_pic;
    private TextView mine_user_name;
    private TextView mine_user_gender;
    private TextView mine_user_code;

    private IosTypeDialog iosTypeDialog;
    private PopupWindow popupWindow;
    private View contentView;
    private UserInfo userInfo;

    @Override
    public String getCenterText() {
        return "基础信息";
    }

    @Override
    public int layoutID() {
        return R.layout.activity_mine_user_data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(R.color.white, true);
    }

    @Override
    protected void initView() {
        super.initView();
        mine_user_pic = findViewById(R.id.mine_user_pic);
        mine_user_name = findViewById(R.id.mine_user_name);
        mine_user_gender = findViewById(R.id.mine_user_gender);
        mine_user_code = findViewById(R.id.mine_user_code);

        userInfo = ServiceContext.userService().getUserInfo();

        if (userInfo != null) {
            if (!TextUtils.isEmpty(userInfo.getHead_url())) {
                GlideUtils.LoadCircleImage(this, userInfo.getHead_url(), mine_user_pic);
            } else {
                GlideUtils.LoadCircleImage(this, R.drawable.ic_header_default, mine_user_pic);
            }
            if (!TextUtils.isEmpty(userInfo.getUser_name())) {
                mine_user_name.setText(userInfo.getUser_name());
            } else {
                mine_user_name.setText(String.format("闲读读者_%s", userInfo.getPid()));
            }

            if (userInfo.getGender().equals("1")) {
                mine_user_gender.setText("男");
            } else if (userInfo.getGender().equals("2")) {
                mine_user_gender.setText("女");
            } else {
                mine_user_gender.setText("未知");
            }
            mine_user_code.setText(String.valueOf(userInfo.getPid()));

            showPopwindow();
        }
        else{
            //未登录，则跳转到登录页
            ARouter.getInstance().build(ARouterPath.ACTIVITY_LOGIN_OTHER_PHONE).navigation();
            finish();
        }
    }

    public void set_user_pic(View view) {
        Toaster.showToastCenter(this, "修改头像");
    }

    public void set_nickname(View view) {
        Toaster.showToastCenter(this, "修改昵称");
        View edit_nickname_view = getLayoutInflater().inflate(R.layout.edit_nickname_view, null, false);
        EditText edit_nickname_et = edit_nickname_view.findViewById(R.id.edit_nickname_et);
        edit_nickname_et.setText(mine_user_name.getText().toString());
        //跳转到通知开关页面
        IosTypeDialog.Builder builder = new IosTypeDialog.Builder(this);
        builder.setTitle("修改昵称");
        builder.setContext("");
        builder.setContext(edit_nickname_view);
        builder.setLeftButton("取消", new IosTypeDialog.OnClickListener() {
            @Override
            public void onClick(View v) {
                iosTypeDialog.dismiss();
            }
        });
        builder.setRightButton("确认修改", new IosTypeDialog.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edit_nickname_et.getText().toString())) {

                    userUpdata("user_name", edit_nickname_et.getText().toString());
                    mine_user_name.setText(edit_nickname_et.getText().toString());
                    userInfo.setUser_name(edit_nickname_et.getText().toString());

                    AppPersistRepository.get().save(LoginAPI.USER_DATA_KEY, JsonUtil.Companion.toJson(userInfo));
                    EventService.Companion.post(EventConstant.REFRESH_USER_DATA);
                    // TODO: 2020/6/10 发送修改昵称请求
                }
                iosTypeDialog.dismiss();
            }
        });

        iosTypeDialog = builder.create();
        iosTypeDialog.show();
        setDialogWindowAttr(iosTypeDialog);
    }

    //在dialog.show()之后调用
    public void setDialogWindowAttr(Dialog dlg) {
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        lp.width = (int) (windowManager.getDefaultDisplay().getWidth() * 0.8); //设置宽度
        dlg.getWindow().setAttributes(lp);
    }

    public void set_gender(View view) {
        openPopWindow();
        setBackgroundAlpha(0.5f, this);
    }

    /**
     * 显示popupWindow
     */
    private void showPopwindow() {
        //加载弹出框的布局
        contentView = LayoutInflater.from(UserinfoActivity.this).inflate(
                R.layout.pop_edit_gender_view, null);

        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //进入退出的动画，指定刚才定义的style
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 按下android回退物理键 PopipWindow消失解决

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                return true;
            } else {
                finish();
            }
        }
        return false;
    }

    /**
     * 设置背景颜色
     *
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha, Context mContext) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    public void openPopWindow() {
        //从底部显示
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        //添加按键事件监听
        setButtonListeners();
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1f, UserinfoActivity.this);
            }
        });
        setBackgroundAlpha(0.5f, this);
    }

    private void setButtonListeners() {
        TextView man = contentView.findViewById(R.id.man);
        TextView women = contentView.findViewById(R.id.women);
        TextView cancel = contentView.findViewById(R.id.cancel);

        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    LinkedHashMap<String, Object> updata = new LinkedHashMap<>();
                    userUpdata("gender", "1");
                    mine_user_gender.setText("男");
                    userInfo.setGender("1");
                    AppPersistRepository.get().save(LoginAPI.USER_DATA_KEY, JsonUtil.Companion.toJson(userInfo));
                    EventService.Companion.post(EventConstant.REFRESH_USER_DATA);
                    popupWindow.dismiss();
                }
            }
        });
        women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    LinkedHashMap<String, Object> updata = new LinkedHashMap<>();
                    userUpdata("gender", "2");
                    mine_user_gender.setText("女");
                    userInfo.setGender("2");
                    AppPersistRepository.get().save(LoginAPI.USER_DATA_KEY, JsonUtil.Companion.toJson(userInfo));
                    EventService.Companion.post(EventConstant.REFRESH_USER_DATA);
                    popupWindow.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }

    private void userUpdata(String key, String value) {
        LinkedHashMap<String, Object> updata = new LinkedHashMap<>();
        updata.put("utype", "1");
        updata.put(key, value);
        APIContext.updataUserAPI().updataUser(updata).onSuccess(new Function1<UserInfo, Unit>() {
            @Override
            public Unit invoke(UserInfo userInfo) {
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                return null;
            }
        }).execute();
    }
}
