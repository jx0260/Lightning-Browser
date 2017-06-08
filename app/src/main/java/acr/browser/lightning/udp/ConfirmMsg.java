package acr.browser.lightning.udp;

/**
 * 教师端收到了学生信息的确认消息
 * Created by Jin Liang on 2017/6/3.
 */

public class ConfirmMsg {

    private boolean confirmReceived;
    private String sendTaskId;

    public boolean isConfirmReceived() {
        return confirmReceived;
    }

    public void setConfirmReceived(boolean confirmReceived) {
        this.confirmReceived = confirmReceived;
    }

    public String getSendTaskId() {
        return sendTaskId;
    }

    public void setSendTaskId(String sendTaskId) {
        this.sendTaskId = sendTaskId;
    }
}
