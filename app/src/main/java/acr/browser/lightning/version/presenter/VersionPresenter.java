package acr.browser.lightning.version.presenter;

import android.content.Context;

import net.chinaedu.aedu.mvp.AeduBasePresenter;
import net.chinaedu.aedu.utils.LogUtils;

import acr.browser.lightning.common.CommonCallback;
import acr.browser.lightning.version.model.IVersionModel;
import acr.browser.lightning.version.model.VersionModel;
import acr.browser.lightning.version.view.IVersionView;
import acr.browser.lightning.version.vo.VersionCheckerVO;
import retrofit2.Response;

/**
 * Created by MartinKent on 2017/6/5.
 */

public class VersionPresenter extends AeduBasePresenter<IVersionView, IVersionModel> implements IVersionPresenter {
    private final IVersionView view;

    public VersionPresenter(Context context, IVersionView view) {
        super(context, view);
        this.view = view;
        LogUtils.d("IVersionView=" + view);
    }

    @Override
    public IVersionView getView() {
        return view;
    }

    @Override
    public IVersionModel createModel() {
        return new VersionModel();
    }

    /**
     * @param type app类型1:学启教师版安卓客户端;2:学启教师版IOS客户端;3:学启学生版安卓客户端;4:学启学生版IOS客户端;5:学启标准版安卓客户端;6:学启标准版PAD客户端;7:远程控制客户端;
     */
    @Override
    public void findMaxVersion(int type) {
        getModel().findMaxVersion(type, new CommonCallback<VersionCheckerVO>() {
            @Override
            public void onResponse(Response<VersionCheckerVO> response) {
                LogUtils.d("getView=" + getView() + ",response.body=" + response.body());
                getView().onFindMaxVersionSuccess(response.body());
            }

            @Override
            public boolean onFailure(Throwable e) {
                getView().onFindMaxVersionFailed(e);
                return true;
            }
        });
    }
}
