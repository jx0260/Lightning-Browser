package acr.browser.lightning.version.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MartinKent on 2017/6/5.
 */

public class VersionEntity {
    /**
     * type : 3
     * versionCode : 1
     * mobileVersion : 1.1.1
     * mobileVersionUrl : http://101stu.chinaedu.com/101XueQi_V1_0.apk
     * mustUpdate : 1
     * content : 测试数据，非类型对应下项目
     * title : 测试必须更新
     * size : 456230
     * id : ebb603ff-491e-4ea3-85ed-44ed9409deed
     * createTime : 2017-05-25 15:21:44
     * deleteFlag : 2
     * version : 1
     * checked : false
     */

    @SerializedName("type")
    private int type;
    @SerializedName("versionCode")
    private int versionCode;
    @SerializedName("mobileVersion")
    private String mobileVersion;
    @SerializedName("mobileVersionUrl")
    private String mobileVersionUrl;
    @SerializedName("mustUpdate")
    private int mustUpdate;
    @SerializedName("content")
    private String content;
    @SerializedName("title")
    private String title;
    @SerializedName("size")
    private long size;
    @SerializedName("id")
    private String id;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("deleteFlag")
    private int deleteFlag;
    @SerializedName("version")
    private int version;
    @SerializedName("checked")
    private boolean checked;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getMobileVersion() {
        return mobileVersion;
    }

    public void setMobileVersion(String mobileVersion) {
        this.mobileVersion = mobileVersion;
    }

    public String getMobileVersionUrl() {
        return mobileVersionUrl;
    }

    public void setMobileVersionUrl(String mobileVersionUrl) {
        this.mobileVersionUrl = mobileVersionUrl;
    }

    public int getMustUpdate() {
        return mustUpdate;
    }

    public void setMustUpdate(int mustUpdate) {
        this.mustUpdate = mustUpdate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
