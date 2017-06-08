package retrofit2.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Jin Liang on 2017/5/10.
 */

public class CrystalGsonConverterFactory extends Converter.Factory {

    public static CrystalGsonConverterFactory create() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return create(gson);
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static CrystalGsonConverterFactory create(Gson gson) {
        return new CrystalGsonConverterFactory(gson);
    }

    private final Gson gson;

    private CrystalGsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new CrystalGsonResponseBodyConverter<>(gson, type);
    }

}
