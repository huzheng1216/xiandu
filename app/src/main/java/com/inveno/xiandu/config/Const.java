package com.inveno.xiandu.config;

import com.inveno.xiandu.utils.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By huzheng
 * Date 2020-02-16
 * Des 常量
 */
public class Const {

    public static void init(){
    }

    //适配器view类型
    public static final int ADAPTER_TYPE_FOOT = 0;
    public static final int ADAPTER_TYPE_DATA = 1;

    /*URL_BASE*/
    public static final String API_BASE_URL = "http://api.zhuishushenqi.com";
    public static final String IMG_BASE_URL = "http://statics.zhuishushenqi.com";
    //Book Date Convert Format
    public static final String FORMAT_BOOK_DATE = "yyyy-MM-dd'T'HH:mm:ss";

    //BookCachePath (因为getCachePath引用了Context，所以必须是静态变量，不能够是静态常量)
    public static String BOOK_CACHE_PATH = FileUtils.getCachePath()+ File.separator
            + "book_cache"+ File.separator ;


}
