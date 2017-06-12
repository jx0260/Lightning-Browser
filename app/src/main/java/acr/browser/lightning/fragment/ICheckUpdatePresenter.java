package acr.browser.lightning.fragment;

import acr.browser.lightning.version.entity.VersionEntity;

/**
 * Created by Jin Liang on 2017/6/10.
 */

public interface ICheckUpdatePresenter {

    void startDownload(VersionEntity entity);

    void stopDownload();

}
