package com.inveno.xiandu.db;

import android.content.Context;

import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.gen.BookShelfDao;

import java.util.List;

/**
 * Created By huzheng
 * Date 2020/3/5
 * Des sql数据
 */
public class SQL {

    private Context context;
    private static SQL daoManager;
    //书架
    private List<BookShelf> bookShelves;

    public static SQL getInstance(Context context) {
        if (daoManager == null) {
            synchronized (SQL.class) {
                if (daoManager == null) {
                    daoManager = new SQL(context);
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
     * 添加书架
     * @param bookShelf
     * @return
     */
    public long addBookShelf(BookShelf bookShelf) {
        return DaoManager.getInstance(context).bookShelfDao.insertOrReplace(bookShelf);
    }

}
