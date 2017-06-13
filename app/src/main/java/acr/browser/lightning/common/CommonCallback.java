package acr.browser.lightning.common;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.BaseResponseObj;

/**
 * Created by MartinKent on 2017/5/26.
 */

public abstract class CommonCallback<T extends BaseResponseObj> implements Callback<T> {
    @Override
    public final void onResponse(Call<T> call, Response<T> response) {
        if (null == response || null == response.body()) {
            onFailure(call, new Throwable("获取数据失败"));
            onComplete();
            return;
        }
        if (0 != response.body().getStatus()) {
            onFailure(call, new Throwable(response.body().getMessage()));
            onComplete();
            return;
        }
        onResponse(response);
        onComplete();
    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
        onComplete();
        onFailure(t);
    }

    public abstract void onResponse(Response<T> response);

    protected void onFailure(Throwable e) {
    }

    public void onComplete() {

    }
}
