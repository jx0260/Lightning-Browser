package acr.browser.lightning.version.presenter;


import net.chinaedu.aedu.mvp.IAeduMvpPresenter;

import acr.browser.lightning.version.model.IVersionModel;
import acr.browser.lightning.version.view.IVersionView;

/**
 * Created by MartinKent on 2017/6/5.
 */

public interface IVersionPresenter extends IAeduMvpPresenter<IVersionView, IVersionModel> {
    /**
     * 获取最新版本
     *
     * @param type app类型1:学启教师版安卓客户端;2:学启教师版IOS客户端;3:学启学生版安卓客户端;4:学启学生版IOS客户端;5:学启标准版安卓客户端;6:学启标准版PAD客户端;7:远程控制客户端;
     */
    void findMaxVersion(int type);
}
