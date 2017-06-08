package acr.browser.lightning.udp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 学生上线 多播的信息
 * Created by Jin Liang on 2017/5/25.
 */

public class StudentOnlineMsg implements Parcelable {

    private String sendTaskId; // 发送任务id
    private String rName;
    private String uName; //用户名
    private Integer state; //1:学生上线 0：学生下线
    private String img;   //学生头像地址
    private String ip;    //学生端IP地址
    private Integer width; //平板宽度
    private Integer height;//平板高度

    public String getSendTaskId() {
        return sendTaskId;
    }

    public void setSendTaskId(String sendTaskId) {
        this.sendTaskId = sendTaskId;
    }

    public String getrName() {
        return rName;
    }

    public void setrName(String rName) {
        this.rName = rName;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rName);
        dest.writeString(this.uName);
        dest.writeValue(this.state);
        dest.writeString(this.img);
        dest.writeString(this.ip);
        dest.writeValue(this.width);
        dest.writeValue(this.height);
    }

    public StudentOnlineMsg() {
    }

    protected StudentOnlineMsg(Parcel in) {
        this.rName = in.readString();
        this.uName = in.readString();
        this.state = (Integer) in.readValue(Integer.class.getClassLoader());
        this.img = in.readString();
        this.ip = in.readString();
        this.width = (Integer) in.readValue(Integer.class.getClassLoader());
        this.height = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<StudentOnlineMsg> CREATOR = new Creator<StudentOnlineMsg>() {
        @Override
        public StudentOnlineMsg createFromParcel(Parcel source) {
            return new StudentOnlineMsg(source);
        }

        @Override
        public StudentOnlineMsg[] newArray(int size) {
            return new StudentOnlineMsg[size];
        }
    };
}
