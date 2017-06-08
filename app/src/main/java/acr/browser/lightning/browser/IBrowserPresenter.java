package acr.browser.lightning.browser;

import acr.browser.lightning.version.entity.VersionEntity;

/**
 * Created by Jin Liang on 2017/6/7.
 */

public interface IBrowserPresenter {

    void checkVersion();

    void startDownload(VersionEntity entity);

    void stopDownload();
}
