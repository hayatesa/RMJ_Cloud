package rmj.cloud.common.util;

import rmj.cloud.common.constant.BusinessCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 溶酶菌
 */
public class ResultObject<T> implements Serializable {

    public final static Integer SUCCESS_CODE = BusinessCode.SUCCESS.getCode();
    public final static String DEFAULT_SUCCESS_MESSAGE = BusinessCode.SUCCESS.getMsg();
    public final static Integer FAILURE_CODE = BusinessCode.FAILURE.getCode();
    public final static String DEFAULT_FAILURE_MESSAGE = BusinessCode.FAILURE.getMsg();

    private Integer code;
    private String msg;
    private T data;
    private Date timeStamp;

    public ResultObject() {
        this.timeStamp = new Date();
    }

    public ResultObject(Integer code, String msg, T data) {
        this();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResultObject<T> success() {
        return success(DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static <T> ResultObject<T> success(String message) {
        return success(message, null);
    }

    public static <T> ResultObject<T> success(T data) {
        return success(DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static <T> ResultObject<T> success(String message, T data) {
        return new ResultObject(SUCCESS_CODE, message, data);
    };

    public static <T> ResultObject<T> failure() {
        return failure(DEFAULT_FAILURE_MESSAGE, null);
    }

    public static <T> ResultObject<T> failure(String message) {
        return failure(message, null);
    }

    public static <T> ResultObject<T> failure(T data) {
        return failure(DEFAULT_FAILURE_MESSAGE, null);
    }

    public static <T> ResultObject<T> failure(String message, T data) {
        return new ResultObject(FAILURE_CODE, message, data);
    };

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
