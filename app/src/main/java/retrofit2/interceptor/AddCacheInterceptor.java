package retrofit2.interceptor;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 数据返回后，新增或更新缓存
 * Created by Jin Liang on 2017/5/14.
 */

public class AddCacheInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        return null;
    }

    /*@Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!(request.body() instanceof FormBody)) {
            return chain.proceed(request);
        }
        FormBody body = (FormBody) request.body();

        String cacheKey = generateCacheKey(request.url().url().toString(), body);

        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();

        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(UTF8);
        }

        String cacheValue = "";
        if (contentLength != 0) {
            cacheValue = buffer.clone().readString(charset);
        }

        // 需要缓存
        if (cacheValue.contains("cacheTimeLength")) {
            Date now = new Date();
            Pattern p = Pattern.compile("\"cacheTimeLength\":(\\d+)");
            Matcher m = p.matcher(cacheValue);
            if (m.find()) {
                long cacheTimeLength = Long.valueOf(m.group(1)); //秒
                Date expiresDate = new Date(now.getTime() + cacheTimeLength * 1000);

                DaoSession daoSession = DaoSessionProvider.getDaoSession();
                HttpCacheDao httpCacheDao = daoSession.getHttpCacheDao();

                HttpCache newHttpCache = new HttpCache();
                newHttpCache.setCacheKey(cacheKey);
                newHttpCache.setCacheValue(cacheValue);
                newHttpCache.setExpires(expiresDate);

                HttpCache oldHttpCache = httpCacheDao.queryBuilder().where(HttpCacheDao.Properties.CacheKey.eq(cacheKey)).unique();
                if (oldHttpCache != null) {
                    daoSession.delete(oldHttpCache);
                }
                daoSession.insert(newHttpCache);
            }
        }

        return response;
    }

    private String getCacheCode(FormBody body) {
        int size = body.size();
        if (size <= 0) {
            return "";
        }

        for (int i = 0; i < size; i++) {
            if (body.name(i).equals(CacheRequest.Result.CACHE_CODE_KEY)) {
                return body.value(i);
            }
        }
        return "";
    }

    private String generateCacheKey(String url, FormBody formBody) {
        StringBuilder cacheKeyBuilder = new StringBuilder(url);
        String splitStr = "";

        int j = 0;
        for (int i = 0; i < formBody.size(); i++) {
            String paraName = formBody.name(i);
            if (paraName.equals(SHA1EncryptInterceptor.KEY_TIMESTAMP) ||
                    paraName.equals(SHA1EncryptInterceptor.KEY_SIGNATURE) ||
                    paraName.equals(CacheRequest.Result.CACHE_CODE_KEY)) {
                continue;
            }
            splitStr = j == 0 ? "?" : "&";
            cacheKeyBuilder.append(splitStr).append(formBody.name(i)).append("=").append(formBody.value(i));
            j++;
        }
        return cacheKeyBuilder.toString();
    }*/
}
