package acr.browser.lightning.safedomain;

import javax.inject.Inject;

import acr.browser.lightning.common.CommonCallback;
import retrofit2.ApiServiceManager;

/**
 * Created by Jin Liang on 2017/6/13.
 */

public class SafeDomainModel implements ISafeDomainModel {

    @Inject
    public SafeDomainModel(){}

    private SafeDomainListService mUrlWhiteListService = ApiServiceManager.getService(SafeDomainListService.class);

    @Override
    public void findSafeDomainList(CommonCallback<SafeDomainVO> callback) {
        mUrlWhiteListService.findSafeDomainList().enqueue(callback);
    }
}
