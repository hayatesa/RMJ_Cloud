/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package rmj.cloud.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.io.*;

/**
 * Jedis Cache 工具类
 *
 * @author ThinkGem
 * @version 2014-6-29
 */
public class JedisUtils {

    public static final String CHARSET_NAME = "UTF-8";
    private static Logger logger = LoggerFactory.getLogger(JedisUtils.class);

    private static JedisPool jedisPool = SpringContextHolder.getBean(JedisPool.class);

    public static final String KEY_PREFIX = "lifetouch_";

    /**
     * 向队列插入一个字符串元素
     * @param key
     * @param value
     * @return
     */
    public static boolean pushElement(String key, String value) {
        return pushElements(key, value);
    }

    /**
     * 向队列依次插入多个String类型元素
     * @param key
     * @param values
     * @return
     */
    public static boolean pushElements(String key, String... values) {
        Jedis jedis = null;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            for (String value : values)
                jedis.rpush(key, value);
            return true;
        } catch (Exception e) {
            logger.warn("pushElements {} = {}. {}", key, values, e.getMessage());
            return false;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 从队列拿出一个String类型元素
     * @param key
     * @return
     */
    public static String popElement(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            value = jedis.lpop(key);
        } catch (Exception e) {
            logger.warn("popElement {} = {}. {}", key, value, e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 向队列依次插入一个Object 类型元素
     * @param key
     * @param value
     * @return
     */
    public static boolean pushObjectElement(String key, Object value) {
        return pushObjectElements(key, value);
    }

    /**
     * 向队列依次插入多个Object 类型元素
     * @param key
     * @param values
     * @return
     */
    public static boolean pushObjectElements(String key, Object... values) {
        Jedis jedis = null;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            for (Object value : values) {
                jedis.rpush(getBytesKey(key), toBytes(value));
            }
            return true;
        } catch (Exception e) {
            logger.warn("pushObjectElements {} = {}. {}", key, values, e.getMessage());
            return false;
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 从队列拿出一个Object 类型元素
     * @param key
     * @return
     */
    public static Object popObjectElement(String key) {
        Object value = null;
        Jedis jedis = null;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            value = toObject(jedis.lpop(getBytesKey(key)));
        } catch (Exception e) {
            logger.warn("popObjectElement {} = {}. {}", key, value, e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 向Hash set key-value 元素
     * @param key redis 的key
     * @param field hash 集合的key值
     * @param value hash 集合的value值
     * @return
     */
    public static Long hSetStringElement(String key, String field, String value) {
        Jedis jedis = null;
        Long result = 0L;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            result = jedis.hset(getBytesKey(key), field.getBytes(CHARSET_NAME), value.getBytes(CHARSET_NAME));
        } catch (Exception e) {
            logger.warn("hSetStringElement key:{} , field:{}, value:{}. {}", key, field, value, e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Hash get 对应的value 元素(String 类型值)
     * @param key redis 的key
     * @param field hash 集合的key值
     * @return
     */
    public static String hGetStringElement(String key, String field) {
        Jedis jedis = null;
        String result = null;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            result = new String(jedis.hget(getBytesKey(key), field.getBytes(CHARSET_NAME)), CHARSET_NAME);
        } catch (Exception e) {
            logger.warn("hGetStringElement key:{} , field:{}. {}", key, field, e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Hash get 对应的value 元素(Object 类型值)
     * @param key redis 的key
     * @param field hash 集合的key值
     * @return
     */
    public static Object hGetObjectElement(String key, String field) {
        Jedis jedis = null;
        Object result = null;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            result = jedis.hget(getBytesKey(key), field.getBytes(CHARSET_NAME));
        } catch (Exception e) {
            logger.warn("hGetObjectElement key:{} , field:{}. {}", key, field, e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Hash set key-value 元素
     * @param key redis 的key
     * @param field hash 集合的key值
     * @param value hash 集合的value值
     * @return
     */
    public static Long hSetObjectElement(String key, String field, Object value) {
        Jedis jedis = null;
        Long result = 0L;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            result = jedis.hset(getBytesKey(key), field.getBytes(CHARSET_NAME), toBytes(value));
        } catch (Exception e) {
            logger.warn("hSetObjectElement key:{} , field:{}, value:{}. {}", key, field, value, e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public static boolean existKey(String key) {
        Jedis jedis = null;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            return jedis.exists(key) || jedis.exists(getBytesKey(key));
        } catch (Exception e) {
            logger.warn("removeKey {}. {}", key, e.getMessage());
            return false;
        } finally {
            returnResource(jedis);
        }
    }

    public static void removeKey(String key) {
        Jedis jedis = null;
        try {
            key = getPrefixKey(key);
            jedis = getResource();
            if (jedis.exists(getBytesKey(key)))
                jedis.del(getBytesKey(key));
            if (jedis.exists(key))
                jedis.del(key);
        } catch (Exception e) {
            logger.warn("removeKey {}. {}", key, e.getMessage());
        } finally {
            returnResource(jedis);
        }
    }

    public static String getPrefixKey(String key) {
        return KEY_PREFIX + key;
    }

    /**
     * 获取资源
     *
     * @return
     * @throws JedisException
     */
    public static Jedis getResource() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // logger.debug("getResource.", jedis);
        } catch (JedisException e) {
            logger.warn("getResource. {}", e.getMessage());
            returnBrokenResource(jedis);
            throw e;
        }
        return jedis;
    }

    /**
     * 归还资源
     *
     * @param jedis
     */
    public static void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 释放资源
     *
     * @param jedis
     */
    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 获取byte[]类型Key
     *
     * @param object
     * @return
     */
    public static byte[] getBytesKey(Object object) {
        if (object instanceof String) {
            try {
                return ((String) object).getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return serialize(object);
        }
    }

    /**
     * Object转换byte[]类型
     *
     * @param object
     * @return
     */
    public static byte[] toBytes(Object object) {
        return serialize(object);
    }

    /**
     * byte[]型转换Object
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        return unserialize(bytes);
    }

    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            if (object != null) {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                return baos.toByteArray();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            if (bytes != null && bytes.length > 0) {
                bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
