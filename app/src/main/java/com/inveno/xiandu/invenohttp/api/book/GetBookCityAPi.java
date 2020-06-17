package com.inveno.xiandu.invenohttp.api.book;

import com.alibaba.fastjson.TypeReference;
import com.inveno.android.basics.service.app.context.BaseSingleInstanceService;
import com.inveno.android.basics.service.callback.BaseStatefulCallBack;
import com.inveno.android.basics.service.callback.StatefulCallBack;
import com.inveno.android.basics.service.callback.common.MultiTypeHttpStatefulCallBack;
import com.inveno.xiandu.bean.BaseDataBean;
import com.inveno.xiandu.bean.book.BaseDataBeanList;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.BookShelfList;
import com.inveno.xiandu.bean.book.EditorRecommend;
import com.inveno.xiandu.bean.book.EditorRecommendList;
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

/**
 * @author yongji.wang
 * @date 2020/6/15 17:41
 * @更新说明：
 * @更新时间：
 * @Version：1.0.0
 */
public class GetBookCityAPi extends BaseSingleInstanceService {

    protected static final boolean MODULE_DEBUG = false;

    private static final int GET_DATA_PAGE_NUM = 20;//单次请求条数

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

                    }else if (editorRecommend.getDisplay_type() == DisplayType.IMG_THREE) {
                        editorRecommend.setType(RecyclerBaseAdapter.MORE_IMAGE);

                    }else if (editorRecommend.getDisplay_type() == DisplayType.IMG_FULL) {
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

    public BaseStatefulCallBack<ArrayList<BaseDataBean>> getBookCity(int channel) {

        ArrayList<BaseDataBean> mDataBeans = new ArrayList<>();
        ArrayList<BaseDataBean> topDataBeans = new ArrayList<>();
        ArrayList<BaseDataBean> bottomDataBeans = new ArrayList<>();

        StatefulCallBack<EditorRecommendList> editorRequest = getEditorRecommend();
        StatefulCallBack<BookShelfList> topRequest;
        StatefulCallBack<BookShelfList> bottomRequest;
        if (channel == 1) {
            //男频
            //请求男生热文
            topRequest = getRecommend(1, 1, 4);
            //请求男生人气精选
            bottomRequest = getRecommend(1, 3, GET_DATA_PAGE_NUM);
        } else if (channel == 2) {
            //女频
            //请求女生热文
            topRequest = getRecommend(2, 2, 4);
            //请求女生人气精选
            bottomRequest = getRecommend(2, 5, GET_DATA_PAGE_NUM);
        } else if (channel == 3) {
            //出版畅销
            topRequest = getRecommend(3, 6, 4);
            //请求出版人气精选
            bottomRequest = getRecommend(3, 7, GET_DATA_PAGE_NUM);
        } else {
            topRequest = getRecommend(0, 4, GET_DATA_PAGE_NUM);
            //推荐
            //请求猜你喜欢
            bottomRequest = getRecommend(0, 4, GET_DATA_PAGE_NUM);
        }


        BaseStatefulCallBack<ArrayList<BaseDataBean>> uiCallback = new BaseStatefulCallBack<ArrayList<BaseDataBean>>() {
            @Override
            public void execute() {
                if (channel == 0) {
                    editorRequest.execute();
                } else {
                    topRequest.execute();
                }
                bottomRequest.execute();
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
                    if (!bottomDataBeans.isEmpty()) {
                        mDataBeans.addAll(topDataBeans);
                        mDataBeans.addAll(bottomDataBeans);

                        uiCallback.invokeSuccess(mDataBeans);
                    }
                    return null;
                }
            }).onFail(new Function2<Integer, String, Unit>() {
                @Override
                public Unit invoke(Integer integer, String s) {
                    uiCallback.invokeFail(integer, s);
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
                    if (!topDataBeans.isEmpty()) {
                        mDataBeans.addAll(topDataBeans);
                        mDataBeans.addAll(bottomDataBeans);
                        uiCallback.invokeSuccess(mDataBeans);
                    }
                    return null;
                }
            }).onFail(new Function2<Integer, String, Unit>() {
                @Override
                public Unit invoke(Integer integer, String s) {
                    uiCallback.invokeFail(integer, s);
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
                    if (!bottomDataBeans.isEmpty()) {
                        mDataBeans.addAll(topDataBeans);
                        mDataBeans.addAll(bottomDataBeans);
                        uiCallback.invokeSuccess(mDataBeans);
                    }
                    return null;
                }
            }).onFail(new Function2<Integer, String, Unit>() {
                @Override
                public Unit invoke(Integer integer, String s) {
                    uiCallback.invokeFail(integer, s);
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
                    if (!topDataBeans.isEmpty()) {
                        mDataBeans.addAll(topDataBeans);
                        mDataBeans.addAll(bottomDataBeans);
                        uiCallback.invokeSuccess(mDataBeans);
                    }
                    return null;
                }
            }).onFail(new Function2<Integer, String, Unit>() {
                @Override
                public Unit invoke(Integer integer, String s) {
                    uiCallback.invokeFail(integer, s);
                    return null;
                }
            });
        }
        return uiCallback;
    }
}
