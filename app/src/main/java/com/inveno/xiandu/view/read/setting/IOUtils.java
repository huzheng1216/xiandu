package com.inveno.xiandu.view.read.setting;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created By huzheng
 * Date 2020/5/13
 * Des
 */
public class IOUtils {

    public static void close(Closeable closeable){
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
            //close error
        }
    }
}
