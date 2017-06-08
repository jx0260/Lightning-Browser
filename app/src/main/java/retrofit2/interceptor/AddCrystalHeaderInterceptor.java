package retrofit2.interceptor;


import java.io.IOException;

import acr.browser.lightning.app.CrystalContext;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 所有http请求出口，通过okhttp interceptor添加公有header
 * Created by Jin Liang on 2017/5/9.
 */

public class AddCrystalHeaderInterceptor implements Interceptor {

    private String deviceId;

    public AddCrystalHeaderInterceptor(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oriRequest = chain.request();

        String keySessionId = CrystalContext.getInstance().getKeySessionId();
        if(null == keySessionId){
            keySessionId = "";
        }
        Request addHeaderRequest = oriRequest.newBuilder()
                .addHeader("KEY_SESSIONID", keySessionId)
                .addHeader("KEY_DEVICEID", deviceId)
                .addHeader("deviceType", "2")
                .addHeader("version", "1.0.1").build();

        return chain.proceed(addHeaderRequest);
    }
}
