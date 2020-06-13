package com.inveno.xiandu.db;

import android.content.Context;

import com.inveno.xiandu.applocation.MainApplication;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.gen.BookShelfDao;
import com.inveno.xiandu.gen.ChapterInfoDao;
import com.inveno.xiandu.http.DDManager;
import com.inveno.xiandu.http.body.BaseRequest;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
     * 是否包含某本书
     */
    public boolean hasBookShelf(BookShelf bookShelf) {
        BookShelf bookShelf1 = DaoManager.getInstance(context).bookShelfDao.loadByRowId(bookShelf.getContent_id());
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
        if (bookShelf.getBookChapters() != null)
            DaoManager.getInstance(context).chapterInfoDao.insertOrReplaceInTx(bookShelf.getBookChapters());
        //上传服务器
        DDManager.getInstance().addBookShelf(bookShelf.getContent_id(), 1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<BaseRequest<List<BookShelf>>>() {
                    @Override
                    public void accept(BaseRequest<List<BookShelf>> listBaseRequest) throws Exception {
                    }
                });
    }

    /**
     * 批量更新本地书架
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
        DDManager.getInstance().updateBookShelf(bookShelf.getContent_id(), -1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<BaseRequest<List<BookShelf>>>() {
                    @Override
                    public void accept(BaseRequest<List<BookShelf>> listBaseRequest) throws Exception {
                    }
                });
    }

}
