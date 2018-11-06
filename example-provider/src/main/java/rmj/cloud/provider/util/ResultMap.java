package rmj.cloud.provider.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResultMap extends HashMap<String, Object> {


    public ResultMap() {
        put(Constant.CODE, Constant.SUCCESS);
        put(Constant.TIMESTAMP, new Date());
    }

    /**
     * 异常
     */
    public static ResultMap fail() {
        return fail(Constant.ERROR, Constant.DEFAULT_ERR_MSG);
    }

    /**
     * 异常
     * @param msg 文本消息
     * @return
     */
    public static ResultMap fail(String msg) {
        return fail(500, msg);
    }

    /**
     * 指定异常code值，和异常描述
     * @param code 业务异常，枚举
     * @param msg 描述
     */
    public static ResultMap fail(Integer code, String msg) {
        ResultMap r = new ResultMap();
        r.put( Constant.CODE, code);
        r.put( Constant.MSG, msg);
        return r;
    }

    /**
     * 正常
     * @param msg 文本消息
     */
    public static ResultMap success(String msg) {
        ResultMap r = new ResultMap();
        r.put( Constant.MSG, msg);
        return r;
    }

    /**
     * 正常
     * @param map 需要返回的数据
     */
    public static ResultMap success(Map<String, Object> map) {
        ResultMap r = new ResultMap();
        r.putAll(map);
        return r;
    }

    /**
     * 正常
     */
    public static ResultMap success() {
        return new ResultMap();
    }

    /**
     * 添加需要返回的数据
     * @param key 键
     * @param value 值
     */
    @Override
    public ResultMap put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 添加需要返回的数据, 默认键为data
     * @param value 值
     */
    public ResultMap putData(Object value) {
        return this.put(Constant.DATA, value);
    }
}
