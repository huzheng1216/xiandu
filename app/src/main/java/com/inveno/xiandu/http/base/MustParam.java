package com.inveno.xiandu.http.base;

import android.content.Context;

/**
 *
 * @ClassName: MustParam
 * @Description: 与服务器交互的必传参数
 * @author zheng.hu
 * @date 2016/9/26
 *
 */
public class MustParam {

	public final static String APP_NAME = "app";
	public final static String UID = "uid";
	public final static String APP_VERSION = "ver";
	public final static String AGREEMENT_VERSION = "rver";
	public final static String OPERATORS = "op";
	public final static String NET = "net";
	public final static String OS = "os";
	public final static String OS_VERSION = "osver";
	public final static String PHONE_MODEL = "pm";
	public final static String TIME_CURRENT = "tm";
	public final static String TOKEN = "tk";
	public final static String SDK_VERSION = "sdk";
	public final static String OS_LANGUAGE = "lang";
	public final static String IMEI="imi";
	public final static String MAC="mac";

	private MustParam(Context context) {
		super();
	}
	private static MustParam mustParam;

	public synchronized static MustParam newInstance(Context context) {
		if (mustParam == null) {
			mustParam = new MustParam(context);
		}
		return mustParam;
	}
}