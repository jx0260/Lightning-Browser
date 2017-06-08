package acr.browser.lightning.udp;

/**
 * 上线状态
 * Created by Jin Liang on 2017/5/25.
 */

public enum OnlineEnum {

    ON_LINE(1), OFF_LINE(0);

    private int val;

    OnlineEnum(int val){
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
