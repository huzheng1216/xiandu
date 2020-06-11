package com.inveno.xiandu.db;

import android.content.Context;

import com.inveno.xiandu.applocation.MainApplication;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.bean.book.ChapterInfo;
import com.inveno.xiandu.gen.BookShelfDao;
import com.inveno.xiandu.gen.ChapterInfoDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

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
//        bookShelfDaos = DaoManager.getInstance(context).getBookShelfDao().queryRaw("");
//        sourceDaos = DaoManager.getInstance(context).getSourceDao().queryRaw("");
    }

    /**
     * 获取全部书架书架
     */
    public List<BookShelf> getAllBookShelf() {
        return DaoManager.getInstance(context).bookShelfDao.queryBuilder().orderDesc(BookShelfDao.Properties.Time).list();
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
        DaoManager.getInstance(context).chapterInfoDao.insertOrReplaceInTx(bookShelf.getBookChapters());
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
    }

}
