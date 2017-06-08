package retrofit2.interceptor;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import acr.browser.lightning.utils.SHA1Utils;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * SHA1 参数加密
 * Created by Jin Liang on 2017/5/9.
 */

public class SHA1EncryptInterceptor implements Interceptor {

    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_SIGNATURE = "signature";

    private static final String TAG = "SHA1EncryptInterceptor";
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private MediaType FORM = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");

    private Gson mGson = new Gson();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oriRequest = chain.request();

        if (oriRequest.body() instanceof MultipartBody) {
            return chain.proceed(oriRequest);
        }

        Request resultRequest = oriRequest;
        //获取到方法
        String method = oriRequest.method();
        try {
            HashMap<String, Object> paramsMap = new HashMap<>();

            //get请求的封装
            if (method.equals("GET")) {
                Log.e(TAG, "暂不支持 GET 请求，请改为使用 POST 方式。");
            } else if (method.equals("POST")) {

                RequestBody requestBody = oriRequest.body();

                if (requestBody instanceof FormBody) {
                    for (int i = 0; i < ((FormBody) requestBody).size(); i++) {
                        paramsMap.put(((FormBody) requestBody).name(i), ((FormBody) requestBody).value(i));
                    }
                }
            }

            // 添加 timestamp参数
            if (paramsMap.get(KEY_TIMESTAMP) == null) {
                paramsMap.put(KEY_TIMESTAMP, Calendar.getInstance().getTimeInMillis());
            }

            // 对所有参数进行签名
            String signatureStr = SHA1Utils.makeSignature(paramsMap, SHA1Utils.KEY);
            if (!TextUtils.isEmpty(signatureStr)) {
                paramsMap.put(KEY_SIGNATURE, signatureStr);
            }

            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                String val = String.valueOf(paramsMap.get(key));
                bodyBuilder.add(key, val);
            }
            resultRequest = oriRequest.newBuilder().post(bodyBuilder.build()).build();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return chain.proceed(resultRequest);
    }
}
