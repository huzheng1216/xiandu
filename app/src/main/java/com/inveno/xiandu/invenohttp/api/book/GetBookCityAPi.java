package com.inveno.xiandu.invenohttp.api.book;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.ad.bean.IndexedAdValueWrapper;
import com.inveno.android.ad.service.InvenoAdServiceHolder;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.android.basics.service.thread.ThreadUtil;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.ad.AdModel;
import com.inveno.xiandu.bean.book.BaseDataBeanList;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.BookShelfList;
import com.inveno.xiandu.bean.book.CapterDetail;
import com.inveno.xiandu.bean.book.ClassifyData;
import com.inveno.xiandu.bean.book.ClassifyMenu;
import com.inveno.xiandu.bean.book.ClassifyMenuList;
import com.inveno.xiandu.bean.book.EditorRecommend;
import com.inveno.xiandu.bean.book.EditorRecommendList;
import com.inveno.xiandu.bean.book.RankingData;
import com.inveno.xiandu.bean.book.RankingDataList;
import com.inveno.xiandu.bean.book.RankingMenu;
import com.inveno.xiandu.bean.book.RankingMenuList;
import com.inveno.xiandu.bean.book.RecommendName;
import com.inveno.xiandu.invenohttp.bacic_data.DisplayType;
import com.inveno.xiandu.invenohttp.bacic_data.HttpUrl;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.view.adapter.RecyclerBaseAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import static com.inveno.android.ad.config.ScenarioManifest.*;

/**
 * @author yongji.wang
 * @date 2020/6/15 17:41
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class GetBookCityAPi extends BaseSingleInstanceService {

    protected static final boolean MODULE_DEBUG = false;

    public static final int GET_DATA_PAGE_NUM = 10;//单次请求条数

    /**
     * 小编推荐
     *
     * @return
     */
    public StatefulCallBack<EditorRecommendList> getEditorRecommend() {
        StatefulCallBack<EditorRecommendList> realCallback;
        if (MODULE_DEBUG) {

        } else {
            realCallback = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<EditorRecommendList>newCallBack(new TypeReference<EditorRecommendList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.EDITOR_LIST))
                    .withArg(ServiceContext.bacicParamService().getBaseParam())
                    .buildCallerCallBack();
        }

        BaseStatefulCallBack<EditorRecommendList> uiCallback = new BaseStatefulCallBack<EditorRecommendList>() {
            @Override
            public void execute() {
                realCallback.execute();
            }
        };
        realCallback.onSuccess(new Function1<EditorRecommendList, Unit>() {
            @Override
            public Unit invoke(EditorRecommendList editorRecommendList) {
                List<EditorRecommend> editorRecommends = new ArrayList<>();
                for (EditorRecommend editorRecommend : editorRecommendList.getNovel_list()) {
                    if (editorRecommend.getDisplay_type() == DisplayType.PLAIN_TEXT) {
                        editorRecommend.setType(RecyclerBaseAdapter.NOT_IMAGE);

                    } else if (editorRecommend.getDisplay_type() == DisplayType.IMG_SINGLE) {
                        editorRecommend.setType(RecyclerBaseAdapter.SMALL_IMAGE);

                    } else if (editorRecommend.getDisplay_type() == DisplayType.IMG_THREE) {
                        editorRecommend.setType(RecyclerBaseAdapter.MORE_IMAGE);

                    } else if (editorRecommend.getDisplay_type() == DisplayType.IMG_FULL) {
                        editorRecommend.setType(RecyclerBaseAdapter.BIG_IMAGE);
                    }
                    editorRecommends.add(editorRecommend);
                }
                EditorRecommendList mList = new EditorRecommendList();
                mList.setNovel_list(editorRecommends);
                uiCallback.invokeSuccess(mList);
                return null;
            }
        });
        realCallback.onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallback.invokeFail(integer, s);
                return null;
            }
        });
        return uiCallback;
    }

    /**
     * 获取推荐小说,根据组合值获取指定内容
     *
     * @param channel_id 0未知,1男频,2女频,3出版
     * @param type       0未知，1男生热文，2女生热文，3人气精选，4猜你喜欢
     * @param num        请求条数
     * @return
     */
    public StatefulCallBack<BookShelfList> getRecommend(int channel_id, int type, int num) {
        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("channel_id", channel_id);
        mParams.put("num", num);
        mParams.put("type", type);
        mParams.putAll(bacicParams);

        StatefulCallBack<BookShelfList> realCallback;
        if (MODULE_DEBUG) {

        } else {
            realCallback = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<BookShelfList>newCallBack(new TypeReference<BookShelfList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.RECOMMEND_LIST))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }

        BaseStatefulCallBack<BookShelfList> uiCallback = new BaseStatefulCallBack<BookShelfList>() {
            @Override
            public void execute() {
                realCallback.execute();
            }
        };
        realCallback.onSuccess(new Function1<BookShelfList, Unit>() {
            @Override
            public Unit invoke(BookShelfList bookShelfList) {
                List<BookShelf> novel_list = new ArrayList<>();
                for (BookShelf editorRecommend : bookShelfList.getNovel_list()) {
                    editorRecommend.setType(RecyclerBaseAdapter.DEFAUL_RECOMMEND);
                    novel_list.add(editorRecommend);
                }
                BookShelfList mList = new BookShelfList();
                mList.setNovel_list(novel_list);
                uiCallback.invokeSuccess(mList);
                return null;
            }
        });
        realCallback.onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallback.invokeFail(integer, s);
                return null;
            }
        });
        return uiCallback;
    }

    /**
     * 获取推荐的小说详情
     *
     * @param content_id 小说id
     * @return
     */
    public StatefulCallBack<BookShelf> getBook(long content_id) {
        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("content_id", content_id);
        mParams.putAll(bacicParams);

        if (MODULE_DEBUG) {

        } else {
            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<BookShelf>newCallBack(new TypeReference<BookShelf>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_BOOK))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }

        return null;
    }

    /**
     * 获取推荐的小说详情
     *
     * @param content_id 小说id
     * @return
     */
    public StatefulCallBack<BookShelfList> getRelevantBook(long content_id) {
        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("content_id", content_id);
        mParams.putAll(bacicParams);

        if (MODULE_DEBUG) {

        } else {
            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<BookShelfList>newCallBack(new TypeReference<BookShelfList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.RELEVANT_LIST))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }

        return null;
    }

    /**
     * 推荐接口异步请求同步返回
     *
     * @param channel 请求页
     * @return
     */
    public BaseStatefulCallBack<ArrayList<BaseDataBean>> getBookCity(int channel, Context context) {

        ArrayList<BaseDataBean> mDataBeans = new ArrayList<>();
        ArrayList<BaseDataBean> topDataBeans = new ArrayList<>();
        ArrayList<BaseDataBean> bottomDataBeans = new ArrayList<>();

        StatefulCallBack<EditorRecommendList> editorRequest = getEditorRecommend();
        StatefulCallBack<BookShelfList> topRequest;
        StatefulCallBack<BookShelfList> bottomRequest;
        StatefulCallBack<IndexedAdValueWrapper> topAdRequest;
        StatefulCallBack<IndexedAdValueWrapper> bottomAdRequest = InvenoAdServiceHolder.getService().requestInfoAd(GUESS_YOU_LIKE, context);
        String topScenario = "";
        final AdModel adModelTop = new AdModel();
        final AdModel adModelBottom = new AdModel();
        if (channel == 1) {
            //男频
            //请求男生热文
            topRequest = getRecommend(1, 1, 4);
            //请求男生人气精选
            bottomRequest = getRecommend(1, 3, GET_DATA_PAGE_NUM);

            topAdRequest = InvenoAdServiceHolder.getService().requestInfoAd(BOY_GIRL_BOTTOM, context);
        } else if (channel == 2) {
            //女频
            //请求女生热文
            topRequest = getRecommend(2, 2, 4);
            //请求女生人气精选
            bottomRequest = getRecommend(2, 5, GET_DATA_PAGE_NUM);

            topAdRequest = InvenoAdServiceHolder.getService().requestInfoAd(BOY_GIRL_BOTTOM, context);
        } else if (channel == 3) {
            //出版畅销
            topRequest = getRecommend(3, 6, 4);
            //请求出版人气精选
            bottomRequest = getRecommend(3, 7, GET_DATA_PAGE_NUM);

            topAdRequest = InvenoAdServiceHolder.getService().requestInfoAd(BOY_GIRL_BOTTOM, context);
        } else {
            topRequest = getRecommend(0, 4, GET_DATA_PAGE_NUM);
            //推荐
            //请求猜你喜欢
            bottomRequest = getRecommend(0, 4, GET_DATA_PAGE_NUM);

            topAdRequest = InvenoAdServiceHolder.getService().requestInfoAd(EDITOR_RECOMMEND, context);
        }

        BaseStatefulCallBack<ArrayList<BaseDataBean>> uiCallback = new BaseStatefulCallBack<ArrayList<BaseDataBean>>() {
            @Override
            public void execute() {
                if (channel == 0) {
                    editorRequest.execute();
                } else {
                    topRequest.execute();
                }
                //请求广告
                topAdRequest.execute();
            }
        };
        if (channel == 0) {
            //推荐
            //请求小编推荐
            editorRequest.onSuccess(new Function1<EditorRecommendList, Unit>() {
                @Override
                public Unit invoke(EditorRecommendList editorRecommendList) {
                    RecommendName recommendName = new RecommendName();
                    recommendName.setRecommendName("小编推荐");
                    recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);
                    topDataBeans.clear();
                    topDataBeans.add(recommendName);
                    topDataBeans.addAll(editorRecommendList.getNovel_list());

                    addAd(topDataBeans, bottomDataBeans, adModelTop, adModelBottom);
                    mDataBeans.addAll(topDataBeans);

                    bottomRequest.execute();
                    bottomAdRequest.execute();
                    return null;
                }
            }).onFail(new Function2<Integer, String, Unit>() {
                @Override
                public Unit invoke(Integer integer, String s) {
                    bottomRequest.execute();
                    bottomAdRequest.execute();
//                    uiCallback.invokeFail(integer, s);
                    return null;
                }
            });

            bottomRequest.onSuccess(new Function1<BookShelfList, Unit>() {
                @Override
                public Unit invoke(BookShelfList bookShelfList) {
                    RecommendName recommendName = new RecommendName();
                    recommendName.setRecommendName("猜你喜欢");
                    recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);

                    bottomDataBeans.add(recommendName);
                    bottomDataBeans.addAll(bookShelfList.getNovel_list());

                    addAd(topDataBeans, bottomDataBeans, adModelTop, adModelBottom);

                    mDataBeans.addAll(bottomDataBeans);
                    uiCallback.invokeSuccess(mDataBeans);
                    return null;
                }
            }).onFail(new Function2<Integer, String, Unit>() {
                @Override
                public Unit invoke(Integer integer, String s) {
                    uiCallback.invokeSuccess(mDataBeans);
                    return null;
                }
            });

        } else {
            topRequest.onSuccess(new Function1<BookShelfList, Unit>() {
                @Override
                public Unit invoke(BookShelfList bookShelfList) {
                    RecommendName recommendName = new RecommendName();
                    if (channel == 1) {
                        recommendName.setRecommendName("男生热文");
                        recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);
                    } else if (channel == 2) {
                        recommendName.setRecommendName("女生热文");
                        recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);

                    } else if (channel == 3) {
                        recommendName.setRecommendName("精选畅销");
                        recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);
                    }
                    topDataBeans.add(recommendName);
                    topDataBeans.addAll(bookShelfList.getNovel_list());

                    addAd(topDataBeans, bottomDataBeans, adModelTop, adModelBottom);

                    mDataBeans.addAll(topDataBeans);
                    bottomRequest.execute();
                    bottomAdRequest.execute();
                    return null;
                }
            }).onFail(new Function2<Integer, String, Unit>() {
                @Override
                public Unit invoke(Integer integer, String s) {
                    bottomRequest.execute();
                    bottomAdRequest.execute();
                    return null;
                }
            });
            bottomRequest.onSuccess(new Function1<BookShelfList, Unit>() {
                @Override
                public Unit invoke(BookShelfList bookShelfList) {
                    RecommendName recommendName = new RecommendName();
                    recommendName.setRecommendName("人气精选");
                    recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);
                    bottomDataBeans.add(recommendName);
                    bottomDataBeans.addAll(bookShelfList.getNovel_list());

                    addAd(topDataBeans, bottomDataBeans, adModelTop, adModelBottom);

                    mDataBeans.addAll(bottomDataBeans);
                    uiCallback.invokeSuccess(mDataBeans);
                    return null;
                }
            }).onFail(new Function2<Integer, String, Unit>() {
                @Override
                public Unit invoke(Integer integer, String s) {
                    uiCallback.invokeSuccess(mDataBeans);
                    return null;
                }
            });
        }

        topAdRequest.onSuccess(wrapper -> {
//            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            adModelTop.setWrapper(wrapper);
            int adIndex = wrapper.getIndex() + 1;
            if (mDataBeans.size() >= adIndex && topDataBeans.size() >= adIndex) {
                topDataBeans.add(adIndex, adModelTop);
                mDataBeans.add(adIndex, adModelTop);
                uiCallback.invokeSuccess(mDataBeans);
            }
            return null;
        }).onFail((integer, s) -> {
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        });

        bottomAdRequest.onSuccess(wrapper -> {
//            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            adModelBottom.setWrapper(wrapper);
            int adIndex = wrapper.getIndex() + 1;
            if (mDataBeans.size() >= adIndex && bottomDataBeans.size() >= adIndex) {
                mDataBeans.add(wrapper.getIndex() + topDataBeans.size() + 1, adModelBottom);
                uiCallback.invokeSuccess(mDataBeans);
            }
            return null;
        }).onFail((integer, s) -> {
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        });

        return uiCallback;
    }

    /**
     * 推荐接口异步请求同步返回
     *
     * @param channel 请求页
     * @return
     */
    public BaseStatefulCallBack<ArrayList<BaseDataBean>> getBookCityTop(int channel, Context context) {

        ArrayList<BaseDataBean> mDataBeans = new ArrayList<>();
        ArrayList<BaseDataBean> topDataBeans = new ArrayList<>();

        StatefulCallBack<EditorRecommendList> editorRequest = getEditorRecommend();
        StatefulCallBack<BookShelfList> topRequest;
        StatefulCallBack<IndexedAdValueWrapper> topAdRequest;
        final AdModel adModelTop = new AdModel();
        if (channel == 1) {
            //男频
            //请求男生热文
            topRequest = getRecommend(1, 1, 4);

            topAdRequest = InvenoAdServiceHolder.getService().requestInfoAd(BOY_GIRL_BOTTOM, context);
        } else if (channel == 2) {
            //女频
            //请求女生热文
            topRequest = getRecommend(2, 2, 4);

            topAdRequest = InvenoAdServiceHolder.getService().requestInfoAd(BOY_GIRL_BOTTOM, context);
        } else {
            //出版畅销
            topRequest = getRecommend(3, 6, 4);

            topAdRequest = InvenoAdServiceHolder.getService().requestInfoAd(BOY_GIRL_BOTTOM, context);
        }

        BaseStatefulCallBack<ArrayList<BaseDataBean>> uiCallback = new BaseStatefulCallBack<ArrayList<BaseDataBean>>() {
            @Override
            public void execute() {
                if (channel == 0) {
                    editorRequest.execute();
                } else {
                    topRequest.execute();
                }
            }
        };

        topRequest.onSuccess(new Function1<BookShelfList, Unit>() {
            @Override
            public Unit invoke(BookShelfList bookShelfList) {
                RecommendName recommendName = new RecommendName();
                if (channel == 1) {
                    recommendName.setRecommendName("男生热文");
                    recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);
                } else if (channel == 2) {
                    recommendName.setRecommendName("女生热文");
                    recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);

                } else if (channel == 3) {
                    recommendName.setRecommendName("精选畅销");
                    recommendName.setType(RecyclerBaseAdapter.CENTER_TITLE);
                }
                topDataBeans.add(recommendName);
                topDataBeans.addAll(bookShelfList.getNovel_list());

                addAd(topDataBeans, null, adModelTop, null);

                mDataBeans.addAll(topDataBeans);
//                uiCallback.invokeSuccess(mDataBeans);
                //请求广告
                topAdRequest.execute();
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallback.invokeFail(integer, s);
                return null;
            }
        });

        topAdRequest.onSuccess(wrapper -> {
            Log.i("requestInfoAd", "onSuccess wrapper " + wrapper.toString());
            adModelTop.setWrapper(wrapper);
            int adIndex = wrapper.getIndex() + 1;
            if (mDataBeans.size() >= adIndex && topDataBeans.size() >= adIndex) {
                topDataBeans.add(adIndex, adModelTop);
                mDataBeans.add(adIndex, adModelTop);

                uiCallback.invokeSuccess(mDataBeans);
            }
            return null;
        }).onFail((integer, s) -> {
            uiCallback.invokeSuccess(mDataBeans);
            Log.i("requestInfoAd", "onFail s:" + s + " integer:" + integer);
            return null;
        });

        return uiCallback;
    }

    private void addAd(ArrayList<BaseDataBean> topDataBeans, ArrayList<BaseDataBean> bottomDataBeans, AdModel adModelTop, AdModel adModelBottom) {
        if (adModelTop.getWrapper() != null && topDataBeans.size() >= adModelTop.getWrapper().getIndex() + 1) {
            topDataBeans.add(adModelTop.getWrapper().getIndex() + 1, adModelTop);
        }

        if (bottomDataBeans != null) {
            if (adModelBottom.getWrapper() != null && bottomDataBeans.size() >= adModelBottom.getWrapper().getIndex() + 1) {
                bottomDataBeans.add(adModelBottom.getWrapper().getIndex() + 1, adModelBottom);
            }
        }
    }

    /**
     * 获取分类列表
     *
     * @param channel_id 频道id
     * @return
     */
    public StatefulCallBack<List<ClassifyMenu>> getClassifyMenu(int channel_id) {
        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("channel_id", channel_id);
        mParams.putAll(bacicParams);

        StatefulCallBack<ClassifyMenuList> realCallback;
        if (MODULE_DEBUG) {
        } else {
            realCallback = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<ClassifyMenuList>newCallBack(new TypeReference<ClassifyMenuList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.CLASSIFY_MENU_LIST))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }
        BaseStatefulCallBack<List<ClassifyMenu>> uiCallback = new BaseStatefulCallBack<List<ClassifyMenu>>() {
            @Override
            public void execute() {
                realCallback.execute();
            }
        };
        realCallback.onSuccess(new Function1<ClassifyMenuList, Unit>() {
            @Override
            public Unit invoke(ClassifyMenuList classifyMenuList) {
                List<ClassifyMenu> classifyMenus = classifyMenuList.getCategory_list();
                uiCallback.invokeSuccess(classifyMenus);
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallback.invokeFail(integer, s);
                return null;
            }
        });

        return uiCallback;
    }


    /**
     * 获取指定分类下的小说列表
     *
     * @param category_id 分类ID
     * @param book_status 小说状态(默认传-1表示全部，0:连载 ,1:完本)
     * @param page_num    当前页码(默认从第一页开始)
     * @return
     */
    public StatefulCallBack<ClassifyData> getClassifyData(int category_id, int book_status, int page_num) {
        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("category_id", category_id);
        mParams.put("book_status", book_status);
        mParams.put("page_num", page_num);
        mParams.putAll(bacicParams);

        StatefulCallBack<ClassifyData> realCallback;
        if (MODULE_DEBUG) {

        } else {
            realCallback = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<ClassifyData>newCallBack(new TypeReference<ClassifyData>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.CLASSIFY_DATA_LIST))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }

        BaseStatefulCallBack<ClassifyData> uiCallback = new BaseStatefulCallBack<ClassifyData>() {
            @Override
            public void execute() {
                realCallback.execute();
            }
        };
        realCallback.onSuccess(new Function1<ClassifyData, Unit>() {
            @Override
            public Unit invoke(ClassifyData classifyData) {
                List<BookShelf> novel_list = new ArrayList<>();
                for (BookShelf bookShelf : classifyData.getNovel_list()) {
                    bookShelf.setType(RecyclerBaseAdapter.DEFAUL_RECOMMEND);
                    novel_list.add(bookShelf);
                }
                classifyData.setCategory_id(category_id);
                classifyData.setNovel_list(novel_list);
                uiCallback.invokeSuccess(classifyData);
                return null;
            }
        });
        realCallback.onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallback.invokeFail(integer, s);
                return null;
            }
        });
        return uiCallback;
    }

    /**
     * 获取排行榜列表
     *
     * @return
     */
    public StatefulCallBack<List<RankingMenu>> getRankingMenu() {
        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();

        StatefulCallBack<RankingMenuList> realCallback;
        if (MODULE_DEBUG) {
        } else {
            realCallback = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<RankingMenuList>newCallBack(new TypeReference<RankingMenuList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.RANKING_MENU_LIST))
                    .withArg(bacicParams)
                    .buildCallerCallBack();
        }
        BaseStatefulCallBack<List<RankingMenu>> uiCallback = new BaseStatefulCallBack<List<RankingMenu>>() {
            @Override
            public void execute() {
                realCallback.execute();
            }
        };
        realCallback.onSuccess(new Function1<RankingMenuList, Unit>() {
            @Override
            public Unit invoke(RankingMenuList rankingMenuList) {
                List<RankingMenu> rankingMenus = rankingMenuList.getRanking_list();
                uiCallback.invokeSuccess(rankingMenus);
                return null;
            }
        }).onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallback.invokeFail(integer, s);
                return null;
            }
        });

        return uiCallback;
    }


    /**
     * 获取排行榜下的小说
     *
     * @param ranking_id 排行榜id
     * @param channel_id 频道id
     * @return
     */
    public StatefulCallBack<List<RankingData>> getRankingData(int ranking_id, int channel_id) {
        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("ranking_id", ranking_id);
        mParams.put("channel_id", channel_id);
        mParams.putAll(bacicParams);

        StatefulCallBack<RankingDataList> realCallback;
        if (MODULE_DEBUG) {

        } else {
            realCallback = MultiTypeHttpStatefulCallBack.INSTANCE
                    .<RankingDataList>newCallBack(new TypeReference<RankingDataList>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.RANKING_DATA_LIST))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }

        BaseStatefulCallBack<List<RankingData>> uiCallback = new BaseStatefulCallBack<List<RankingData>>() {
            @Override
            public void execute() {
                realCallback.execute();
            }
        };
        realCallback.onSuccess(new Function1<RankingDataList, Unit>() {
            @Override
            public Unit invoke(RankingDataList rankingDataList) {
                List<RankingData> rankingDatas = rankingDataList.getNovel_list();
                uiCallback.invokeSuccess(rankingDatas);
                return null;
            }
        });
        realCallback.onFail(new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer integer, String s) {
                uiCallback.invokeFail(integer, s);
                return null;
            }
        });
        return uiCallback;
    }

    /**
     * 获取阅读进度
     *
     * @param content_id 小说id
     * @return
     */
    public StatefulCallBack<CapterDetail> getReadProgress(long content_id) {
        LinkedHashMap<String, Object> bacicParams = ServiceContext.bacicParamService().getBaseParam();
        LinkedHashMap<String, Object> mParams = new LinkedHashMap<>();
        mParams.put("content_id", content_id);
        mParams.putAll(bacicParams);

        if (MODULE_DEBUG) {

        } else {
            return MultiTypeHttpStatefulCallBack.INSTANCE
                    .<CapterDetail>newCallBack(new TypeReference<CapterDetail>() {
                    }.getType())
                    .atUrl(HttpUrl.getHttpUri(HttpUrl.GET_READ_PROGRESS))
                    .withArg(mParams)
                    .buildCallerCallBack();
        }

        return null;
    }
}
