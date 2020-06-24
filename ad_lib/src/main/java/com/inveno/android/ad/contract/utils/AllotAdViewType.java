package com.inveno.android.ad.contract.utils;

import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.config.ScenarioManifest;

import static com.inveno.android.ad.config.AdViewType.*;

public class AllotAdViewType {

    public static int allot(IndexedAdValueWrapper wrapper) {
        return allot(wrapper.getScenario(), wrapper.getDisplayType());
    }

    /**
     * // 阅读器底部广告
     * String READER_BOTTOM = "0x0506ff";
     * <p>
     * // 小编推荐模块广告
     * String EDITOR_RECOMMEND = "0x0203ff";
     * <p>
     * // 开屏广告
     * String SPLASH = "0x060100";
     * <p>
     * // 男生/女生热文底部广告
     * String BOY_GIRL_BOTTOM = "0x0004fe";
     * <p>
     * // 人气精选/猜你喜欢广告
     * String GUESS_YOU_LIKE = "0x0001fe";
     * <p>
     * // 阅读器间隔广告
     * String READER_BETWEEN = "0x0201fe";
     * <p>
     * // 书籍详情页广告
     * String BOOK_DETAIL = "0x0203fe";
     * <p>
     * // 搜索框广告
     * String SEARCH = "0x0207fe";
     * <p>
     * // 排行榜广告
     * String RANKING_LIST = "0x0001ff";
     * <p>
     * // 分类页广告
     * String CATEGORY = "0x0105ff";
     * <p>
     * // 书架广告
     * String BOOK_SHELF = "0x0401ff";
     * <p>
     * // 阅读足迹广告
     * String READ_FOOT_TRACE = "0x0105fe";
     *
     * @return
     */
    public static int allot(String scenario, int desplayType) {

        switch (scenario) {
            case ScenarioManifest.READER_BOTTOM:
                // 阅读器底部广告

                break;
            case ScenarioManifest.EDITOR_RECOMMEND:
                // 小编推荐模块广告
                return desplayType == 1 ? AD_EDITOR_RECOMMEND_TYPE_1 : AD_EDITOR_RECOMMEND_TYPE_2;

            case ScenarioManifest.SPLASH:
                // 开屏广告
                break;

            case ScenarioManifest.BOY_GIRL_BOTTOM:
                // 男生/女生热文底部广告
                return AD_BOY_GIRL_BOTTOM_TYPE;

            case ScenarioManifest.GUESS_YOU_LIKE:
                // 人气精选/猜你喜欢广告
                return AD_GUESS_YOU_LIKE_TYPE_1;

            case ScenarioManifest.READER_BETWEEN:
                // 阅读器间隔广告
                break;

            case ScenarioManifest.BOOK_DETAIL:
                // 书籍详情页广告
                break;

            case ScenarioManifest.SEARCH:
                // 搜索框广告
                return AD_SEARCH_TYPE;

            case ScenarioManifest.RANKING_LIST:
                // 排行榜广告
                return AD_RANKING_LIST_TYPE;

            case ScenarioManifest.CATEGORY:
                // 分类页广告
                return AD_CATEGORY_TYPE;

            case ScenarioManifest.BOOK_SHELF:
                // 书架广告
                return AD_BOOK_SHELF_TYPE;

            case ScenarioManifest.READ_FOOT_TRACE:
                // 阅读足迹广告
                return AD_READ_FOOT_TRACE_TYPE;

            default:
                break;
        }

        return AD_EDITOR_RECOMMEND_TYPE_1;
    }

}
