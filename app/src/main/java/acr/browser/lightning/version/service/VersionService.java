package acr.browser.lightning.version.service;


import acr.browser.lightning.version.vo.VersionCheckerVO;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HttpUrls;
import retrofit2.http.POST;

/**
 * Created by MartinKent on 2017/6/5.
 */

public interface VersionService {
    @FormUrlEncoded
    @POST(HttpUrls.VERSION_FIND_MAX_VERSION)
    Call<VersionCheckerVO> findMaxVersion(@Field("type") int type);
}
