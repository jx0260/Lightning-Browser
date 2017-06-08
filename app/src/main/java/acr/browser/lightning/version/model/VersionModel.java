package acr.browser.lightning.version.model;

import acr.browser.lightning.common.CommonCallback;
import acr.browser.lightning.version.service.VersionService;
import acr.browser.lightning.version.vo.VersionCheckerVO;
import retrofit2.ApiServiceManager;
import retrofit2.Call;

/**
 * Created by MartinKent on 2017/6/5.
 */

public class VersionModel implements IVersionModel {
    private VersionService mVersionService = ApiServiceManager.getService(VersionService.class);

    @Override
    public void findMaxVersion(int type, CommonCallback<VersionCheckerVO> callback) {
        Call<VersionCheckerVO> call = mVersionService.findMaxVersion(type);
        call.enqueue(callback);
    }
}
