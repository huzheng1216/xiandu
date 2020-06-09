package com.inveno.android.basics.service.app.context;

import java.util.HashMap;
import java.util.Map;

public class InstanceContext {

    private static class AppContextHolder{
        private static final InstanceContext sInstance = new InstanceContext();
    }


    public static InstanceContext get() {
        return AppContextHolder.sInstance;
    }

    private Map<Class<? extends BaseSingleInstanceService>,BaseSingleInstanceService> mInstanceMap = new HashMap<>();

    public <T extends BaseSingleInstanceService> T getInstance(Class<T> cls){
           return ensure(cls);
    }

    private <T extends BaseSingleInstanceService> T doInstall(Class<T> cls){
        synchronized (mInstanceMap){
            try {
                T result = cls.newInstance();
                result.onCreate();
                mInstanceMap.put(cls,result);
                return result;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private <T extends BaseSingleInstanceService> T ensure(Class<T> cls){
        if(mInstanceMap.containsKey(cls)){
            return (T) mInstanceMap.get(cls);
        }else{
            return doInstall(cls);
        }
    }
}
