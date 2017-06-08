package retrofit2;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import acr.browser.lightning.utils.Installation;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.CrystalGsonConverterFactory;
import retrofit2.interceptor.AddCrystalHeaderInterceptor;
import retrofit2.interceptor.SHA1EncryptInterceptor;

/**
 * 全校版 Retrofit工厂
 */
public class CrystalRetrofitFactory {

    private static Context context;

    private static OkHttpClient crystalHttpClient;

    public static void init(Context context){
        CrystalRetrofitFactory.context = context;

        crystalHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(getPrintLogInterceptor())
                .addInterceptor(new AddCrystalHeaderInterceptor(Installation.id(context)))
                .addInterceptor(new SHA1EncryptInterceptor())
//                .addInterceptor(new AddCacheInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private static Interceptor getPrintLogInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.d("aedu", "OkHttp3Utils/message " + message);
                    }
                });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    public static<T> T create(String baseUrl, final Class<T> service){
        return  (T) new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(CrystalGsonConverterFactory.create())
                .client(crystalHttpClient)
                .build()
                .create(service);
    }
}