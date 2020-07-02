package com.inveno.xiandu.db;

import android.content.Context;

import com.inveno.android.api.service.InvenoServiceContext;
import com.inveno.xiandu.applocation.MainApplication;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.Bookbrack;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.bean.book.ReadTrack;
import com.inveno.xiandu.gen.BookShelfDao;
import com.inveno.xiandu.gen.BookbrackDao;
import com.inveno.xiandu.gen.ChapterInfoDao;
import com.inveno.xiandu.gen.ReadTrackDao;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;
import com.inveno.xiandu.invenohttp.instancecontext.APIContext;
import com.inveno.xiandu.invenohttp.instancecontext.ServiceContext;
import com.inveno.xiandu.utils.Toaster;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

/**
 * Created By huzheng
 * Date 2020/3/5
 * Des sql数据
 */
public class SQL {

    private Context context;
    private static SQL daoManager;

    public static SQL getInstance() {
        if (daoManager == null) {
            synchronized (SQL.class) {
                if (daoManager == null) {
                    daoManager = new SQL(MainApplication.getContext());
                }
            }
        }
        return daoManager;
    }

    private SQL(Context context) {
        this.context = context;
    }

    /**
     * 获取全部书架书架
     */
    public List<BookShelf> getAllBookShelf() {
        return DaoManager.getInstance(context).bookShelfDao.queryBuilder().orderDesc(BookShelfDao.Properties.Time).list();
    }

    /**
     * 获取全部书架书架
     */
    public List<Bookbrack> getAllBookbrack() {
        return DaoManager.getInstance(context).bookbrackDao.queryBuilder().orderDesc(BookbrackDao.Properties.Time).list();
    }

    /**
     * 根据id获取某本书
     */
    public BookShelf getBookShelf(long content_id) {
        return DaoManager.getInstance(context).bookShelfDao.queryBuilder().where(BookShelfDao.Properties.Content_id.eq(content_id)).unique();
    }

    /**
     * 根据id获取某本书
     */
    public Bookbrack getBookbrack(long content_id) {
        return DaoManager.getInstance(context).bookbrackDao.queryBuilder().where(BookbrackDao.Properties.Content_id.eq(content_id)).unique();
    }

    /**
     * 是否包含某本书
     */
    public boolean hasBookShelf(BookShelf bookShelf) {
        BookShelf bookShelf1 = DaoManager.getInstance(context).bookShelfDao.loadByRowId(bookShelf.getContent_id());
        return bookShelf1 != null;
    }

    /**
     * 是否包含某本书
     */
    public boolean hasBookbrack(Bookbrack bookbrack) {
        Bookbrack bookShelf1 = DaoManager.getInstance(context).bookbrackDao.loadByRowId(bookbrack.getContent_id());
        return bookShelf1 != null;
    }

    /**
     * 是否包含某本书
     */
    public boolean hasBookbrack(Long contentId) {
        Bookbrack bookShelf1 = DaoManager.getInstance(context).bookbrackDao.loadByRowId(contentId);
        return bookShelf1 != null;
    }

    /**
     * 添加书架书籍
     *
     * @param bookShelf
     * @return
     */
    public void addBookShelf(BookShelf bookShelf) {
        bookShelf.setTime(System.currentTimeMillis() + "");
        DaoManager.getInstance(context).bookShelfDao.insertOrReplace(bookShelf);
//        if (bookShelf.getBookChapters() != null)
//            DaoManager.getInstance(context).chapterInfoDao.insertOrReplaceInTx(bookShelf.getBookChapters());
        //上传服务器
//        DDManager.getInstance().addBookShelf(bookShelf.getContent_id(), 1)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new Consumer<BaseRequest<List<BookShelf>>>() {
//                    @Override
//                    public void accept(BaseRequest<List<BookShelf>> listBaseRequest) throws Exception {
//                    }
//                });
    }

    /**
     * 添加书籍阅读记录
     *
     * @param bookbrack
     * @return
     */
    public void addBookbrack(Bookbrack bookbrack) {
        bookbrack.setTime(System.currentTimeMillis() + "");
        DaoManager.getInstance(context).bookbrackDao.insertOrReplace(bookbrack);
        //上传服务器
        APIContext.bookbrackApi().addBookbrack(InvenoServiceContext.uid().getUid(), ServiceContext.userService().getUserPid(), bookbrack.getContent_id())
                .onSuccess(new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        return null;
                    }
                }).execute();
    }

    /**
     * 批量更新本地书架
     *
     * @param bookShelves
     */
    public void insertOrReplace(List<BookShelf> bookShelves) {
        long l = System.currentTimeMillis();
        for (BookShelf bookShelf : bookShelves) {
            bookShelf.setTime(l + "");
        }
        DaoManager.getInstance(context).bookShelfDao.insertOrReplaceInTx(bookShelves);
    }

    /**
     * 批量更新本地书架
     *
     * @param bookbracks
     */
    public void insertOrReplaceBookbrack(List<Bookbrack> bookbracks) {
        long l = System.currentTimeMillis();
        for (Bookbrack bookShelf : bookbracks) {
            bookShelf.setTime(l + "");
        }
        DaoManager.getInstance(context).bookbrackDao.insertOrReplaceInTx(bookbracks);
    }

    /**
     * 移除书架上的书籍
     *
     * @param bookShelf
     * @return
     */
    public void delBookShelf(BookShelf bookShelf) {
        //删除书籍
        DaoManager.getInstance(context).bookShelfDao.delete(bookShelf);
        //删除章节
        QueryBuilder<ChapterInfo> chapterInfoQueryBuilder = DaoManager.getInstance(context).chapterInfoDao.queryBuilder();
        DeleteQuery<ChapterInfo> chapterInfoDeleteQuery = chapterInfoQueryBuilder.where(ChapterInfoDao.Properties.Content_id.eq(bookShelf.getContent_id())).buildDelete();
        chapterInfoDeleteQuery.executeDeleteWithoutDetachingEntities();
        //上传服务器
//        DDManager.getInstance().updateBookShelf(bookShelf.getContent_id(), -1)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new Consumer<BaseRequest<List<BookShelf>>>() {
//                    @Override
//                    public void accept(BaseRequest<List<BookShelf>> listBaseRequest) throws Exception {
//                    }
//                });
    }

    /**
     * 移除书架上的书籍
     *
     * @param bookbracks
     * @return
     */
    public void delBookbrack(List<Bookbrack> bookbracks) {
        ArrayList<Long> contentIds = new ArrayList<>();
        for (Bookbrack bookbrack : bookbracks) {
            //删除书籍
            DaoManager.getInstance(context).bookbrackDao.delete(bookbrack);
            //删除章节
            QueryBuilder<ChapterInfo> chapterInfoQueryBuilder = DaoManager.getInstance(context).chapterInfoDao.queryBuilder();
            DeleteQuery<ChapterInfo> chapterInfoDeleteQuery = chapterInfoQueryBuilder.where(ChapterInfoDao.Properties.Content_id.eq(bookbrack.getContent_id())).buildDelete();
            chapterInfoDeleteQuery.executeDeleteWithoutDetachingEntities();

            contentIds.add(bookbrack.getContent_id());
        }
        APIContext.bookbrackApi().updataBookbrack(InvenoServiceContext.uid().getUid(), ServiceContext.userService().getUserPid(), contentIds, -1)
                .onSuccess(new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        return null;
                    }
                }).execute();
    }

    /**
     * 获取全部足迹图书
     */
    public List<ReadTrack> getAllReadTrack() {
        return DaoManager.getInstance(context).readTrackDao.queryBuilder().orderDesc(ReadTrackDao.Properties.Time).list();
    }

    /**
     * 足迹是否包含某本书
     */
    public boolean hasReadTrack(Long contentId) {
        ReadTrack readTrack = DaoManager.getInstance(context).readTrackDao.loadByRowId(contentId);
        return readTrack != null;
    }

    /**
     * 添加足迹书籍
     *
     * @param readTrack
     * @return
     */
    public void addReadTrack(ReadTrack readTrack) {
        readTrack.setTime(System.currentTimeMillis() + "");
        DaoManager.getInstance(context).readTrackDao.insertOrReplace(readTrack);
        //上传服务器
        APIContext.bookbrackApi().addBookbrack(InvenoServiceContext.uid().getUid(), ServiceContext.userService().getUserPid(), readTrack.getContent_id())
                .onSuccess(new Function1<String, Unit>() {
                    @Override
                    public Unit invoke(String s) {
                        return null;
                    }
                })
                .onFail(new Function2<Integer, String, Unit>() {
                    @Override
                    public Unit invoke(Integer integer, String s) {
                        return null;
                    }
                }).execute();
    }

    /**
     * 批量更新本地足迹
     *
     * @param readTracks
     */
    public void insertOrReplaceReadTrack(List<ReadTrack> readTracks) {
        long l = System.currentTimeMillis();
        for (ReadTrack readTrack : readTracks) {
            readTrack.setTime(l + "");
        }
        DaoManager.getInstance(context).readTrackDao.insertOrReplaceInTx(readTracks);
    }


    /**
     * 移除足迹的书籍
     *
     * @param readTracks
     * @return
     */
    public void delReadTrack(List<ReadTrack> readTracks) {
        ArrayList<Long> contentIds = new ArrayList<>();
        for (ReadTrack readTrack : readTracks) {
            //删除书籍
            DaoManager.getInstance(context).readTrackDao.delete(readTrack);
            //删除章节
            QueryBuilder<ChapterInfo> chapterInfoQueryBuilder = DaoManager.getInstance(context).chapterInfoDao.queryBuilder();
            DeleteQuery<ChapterInfo> chapterInfoDeleteQuery = chapterInfoQueryBuilder.where(ChapterInfoDao.Properties.Content_id.eq(readTrack.getContent_id())).buildDelete();
            chapterInfoDeleteQuery.executeDeleteWithoutDetachingEntities();

            APIContext.readTrackApi().deleteReadTrack(InvenoServiceContext.uid().getUid(), ServiceContext.userService().getUserPid(), readTrack.getContent_id())
                    .onSuccess(new Function1<String, Unit>() {
                        @Override
                        public Unit invoke(String s) {
                            return null;
                        }
                    })
                    .onFail(new Function2<Integer, String, Unit>() {
                        @Override
                        public Unit invoke(Integer integer, String s) {
                            return null;
                        }
                    }).execute();
        }
    }
}
