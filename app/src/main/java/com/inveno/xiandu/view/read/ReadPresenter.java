package com.inveno.xiandu.view.read;

import android.text.TextUtils;

import com.inveno.xiandu.bean.book.BookChapter;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.view.read.adapter.RxPresenter;
import com.inveno.xiandu.view.read.page.BookManager;
import com.inveno.xiandu.view.read.setting.IOUtils;

import org.reactivestreams.Subscription;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zheng.hu on 17-5-16.
 */

public class ReadPresenter extends RxPresenter<ReadContract.View>
        implements ReadContract.Presenter {
    private static final String TAG = "ReadPresenter";

    private Disposable mChapterSub;

    @Override
    public void loadCategory(String bookId) {
        DDManager.getInstance().getChapterList(bookId, 1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRequest<BookChapter>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseRequest<BookChapter> bookChapterBaseRequest) {
                        mView.showCategory(bookChapterBaseRequest.getData().getChapter_list());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void loadChapter(String bookId, List<ChapterInfo> bookChapters) {

        //取消上次的任务，防止多次加载
        if (mChapterSub != null && !mChapterSub.isDisposed()) {
            mChapterSub.dispose();
        }

        List<Observable<BaseRequest<ChapterInfo>>> list = new ArrayList<>(bookChapters.size());
        for (ChapterInfo chapterInfo : bookChapters) {
            if (!TextUtils.isEmpty(chapterInfo.getContent()))
                continue;
            list.add(DDManager.getInstance().getChapterInfo(bookId, chapterInfo.getChapter_id()+""));
        }
        LogUtils.H("hahahahah");
        Observable.concat(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BaseRequest<ChapterInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mChapterSub = d;
                    }

                    @Override
                    public void onNext(BaseRequest<ChapterInfo> chapterInfoBaseRequest) {
                        ChapterInfo data = chapterInfoBaseRequest.getData();
                        saveChapterInfo(bookId, data.getChapter_id()+"", data.getContent());
                        mView.finishChapter();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //只有第一个加载失败才会调用errorChapter
                        mView.errorChapter();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

//        List<Single<ChapterInfoBean>> chapterInfos = new ArrayList<>(bookChapters.size());
//        ArrayDeque<String> titles = new ArrayDeque<>(bookChapters.size());
//
//        // 将要下载章节，转换成网络请求。
//        for (int i = 0; i < size; ++i) {
//            ChapterInfo bookChapter = bookChapters.get(i);
//            // 网络中获取数据
////            Single<ChapterInfoBean> chapterInfoSingle = RemoteRepository.getInstance()
////                    .getChapterInfo(bookChapter.getLink());
////
////            chapterInfos.add(chapterInfoSingle);
////
////            titles.add(bookChapter.getTitle());
//        }
//
//        Single.concat(chapterInfos)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        new Subscriber<ChapterInfoBean>() {
//                            String title = titles.poll();
//
//                            @Override
//                            public void onSubscribe(Subscription s) {
//                                s.request(Integer.MAX_VALUE);
//                                mChapterSub = s;
//                            }
//
//                            @Override
//                            public void onNext(ChapterInfoBean chapterInfoBean) {
//                                //存储数据
////                                BookRepository.getInstance().saveChapterInfo(
////                                        bookId, title, chapterInfoBean.getBody()
////                                );
//                                mView.finishChapter();
//                                //将获取到的数据进行存储
//                                title = titles.poll();
//                            }
//
//                            @Override
//                            public void onError(Throwable t) {
//                                //只有第一个加载失败才会调用errorChapter
//                                if (bookChapters.get(0).getTitle().equals(title)) {
//                                    mView.errorChapter();
//                                }
//                                LogUtils.H(t.getMessage());
//                            }
//
//                            @Override
//                            public void onComplete() {
//                            }
//                        }
//                );
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mChapterSub != null && !mChapterSub.isDisposed()) {
            mChapterSub.dispose();
        }
    }



    /**
     * 存储章节
     * @param folderName
     * @param fileName
     * @param content
     */
    public void saveChapterInfo(String folderName,String fileName,String content){
        File file = BookManager.getBookFile(folderName, fileName);
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

}
