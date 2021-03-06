package com.inveno.xiandu.invenohttp.bacic_data;

import com.inveno.xiandu.BuildConfig;

/**
 * @author yongji.wang
 * @date 2020/6/8 14:54
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class HttpUrl {
    private static String HOST_URI = BuildConfig.InvenoUrl;//请求地址

    public static String GET_CODE = "/user/get_code";
    public static String LOGIN_PHONE = "/user/login_phone";
    public static String UPDATA_INFO = "/user/update_info";
    public static String GET_INFO = "/user/get_info";
    public static String ADD_PREFERENCE = "/behavior/preference/add";

    public static String EARN_COIN = "/coin/earn_coin"; //获取金币
    public static String CONSUMER_COIN = "/coin/consumer_coin"; //消费金币
    public static String QUERY_COIN = "/coin/query_coin"; //用户金币查询
    public static String QUERY_COIN_DETAILS = "/coin/query_coin_details"; //用户金币明细查询
    public static String GET_MISSION = "/mission/list"; //获取任务接口
    public static String COMPLETE_MISSION = "/mission/complete"; //完成任务接口
    public static String READ_TIME = "/behavior/heat_beat/read/time";//阅读时长


    //书城相关
    public static String EDITOR_LIST = "/content/novel/editor/list"; //小编推荐
    public static String RECOMMEND_LIST = "/content/novel/recommend/list"; //获取相关推荐
    public static String RELEVANT_LIST = "/content/novel/relevant/list"; //获取推荐小说
    public static String CLASSIFY_MENU_LIST = "/content/channel/category"; //获取分类列表
    public static String CLASSIFY_DATA_LIST = "/content/novel/category/list"; //获取分类数据
    public static String RANKING_MENU_LIST = "/content/ranking/list"; //获取排行榜列表
    public static String RANKING_DATA_LIST = "/content/ranking/content"; //获取排行榜数据

    public static String GET_BOOK = "/content/novel/info"; //获取小说详情
    public static String GET_READ_PROGRESS = "/behavior/get_read_progress"; //获取小说详情

    public static String BOOKBRACK_DATA_LIST = "/behavior/book_shelf/list"; //书架查询
    public static String BOOKBRACK_ADD = "/behavior/book_shelf/add"; //书架添加
    public static String BOOKBRACK_UPDATA = "/behavior/book_shelf/update"; //书架更新与删除

    public static String SEARCH_BOOK = "/search/novel"; //搜索

    public static String GET_READ_TRACK = "/behavior/get_read_track"; //阅读足迹查询
    public static String DELETE_READ_TRACK = "/behavior/delete_read_track"; //删除阅读足迹

    public static String UPDATA_URL = "/behavior/get_app_version"; //更新
    public static String GET_BANNER_DATA = "/content/banner/list"; //获取Banner列表

    public static String GET_INVITE_CODE = "/behavior/get_invite_code"; //获取邀请码
    public static String BIND_INVITE_CODE = "/behavior/bind_invite_relate"; //获取邀请码

    public static String GET_ACTIVITY = "/content/activity/list"; //获取我的页面活动列表
    public static String TOP_UP_TELEPHONE = "/coin/recharge_telephone_cost"; //充值话费
    public static String RECHANGE_INFO = "/coin/get_recharge_info"; //充值记录

    public static String getHttpUri(String url) {
        return HOST_URI + url;
    }
}
