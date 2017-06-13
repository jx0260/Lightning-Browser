package acr.browser.lightning.safedomain;

import net.chinaedu.aedu.mvp.IAeduMvpModel;

import acr.browser.lightning.common.CommonCallback;

/**
 * Created by Jin Liang on 2017/6/13.
 */

public interface ISafeDomainModel extends IAeduMvpModel{

    void findSafeDomainList(CommonCallback<SafeDomainVO> callback);

}
