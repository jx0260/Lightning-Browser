package retrofit2.http;

/**
 * http接口返回数据封装类 基类
 * 作者：Jin Liang
 * 时间：2017/5/11
 */
public class BaseResponseObj {

    private int status;
    private String message;
    private Integer cacheTimeLength;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCacheTimeLength() {
        return cacheTimeLength;
    }

    public void setCacheTimeLength(Integer cacheTimeLength) {
        this.cacheTimeLength = cacheTimeLength;
    }
}