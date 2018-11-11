package rmj.cloud.common.util;

import java.util.UUID;

/**
 * 随机数工具类
 * @author 溶酶菌
 */
public interface RandomUtil {

    /**
     * 生成UUID
     * @return UUID
     */
    public  static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
