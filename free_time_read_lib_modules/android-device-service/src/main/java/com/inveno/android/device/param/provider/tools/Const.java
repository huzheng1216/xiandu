package com.inveno.android.device.param.provider.tools;



/**
 * 常量
 * 
 * @author z.h
 * 
 */
public class Const {

	/** 下载第三方应用路径 **/
	public static String CONTEXT_PK_NAME = AppConfig.CONTEXT_PK_NAME;

	/** 下载第三方应用路径 **/
	public static final String DOWNLOAD_APP_PATH = "appCache";

	/**
	 * 最后一页保存的文件名
	 */
	public static final String LAST_PAGE_SAVE_PATH = "flow.txt";
	
	/**
	 * 闪屏保存路径
	 */
	public static final String SPLASH_SAVA_PATH = "splash.txt";

	/** 静默安装保存apk名字的文件 **/
	public static final String FLOW_UPDATEVERSION_PATH = "install.txt";

	/** 账户相关信息文件夹 **/
	public static final String FLOW_ACCOUNT_FILE_NAME = "ac";

	/** 账户相关信息文件 **/
	public static final String FLOW_ACCOUNT_FILE_TEXT_NAME = "ac.txt";

	/**
	 * 第三方调起
	 * */
	public static final int TUNE_TYPE_SENDBROADCAST = 1;
	public static final int TUNE_TYPE_STARTACTIVITY = 2;
	public static final int PARAM_TYPE_URL = 1;
	public static final int PARAM_TYPE_URL_PKNAME_CLSNAME = 2;
	public static final int PARAM_TYPE_BUNDLE = 3;

	/** 是否每次都打开loading点击继续页面 */
	public static final boolean agree = true;

	/** 是否打开长按弹出json数据的测试功能 */
	public final static boolean OPEN_TEST_JSON = false;

	/** 是否开启按menu键弹出切换服务器接口功能 */
	public final static boolean OPEN_CHANGE_INTERFACE = AppConfig.DEBUG_MODE;
	/** 是否开启debug调试页面功能 */
	public final static boolean OPEN_DEBUG_SETTING = false;

	
	
	/** 是否开启一键保存所有图片功能 */
	public final static boolean OPEN_ONE_KEY_SAVE_ALL_PHOTO = false;

	/** 上传到服务器的appname */

	/**
	 * 公司名，保存的到SD的文件根目录都需加上这个
	 */
	public static final String CONPANY_NAME = AppConfig.SD_NAME;

	/** 版本号 **/
	public static final String VERSION = AppConfig.VERSION_NAME;

	/** 当前使用的协议版本号 **/
	public static final String AGREEMENT_VERSION = "5";
	
	/** 当前SDK版本号**/
	public static  int SDK_VERSION = 3;
	
	/** 指示设置 **/
	public static final String SETTINGS = "settings";
	
	/** 指示设置 **/
	public static final String WORLD_SETTINGS = "worldsettings";

	/** 瀑布流基本配置数据库名 */
	public static final String FLYSHARE_DB = "flow.db";

	public static final int DATA_VERSION = 2;

	public static final String ACTION_NOTIFICATION_CENTER_MASTER = "ACTION_NOTIFICATION_CENTER_MASTER";

	public static final String ACTION_NOTIFICATION_CENTER_SECONDARY = "ACTION_NOTIFICATION_CENTER_SECONDARY";

	public static final String INVENO_BROADCAST_ACTION_RPC = "INVENO_BROADCAST_ACTION_RPC";

	public static final String BROADCAST_PERMISSION_ONLY_RECV_FROM = "com.huawei.android.launcher.permission.PIFLOW_NEWS_RECEIVE";

	public static final String BROADCAST_PERMISSION_ONLY_SEND_TO = "com.inveno.hwread.permission.LAUNCHER_RECEIVE";
	
	
	
	
	/************从无ui sdk迁移 ******************************/
	
	/** Build号 **/
	public static final String VERSION_BUILD_NUMBER = AppConfig.VERSION_BUILD_NUMBER;
	
	public static final String ACTION_UPDATE_INTEREST = "com.inveno.intent.action.UPDATE_INTEREST";

	public static final String INTENT_PERMISSION_OPEN_SECOND_PAGE = "com.inveno.android.permission.PIFLOW_OPEN_SECOND_PAGE";

}
