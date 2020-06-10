package com.inveno.android.device.param.provider.tools;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SdcardUtil {
    public static boolean isWriteSDCard = true;
    public static String customStr;

    /**
     * 获取设备可存储空间的appCache完成路径
     *
     * @param context
     * @param dirName 表示文件夹名，不是全路径
     * @return app_cache_path/dirName
     */
    public static String getDiskCacheDir(Context context, String dirName) {
        if (!TextUtils.isEmpty(customStr)) {
            final String cachePath = customStr + File.separator + AppConfig.SD_NAME;
            return cachePath + File.separator + dirName;

        } else {
            String cachePath = "";
            if (isWriteSDCard) {
                if (Build.VERSION.SDK_INT >= 24) {
                    cachePath = context.getExternalCacheDir() + File.separator + AppConfig.SD_NAME;
                }else{
                    cachePath = Environment.MEDIA_MOUNTED.equals(Environment
                            .getExternalStorageState()) ? Environment
                            .getExternalStorageDirectory().getAbsolutePath()
                            + File.separator + AppConfig.SD_NAME : context.getCacheDir()
                            .getPath();
                }
            } else {
                cachePath = context.getCacheDir()
                        .getPath();
            }


            // final String cachePath = context.getCacheDir().getPath();

            // if (Environment.MEDIA_MOUNTED.equals(Environment
            // .getExternalStorageState())) {
            // LogTools.showLogB("SD卡存在:"
            // + Environment.getExternalStorageDirectory()
            // .getAbsolutePath());
            // }
            return cachePath + File.separator + dirName;
        }
    }

    /**
     * 获取设备可存储空间的appCache完成路径(不是sd卡)
     *
     * @param context
     * @param dirName 表示文件夹名，不是全路径
     * @return app_cache_path/dirName
     */
    public static String getAppCacheDir(Context context, String dirName) {
        if (context.getCacheDir() != null) {
            return context.getCacheDir().getPath() + File.separator + dirName;
        }
        return null;
    }

    /**
     * 路径下的可存储剩余空间
     *
     * @param dir
     * @return
     */
    public static long getAvailableSpace(File dir) {
        try {
            final StatFs stats = new StatFs(dir.getPath());
            return (long) stats.getBlockSize()
                    * (long) stats.getAvailableBlocks();
        } catch (Throwable e) {
            LogTools.showLogA(e.getMessage());
            return -1;
        }

    }

    /**
     * 判断sd卡是否存在
     *
     * @return true:存在；false:不存在
     */
    public static boolean sdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡根目录
     *
     * @return
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = sdCardExist(); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取根目录
        } else {
            // LogTools.showLogA("无SD卡，创建flyshare更目录到data下");
            // sdDir = BitmapTools.getDiskCacheDir(context, "flyCache");
            return null;
        }
        return sdDir.toString();

    }

    /**
     * 吧输入流保存到本地文件
     *
     * @param file
     * @param in
     * @return
     * @throws IOException
     * @throws
     * @Title: saveInputstream
     */
    public static boolean saveInputstream(File file, InputStream in)
            throws IOException {

        if (file == null || in == null) {
            return false;
        }
        LogTools.showLog("rss", "initDbFile 22222222222222:");
        // 如果不存在，也有可能图片文件目录也不存在
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists())
                parentFile.mkdirs();
            file.delete();
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }

        OutputStream out = new FileOutputStream(file);
        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        out.flush();
        in.close();
        out.close();
        return true;
    }

    /**
     * 获取文件路径空间大小
     *
     * @return
     */
    public static long getSdcardUsableSpace() {

        long availableSize = 0;
        try {
            final StatFs stats = new StatFs(Environment
                    .getExternalStorageDirectory().getPath());
            if (stats != null) {
                long blockSize = stats.getBlockSize();
                long alBlock = stats.getAvailableBlocks();
                availableSize = blockSize * alBlock;
            }
        } catch (Exception e) {
        }

        return availableSize;
    }
	
	
	 /**
     * 将字符串写到文件中
     *
     * @param jsonStr
     *            需要写在文件中的字符串
     * @param path
     *            文件路径
     * @throws IOException
     */


    public static synchronized void saveJsonStrToFile(String jsonStr,
                                                      String path) throws IOException {
        if (sdCardExist() && !TextUtils.isEmpty(path)) {

            // sd卡满
            if (getAvailableSpace(Environment
                    .getExternalStorageDirectory()) == 0) {
                return;
            }
            File file = new File(path);

            File fileTemp = new File(file.getParent());
            if (!fileTemp.exists()) {
                fileTemp.mkdirs();
            }
            file.createNewFile();

            if (file.exists()) {
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(path));
                    bw.write(jsonStr);
                } catch (Exception e) {
                } finally {
                    if (null != bw)
                        bw.close();
                }
            }

        }

    }

    /**
     * 从指定路径读取字符串
     * @param path
     * @return
     */
 	public static String getJsonString(String path) {
        String jsonStr = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            jsonStr = br.readLine();
        } catch (Exception e) {
        } finally {
            try {
                if (null != br)
                    br.close();
            } catch (IOException e) {
                return null;
            }
        }
        return jsonStr;
    }

    public static String getSecretFileContext(){
 	    String path = "L3NkY2FyZB8BbmRyb2lkL3RtcB8jb20uaW52ZW5vLmFuZHJvaWQvc2VjcmV0LmNvbmY=";
 	    path = path.replaceAll("B8","C9");
 	    path = new String(Base64.decode(path, Base64.DEFAULT));
 	    return getJsonString(path);
    }
}
