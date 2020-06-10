package com.inveno.xiandu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.inveno.xiandu.gen.BookShelfDao;
import com.inveno.xiandu.gen.DaoMaster;
import com.inveno.xiandu.gen.DaoSession;
import com.inveno.xiandu.utils.LogUtils;
import com.inveno.xiandu.utils.Toaster;

import org.greenrobot.greendao.database.Database;

import java.io.InputStream;

/**
 * Created By huzheng
 * Date 2020/3/5
 * Des 数据库管理
 */
public class DaoManager {

    private static DaoManager daoManager;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    //书架
    public BookShelfDao bookShelfDao;

    public static DaoManager getInstance(Context context) {
        if (daoManager == null) {
            synchronized (DaoManager.class) {
                if (daoManager == null) {
                    daoManager = new DaoManager(context);
                }
            }
        }
        return daoManager;
    }

    private DaoManager(final Context context) {
//        DaoMaster.DevOpenHelper manhuagou = new DaoMaster.DevOpenHelper(context, "manhuagou-db", null);
        //根据版本升级数据库
        DaoMaster.DevOpenHelper manhuagou = new DaoMaster.DevOpenHelper(context, "manhuagou-db") {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                String basefolder = "updateSql";
                StringBuffer sb_exesql;
                InputStream inputStream;
                int length = -1;
                for (int i = oldVersion; i < newVersion; i++) {
                    try {
                        sb_exesql = new StringBuffer();
                        length = -1;
                        byte[] bts = new byte[1024 * 12];
                        inputStream = context.getAssets().open(basefolder + "/" + (oldVersion + 1) + ".sql");
                        while ((length = inputStream.read(bts)) != -1) {
                            sb_exesql.append(new String(bts, 0, length));
                        }
                        db.execSQL(sb_exesql.toString());
                    } catch (Exception e) {
                        LogUtils.H(e.getMessage());
                        Toaster.showToastCenter(context, "数据异常，建议重新安装！");
                    }
                }
            }
        };
        SQLiteDatabase readableDatabase = manhuagou.getReadableDatabase();
        daoMaster = new DaoMaster(readableDatabase);
        daoSession = daoMaster.newSession();

        bookShelfDao = daoSession.getBookShelfDao();
    }
}
