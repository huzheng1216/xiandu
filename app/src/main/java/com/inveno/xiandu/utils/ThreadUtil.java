package com.inveno.xiandu.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtil {
    private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

    private static ExecutorService sNetWorkService;
    private static Handler sUiService;
    private static ExecutorService sIoService;
    private static ExecutorService sUiPrepareService;
    private static ExecutorService sEventService;
    private static Handler sBackHandler;
    private static HandlerThread sBackHandlerThread;

    public static void init(){
        sNetWorkService = Executors.newCachedThreadPool(new NamedThreadFactory("network"));
        sEventService = Executors.newCachedThreadPool(new NamedThreadFactory("event"));
        sUiService = new Handler(Looper.getMainLooper());
        sIoService = Executors.newCachedThreadPool(new NamedThreadFactory("io"));
        sUiPrepareService = Executors.newFixedThreadPool(5,new NamedThreadFactory("ui_prepare"));
        sBackHandlerThread = new HandlerThread("back_handler");
        sBackHandlerThread.start();
        sBackHandler = new Handler(sBackHandlerThread.getLooper());
    }

    static class NamedThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public NamedThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = name+"-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    public static void initTest(){
        sNetWorkService = Executors.newCachedThreadPool();
        sIoService = Executors.newCachedThreadPool();
    }

    public static void execOnNetWork(Runnable runnable){
        sNetWorkService.execute(runnable);
    }

    public static void execOnUi(Runnable runnable){
        logger.info("execOnUi runnable:{}",runnable.toString());
        sUiService.post(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public static void executeOnEvent(Runnable runnable){
        logger.info("executeOnUiPrepare runnable:{}",runnable.toString());
        sEventService.execute(runnable);
    }

    public static void executeOnUiPrepare(Runnable runnable){
        logger.info("executeOnUiPrepare runnable:{}",runnable.toString());
        try {
            sUiPrepareService.execute(runnable);
        }catch (Exception e){
            logger.warn("executeOnUiPrepare:{}",e.getMessage());
        }
    }

    public static void executeDelayOnUi(Runnable runnable,long delay){
        logger.info("executeDelayOnUi runnable:{}",runnable.toString());
        sUiService.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },delay);
    }

    public static Handler getsUiService() {
        return sUiService;
    }

    public static Handler getsBackHandler() {
        return sBackHandler;
    }

    public static void cancelExecute(Runnable runnable){
        sUiService.removeCallbacks(runnable);
    }

    public static void execOnIo(Runnable runnable){
        sIoService.execute(runnable);
    }

    public static void release() {
        sNetWorkService.shutdownNow();
        sNetWorkService = null;
        sUiService = null;
        sIoService.shutdownNow();
        sIoService = null;
    }

    public static class ExecutorServiceHolder{
        public static ExecutorService getNetworkService(){
            return sNetWorkService;
        }
    }
}
