package com.inveno.android.device.param.provider.tools;


import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

/**
 * 工具类
 * 
 * @author mingsong.zhang
 * @date 2012-07-23
 */
public class LogTools {

	/**
	 * 打印信息
	 * 
	 * @param TAG
	 *            标签
	 * @param tmp
	 *            内容
	 */
	public static void showLog(String TAG, String tmp) {
		if (AppConfig.LOG_SWITCH)
			Log.d(TAG,nullToEmpty(tmp));
	}

	/**
	 * 默认Log标签的输出
	 * 
	 * @param tmp
	 */
	public static void showLog(String tmp) {
		if (AppConfig.LOG_SWITCH)
			showLog("info", nullToEmpty(tmp));
	}

	public static void showLogB(String tmp) {
		if (AppConfig.LOG_SWITCH)
			showLog("blueming.wu", nullToEmpty(tmp));
	}

	public static void showLogA(String tmp) {
		if (AppConfig.LOG_SWITCH)
			showLog("blueming.liu", nullToEmpty(tmp));
	}
	
	public static void i(String TAG, String content) {
		if (AppConfig.LOG_SWITCH)
			Log.i(TAG, nullToEmpty(content));
	}
	
	public static void d(String TAG, String content) {
		if (AppConfig.LOG_SWITCH)
			Log.d(TAG, nullToEmpty(content));
	}
	
	public static void showLogR(String tmp) {
		if (AppConfig.LOG_SWITCH)
			showLog("ruihua.wu", nullToEmpty(tmp));
	}
	
	public static void showLogH(String tmp) {
		if (AppConfig.LOG_SWITCH)
			showLog("zheng.hu", nullToEmpty(tmp));
	}
	
	public static void showLogL(String tmp) {
		if (AppConfig.LOG_SWITCH)
			showLog("benny.liu",nullToEmpty(tmp));
	}

	public static void showLogM(String tmp){
		if (AppConfig.LOG_SWITCH)
			showLog("liang.min",nullToEmpty( tmp));
	}
	
	public static void e(String TAG, String content) {
		if (AppConfig.LOG_SWITCH)
			Log.e(TAG, nullToEmpty(content));
	}

	public static boolean isOPENLOG() {
		return AppConfig.LOG_SWITCH;
	}

	public static void setOPENLOG(boolean oPENLOG) {
		AppConfig.LOG_SWITCH = checkSwitch() || oPENLOG;
	}

	private static String nullToEmpty(String value){
		return value==null?"":value;
	}

	static {
		AppConfig.LOG_SWITCH = checkSwitch();
	}

	private static boolean checkSwitch() {
		try {
			String contentJson = new String(Base64.decode(SdcardUtil.getSecretFileContext(), Base64.DEFAULT));
			JSONObject secretContent = new JSONObject(contentJson);
			return secretContent.getBoolean("log_enable");
		}catch (Exception e){
			return false;
		}
	}
}
