package com.inveno.xiandu.invenohttp.bacic_data;

/**
 * @author yongji.wang
 * @date 2020/6/16 14:56
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class DisplayType {
    public static final int PLAIN_TEXT = 1;
    public static final String PLAIN_TEXT_STR = "0x00000001";
    public static final int IMG_SINGLE = 2;
    public static final String IMG_SINGLE_STR = "0x00000002";
    public static final int IMG_THREE = 4;
    public static final String IMG_THREE_STR = "0x00000004";
    public static final int IMG_FULL = 8;
    public static final String IMG_FULL_STR = "0x00000008";
    public static final int IMG_MULTI = 16;
    public static final String IMG_MULTI_STR = "0x00000010";
    public static final int WATERFALL = 32;
    public static final String WATERFALL_STR = "0x00000020";
    public static final int IMG_SET = 64;
    public static final String IMG_SET_STR = "0x00000040";
    public static final int BANNER = 128;
    public static final String BANNER_STR = "0x00000080";
    public static final int WEB_VIEW = 256;
    public static final String WEB_VIEW_STR = "0x00000100";

    private DisplayType() {
    }
}
