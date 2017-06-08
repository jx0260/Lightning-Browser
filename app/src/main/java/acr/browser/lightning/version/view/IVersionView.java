package acr.browser.lightning.version.view;

import net.chinaedu.aedu.mvp.IAeduMvpView;

import acr.browser.lightning.version.vo.VersionCheckerVO;

/**
 * Created by MartinKent on 2017/6/5.
 */

public interface IVersionView extends IAeduMvpView {
    void onFindMaxVersionSuccess(VersionCheckerVO vo);

    void onFindMaxVersionFailed(Throwable e);
}
