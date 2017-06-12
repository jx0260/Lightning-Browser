package acr.browser.lightning.udp;

/**
 * 锁屏、解锁
 * Created by Jin Liang on 2017/6/10.
 */

public class LockMsg {

    private boolean lock;
    private String token;

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
