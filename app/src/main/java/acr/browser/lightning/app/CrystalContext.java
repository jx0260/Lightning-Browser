package acr.browser.lightning.app;

/**
 * Created by Jin Liang on 2017/6/7.
 */

public class CrystalContext {

    private static CrystalContext mInstance;
    private String keySessionId;

    public static CrystalContext getInstance() {
        if (null == mInstance) {
            synchronized (CrystalContext.class) {
                if (null == mInstance) {
                    mInstance = new CrystalContext();
                }
            }
        }
        return mInstance;
    }

    public void setKeySessionId(String keySessionId) {
        this.keySessionId = keySessionId;
    }

    public String getKeySessionId() {
        return keySessionId;
    }

}
