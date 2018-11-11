package rmj.cloud.common.constant;

public enum BusinessCode {

    SUCCESS(0, "操作成功"),
    FAILURE(500, "操作失败"),
    NO_LOGIN(401, "未登录"),
    NOT_UNAUTHORIZED(403, "无权限");

    private int code;

    private String msg;

    BusinessCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
