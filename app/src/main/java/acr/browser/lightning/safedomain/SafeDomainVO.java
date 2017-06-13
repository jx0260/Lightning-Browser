package acr.browser.lightning.safedomain;

import java.util.List;

import retrofit2.http.BaseResponseObj;

/**
 * 安全域名 列表
 * Created by Jin Liang on 2017/6/13.
 */

public class SafeDomainVO extends BaseResponseObj {

    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
