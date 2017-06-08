package net.chinaedu.screenrecorder;

/**
 * Created by Administrator on 2017/5/31.
 */

public class AppContext {

    private static AppContext instance;

    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mFrame;

    private AppContext(){

    }

    public static AppContext getInstance(){
        if(instance == null){
            instance = new AppContext();
        }
        return instance;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getBitRate() {
        return mBitRate;
    }

    public void setBitRate(int mBitRate) {
        this.mBitRate = mBitRate;
    }

    public int getFrame() {
        return mFrame;
    }

    public void setFrame(int mFrame) {
        this.mFrame = mFrame;
    }
}
