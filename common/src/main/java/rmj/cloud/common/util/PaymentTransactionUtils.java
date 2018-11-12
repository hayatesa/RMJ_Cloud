package rmj.cloud.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 生成sn码工具
 *
 * @author Jaye.Lin
 * @date 创建时间：2017年11月20日 下午2:30:40
 * @version 1.0
 */

public class PaymentTransactionUtils {

    /**
     * 生成sn码
     * @param chcd 渠道
     * @param type 类型
     * @param method 方式
     * @param source 钱包
     * @param destination
     * @return
     */
    public static String generateSn(String chcd, String type, String method, String source, String destination) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String timeStr = sdf.format(new Date());
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        String sn = chcd + type + method + source + destination + timeStr + str.toString();

        return sn;
    }
}
