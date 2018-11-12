package rmj.cloud.common.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CommonQueueUtil {

    private static final Logger LOG = LoggerFactory.getLogger(CommonQueueUtil.class);

    public enum CmQueueKey {
        BillNo(10000), BillPaymentSn(10000), BillAutoPayNo(100), BillItemAutoGenNo(100), BulkIoBatchNo(
                100), BillDepositNo(100);

        private int capacity;

        CmQueueKey(int capacity) {
            this.capacity = capacity;
        }
    }

    public enum QueueKey {
        BizRegisterNo(100000), PaymentTransactionSn(100), BizPointOrderNo(100), StoreRankOrderNo(
                100), StorePreregisterNo(100000), StoreCategoryAppNo(100);

        private int capacity;

        QueueKey(int capacity) {
            this.capacity = capacity;
        }
    }

    private static final int TRY_TIMES = 3;
    private static final String UNDERLINE = "_";
    private static SimpleDateFormat ymdDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat ymDateFormat = new SimpleDateFormat("yyyyMM");

    /**
     * 商户广告充值编号
     */
    public static String generateBizPointOrderNo() {
        String queueValue = getQueueValue(QueueKey.BizPointOrderNo.name());
        return System.currentTimeMillis() + queueValue;
    }

    /**
     * 店铺 店铺等级订单编号
     */
    public static String generateStoreRankOrderNo() {
        String queueValue = getQueueValue(QueueKey.StoreRankOrderNo.name());
        return System.currentTimeMillis() + queueValue;
    }

    /**
     * 商户申请编号
     */
    public static String generateBizRegisterNo() {
        String queueValue = getQueueValue(QueueKey.BizRegisterNo.name());
        return "BR" + ymdDateFormat.format(new Date()) + queueValue;
    }

    /**
     * 边逛边赚申请
     * @return
     */
    public static String generateStorePreregisterNo() {
        String queueValue = getQueueValue(QueueKey.StorePreregisterNo.name());
        return "SP" + ymdDateFormat.format(new Date()) + queueValue;
    }

    /**
     * 店铺 店铺行业分类申请
     */
    public static String generateStoreCategoryAppNo() {
        String queueValue = getQueueValue(QueueKey.StoreCategoryAppNo.name());
        return System.currentTimeMillis() + queueValue;
    }

    /**
     * 生成bill no
     * @param cmInfoNo  小区编号
     * @return
     */
    public static String generateBillNo(String cmInfoNo) {
        if (StringUtils.isEmpty(cmInfoNo))
            return null;
        String queueValue = getQueueValue(getCmQueueKey(CmQueueKey.BillNo, cmInfoNo));
        return ymdDateFormat.format(new Date()) + cmInfoNo + queueValue;
    }

    /**
     * 生成bill payment sn
     * @param cmInfoNo  小区编号
     * @return
     */
    public static String generateBillPaymentSn(String cmInfoNo) {
        if (StringUtils.isEmpty(cmInfoNo))
            return null;
        String queueValue = getQueueValue(getCmQueueKey(CmQueueKey.BillPaymentSn, cmInfoNo));
        return ymdDateFormat.format(new Date()) + cmInfoNo + queueValue;
    }

    /**
     * 生成 bill auto pay no
     * @param cmInfoNo  小区编号
     * @param typeCode  送/回盘类型  送盘(SP) / 回盘(HP)
     * @return
     */
    public static String generateBillAutoPayNo(String cmInfoNo, String typeCode) {
        if (StringUtils.isEmpty(cmInfoNo))
            return null;
        String queueValue = getQueueValue(getCmQueueKey(CmQueueKey.BillAutoPayNo, cmInfoNo));
        if (StringUtils.isEmpty(queueValue))
            return null;
        return ymDateFormat.format(new Date()) + cmInfoNo + queueValue + typeCode;
    }

    /**
     * 生成bill items auto gen no
     * @param cmInfoNo  小区编号
     * @param pricingCode   计费项目code值
     * @return
     */
    public static String generateBillItemAutoGenNo(String cmInfoNo, String pricingCode) {
        if (StringUtils.isEmpty(cmInfoNo))
            return null;
        String queueValue = getQueueValue(getCmQueueKey(CmQueueKey.BillItemAutoGenNo, cmInfoNo));
        if (StringUtils.isEmpty(queueValue))
            return null;
        return ymDateFormat.format(new Date()) + cmInfoNo + pricingCode + queueValue;
    }

    /**
     * 生成 bulk io batch no
     * @param cmInfoNo  小区编号
     * @param batchTypeCode 导入类型
     * @return
     */
    public static String generateBulkIoBatchNo(String cmInfoNo, String batchTypeCode) {
        if (StringUtils.isEmpty(cmInfoNo))
            return null;
        String queueValue = getQueueValue(getCmQueueKey(CmQueueKey.BulkIoBatchNo, cmInfoNo));
        if (StringUtils.isEmpty(queueValue))
            return null;
        return ymdDateFormat.format(new Date()) + cmInfoNo + batchTypeCode + queueValue;
    }

    /**
     * 生成bill deposit no
     * @param cmInfoNo  小区编号
     * @param pricingCode   计费项目code值
     * @param dps
     * @return
     */
    public static String generateBillDepositNo(String cmInfoNo, String pricingCode, String dps) {
        if (StringUtils.isEmpty(cmInfoNo))
            return null;
        String queueValue = getQueueValue(getCmQueueKey(CmQueueKey.BillDepositNo, cmInfoNo));
        if (StringUtils.isEmpty(queueValue))
            return null;
        return ymDateFormat.format(new Date()) + cmInfoNo + StringUtils.trimToEmpty(pricingCode)
                + StringUtils.trimToEmpty(dps) + queueValue;
    }

    public static String generatePaymentTransactionSn(String orderChannelCode, String paymentChannelCode,
            String businessTypeCode) {
        if (Objects.isNull(orderChannelCode) || Objects.isNull(paymentChannelCode) || Objects.isNull(businessTypeCode))
            return null;
        String sn = getQueueValue(QueueKey.PaymentTransactionSn.name());
        if (StringUtils.isEmpty(sn))
            return null;
        StringBuffer result = new StringBuffer();
        result.append(orderChannelCode).append(paymentChannelCode).append(businessTypeCode);
        LocalDateTime now = LocalDateTime.now();
        String year = new Integer(now.getYear()).toString();
        result.append(year.substring(year.length() - 2)).append(String.format("%02d", now.getMonthValue()));
        String timeStamp = new Long(System.currentTimeMillis() / 1000 % (10000000000L)).toString();
        result.append(timeStamp);
        result.append(sn);
        return result.toString();
    }

    private static String getQueueValue(String redisQueueKey) {
        if (!JedisUtils.existKey(redisQueueKey)) {
            LOG.error("The queue in redis is not exist! key = {}", redisQueueKey);
            return null;
        }
        Object obj = JedisUtils.popElement(redisQueueKey);
        // try 3 times
        for (int i = 0; Objects.isNull(obj) && i < TRY_TIMES; i++) {
            obj = JedisUtils.popElement(redisQueueKey);
        }
        //can not get resource from redis
        if (Objects.isNull(obj))
            return null;
        String value = (String) obj;
        LOG.info("Obtain {} from redis queue, and value is {}", redisQueueKey, value);
        JedisUtils.pushElement(redisQueueKey, value);
        return value;
    }

    private static String getCmQueueKey(CmQueueKey cmQueueKey, String cmInfoNo) {
        StringBuffer redisQueueKey = new StringBuffer(cmQueueKey.name());
        if (StringUtils.isNotEmpty(cmInfoNo))
            redisQueueKey.append(UNDERLINE + StringUtils.trimToEmpty(cmInfoNo));
        return redisQueueKey.toString();
    }

    private static void initializeQueue(String redisQueueKey, int capacity) {
        if (JedisUtils.existKey(redisQueueKey)) {
            LOG.debug("Queue, key existed {}", redisQueueKey);
            return;
        }

        LOG.debug("Initialize queue, key {}", redisQueueKey);
        List<String> list = Lists.newLinkedList();
        for (int i = 1; i < capacity; i++) {
            list.add(String.format("%0" + (String.valueOf(capacity).length() - 1) + "d", i));
        }
        String[] arrays = list.toArray(new String[list.size()]);
        JedisUtils.pushElements(redisQueueKey, arrays);
    }

    public static void initializeCmQueue(List<String> cmInfoNoList) {
        Jedis jedis = null;
        try {
            JedisUtils.getResource();
        } catch (JedisException e) {
            LOG.warn("getResource. {}", e.getMessage());
            JedisUtils.returnBrokenResource(jedis);
            return;
        }
        LOG.debug("Initializing redis queue...");
        for (String cmInfoNo : cmInfoNoList)
            for (CmQueueKey key : CmQueueKey.values())
                initializeQueue(getCmQueueKey(key, cmInfoNo), key.capacity);
    }

    public static void initializeQueue() {
        Jedis jedis = null;
        try {
            JedisUtils.getResource();
        } catch (JedisException e) {
            LOG.warn("getResource. {}", e.getMessage());
            JedisUtils.returnBrokenResource(jedis);
            return;
        }
        for (QueueKey key : QueueKey.values()) {
            initializeQueue(key.name(), key.capacity);
        }
    }

    public static void initializeCmQueue(String cmInfoNo) {
        Jedis jedis = null;
        try {
            JedisUtils.getResource();
        } catch (JedisException e) {
            LOG.warn("getResource. {}", e.getMessage());
            JedisUtils.returnBrokenResource(jedis);
            return;
        }
        if (StringUtils.isEmpty(cmInfoNo))
            return;
        for (CmQueueKey key : CmQueueKey.values())
            initializeQueue(getCmQueueKey(key, cmInfoNo), key.capacity);
    }

    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        Calendar calendar = Calendar.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        System.out.println("当前时间戳：" + currentTimeMillis + "，位数：" + new Long(currentTimeMillis).toString().length());
        calendar.setTimeInMillis(currentTimeMillis);
        System.out.println("当前时间：" + format.format(calendar.getTime()));

        String str = new Long(currentTimeMillis).toString();
        str = 1 + str;
        System.out.println("重复时间戳：" + str + "， 位数：" + str.length());
        calendar.setTimeInMillis(Long.parseLong(str));
        System.out.println("重复时间：" + format.format(calendar.getTime()));
    }
}
