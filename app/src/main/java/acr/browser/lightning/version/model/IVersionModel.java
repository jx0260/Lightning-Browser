package acr.browser.lightning.version.model;

import net.chinaedu.aedu.mvp.IAeduMvpModel;

import acr.browser.lightning.common.CommonCallback;
import acr.browser.lightning.version.vo.VersionCheckerVO;

/**
 * Created by MartinKent on 2017/6/5.
 */

public interface IVersionModel extends IAeduMvpModel {
    void findMaxVersion(int type, CommonCallback<VersionCheckerVO> callback);
}
