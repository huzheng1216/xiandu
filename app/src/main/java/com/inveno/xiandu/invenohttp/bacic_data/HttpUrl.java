package com.inveno.xiandu.invenohttp.bacic_data;

/**
 * @author yongji.wang
 * @date 2020/6/8 14:54
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class HttpUrl {
//    private static String HOST_URI = "https://novel.inveno.com/novel";//生产环境
    private static String HOST_URI = "http://121.201.120.128:9000";//测试环境

    public static String GET_CODE = "/user/get_code";
    public static String LOGIN_PHONE = "/user/login_phone";
    public static String UPDATA_INFO = "/user/update_info";
    public static String GET_INFO = "/user/get_info";

    public static String EARN_COIN = "/coin/earn_coin"; //获取金币
    public static String CONSUMER_COIN = "/coin/consumer_coin"; //消费金币
    public static String QUERY_COIN = "/coin/query_coin"; //用户金币查询
    public static String QUERY_COIN_DETAILS = "/coin/query_coin_details"; //用户金币明细查询

    //书城相关
    public static String EDITOR_LIST = "/content/novel/editor/list"; //小编推荐
    public static String RECOMMEND_LIST = "/content/novel/recommend/list"; //获取相关推荐
    public static String RELEVANT_LIST = "/content/novel/relevant/list"; //获取推荐小说


    public static String getHttpUri(String url){
        return HOST_URI + url;
    }
}
