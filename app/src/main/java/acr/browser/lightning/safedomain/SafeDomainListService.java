package acr.browser.lightning.safedomain;

import retrofit2.Call;
import retrofit2.http.HttpUrls;
import retrofit2.http.POST;

/**
 * Created by Jin Liang on 2017/6/13.
 */

public interface SafeDomainListService {
    @POST(HttpUrls.SAFE_DOMAIN_LIST)
    Call<SafeDomainVO> findSafeDomainList();
}
