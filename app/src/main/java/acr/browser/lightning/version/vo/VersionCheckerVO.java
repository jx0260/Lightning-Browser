package acr.browser.lightning.version.vo;

import com.google.gson.annotations.SerializedName;

import acr.browser.lightning.version.entity.VersionEntity;
import retrofit2.http.BaseResponseObj;


/**
 * Created by MartinKent on 2017/6/5.
 */

public class VersionCheckerVO extends BaseResponseObj {


    /**
     * object : {"type":3,"versionCode":1,"mobileVersion":"1.1.1","mobileVersionUrl":"http://101stu.chinaedu.com/101XueQi_V1_0.apk","mustUpdate":1,"content":"测试数据，非类型对应下项目","title":"测试必须更新","id":"ebb603ff-491e-4ea3-85ed-44ed9409deed","createTime":"2017-05-25 15:21:44","deleteFlag":2,"version":1,"checked":false}
     */

    @SerializedName("object")
    private VersionEntity object;

    public VersionEntity getObject() {
        return object;
    }

    public void setObject(VersionEntity object) {
        this.object = object;
    }
}
