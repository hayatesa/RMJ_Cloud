package rmj.cloud.provider.util;

/**
 * 全局常量
 */
public interface Constant {

    static final Integer SUCCESS = 0; // 成功

    static final Integer NO_LOGIN = 401; // 未授权

    static final Integer NOT_UNAUTHORIZED = 403; // 无权限

    static final Integer ERROR = 500; // 服务器错误

    static final String DEFAULT_ERR_MSG = "操作失败"; // 默认错误提示

    static final String CODE = "code";

    static final String MSG = "msg";

    static final String TIMESTAMP = "timestamp";

    static final String PATH = "path";

    static final String DATA = "data";
}
