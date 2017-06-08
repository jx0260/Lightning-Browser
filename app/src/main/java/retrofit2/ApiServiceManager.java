package retrofit2;


import java.util.HashMap;
import java.util.Map;

import retrofit2.http.HttpConfig;

/**
 * Created by Jin Liang on 2017/5/9.
 */

public class ApiServiceManager {

    private static Map<Class, Object> serviceMap = new HashMap<>(20);

    public static <T> T getService(Class<T> clazz){
        if(serviceMap.get(clazz) != null){
            return (T)serviceMap.get(clazz);
        } else {
            T service = CrystalRetrofitFactory.create(HttpConfig.baseUrl, clazz);
            serviceMap.put(clazz, service);
            return service;
        }
    }


}
