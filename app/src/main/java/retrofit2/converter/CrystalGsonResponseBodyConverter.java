package retrofit2.converter;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.http.BaseResponseObj;

/**
 * Created by Jin Liang on 2017/5/10.
 */

public class CrystalGsonResponseBodyConverter<T extends BaseResponseObj> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private Class<T> clazz;

    CrystalGsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.clazz = (Class<T>) type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String result = value.string();
        Log.d("BodyConverter", "origin response=" + result);
        T t = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("data")) {
                String data = jsonObject.getString("data");
                if (!TextUtils.isEmpty(data)) {
                    t = JSON.parseObject(data, clazz);
                } else {
                    t = clazz.newInstance();
                }
            } else {
                t = clazz.newInstance();
            }

            if (jsonObject.has("message")) {
                t.setMessage(jsonObject.getString("message"));
            }
            if (jsonObject.has("status")) {
                t.setStatus(jsonObject.getInt("status"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        Log.d("BodyConverter", "passed response=" + GsonUtil.toJson(t));
        return t;
    }
}
