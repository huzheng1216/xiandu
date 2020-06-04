package com.inveno.xiandu.db;

import android.content.Context;

import com.inveno.xiandu.bean.book.Book;
import com.inveno.xiandu.bean.book.BookCatalog;
import com.inveno.xiandu.bean.book.BookShelf;
import com.inveno.xiandu.gen.BookCatalogDao;
import com.inveno.xiandu.gen.BookDao;
import com.inveno.xiandu.gen.BookShelfDao;
import com.inveno.xiandu.utils.StringTools;

import java.util.Collection;
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
    //书籍数据
    private List<Book> bookDaos;

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

    /**
     * 获取全部书架书架
     */
    public List<Book> getAllBook() {
        return DaoManager.getInstance(context).bookDao.queryBuilder().orderDesc(BookDao.Properties.UpdateTime).list();
    }

    /**
     * 书架添加书籍
     *
     * @param bookShelf
     * @return
     */
    public boolean insertBook(Book bookShelf) {
        if (StringTools.isEmpty(bookShelf.getName()) || StringTools.isEmpty(bookShelf.getUrl())) {
            return false;
        }
        long l = DaoManager.getInstance(context).bookDao.insertOrReplace(bookShelf);
        if (bookShelf.getBookCatalogs().size() > 0) {
            //插入章节
            for (BookCatalog bookCatalog : bookShelf.getBookCatalogs()) {
                bookCatalog.setBookId(bookShelf.getId());
            }
            DaoManager.getInstance(context).bookCatalogDao.insertOrReplaceInTx(bookShelf.getBookCatalogs());
        }
        return true;
    }

    /**
     * 更新书籍
     *
     * @param remove
     */
    public void updateBook(Book remove) {
        remove.setUpdateTime(System.currentTimeMillis());
        DaoManager.getInstance(context).bookDao.update(remove);
    }

    /**
     * 删除书籍
     *
     * @param remove
     */
    public void delBoot(Book remove) {
        //删除书籍
        DaoManager.getInstance(context).bookDao.delete(remove);
        //删除对应的目录列表
        List<BookCatalog> list = DaoManager.getInstance(context).bookCatalogDao.queryBuilder().where(BookCatalogDao.Properties.BookId.eq(remove.getId())).list();
        for (BookCatalog bookCatalog : list) {
            DaoManager.getInstance(context).bookCatalogDao.delete(bookCatalog);
        }
    }

    /**
     * 获取书架上的书籍
     * @param id
     * @return
     */
    public List<Book> getAllBookByShelfId(Long id) {
        return DaoManager.getInstance(context).bookDao.queryBuilder().orderDesc(BookDao.Properties.UpdateTime).where(BookDao.Properties.ShelfId.eq(id)).list();
    }

    /**
     * 查询书架上的书籍
     * @param name
     * @return
     */
    public List<Book> queryBookByName(String name) {
        return DaoManager.getInstance(context).bookDao.queryBuilder().orderDesc(BookDao.Properties.UpdateTime).where(BookDao.Properties.Name.eq(name)).list();
    }
}
