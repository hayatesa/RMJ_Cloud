package rmj.cloud.common.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * @author Aaron.lin@accentrix.com
 * @date 2018/8/21
 */
public class JWTUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JWTUtil.class);

    public static final String IS_SMS_AUTO_LOGIN = "isSmsAutoLogin";
    public static final String LOGIN_MODE = "loginMode";
    public static final String USER_ID = "userId";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String HEADER_PREFIX = "Bearer ";
    public static final String EMPLOYEE_COMMUNITY_ID = "employeeCmInfoId";
    public static final String PLATFORM = "platform";

    /** redis key */
    private static final String TokenCacheKey = "tokenCacheKey";

    public enum Platform {
        PC, IOS, Android
    }

    private JWTUtil() {
    }

    public static String generateApiToken(String jwtKey, String subject, Map<String, Object> valueMap,
            int expirationHours) {
        DefaultClaims claims = new DefaultClaims();

        claims.setSubject(subject);
        claims.putAll(valueMap);
        LOG.info("Token generated with subject: {}", subject);
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        String token = HEADER_PREFIX + Jwts.builder().setClaims(claims).setIssuer("UserAPI").setIssuedAt(now)
                .setExpiration(getExpiration(expirationHours)).signWith(SignatureAlgorithm.HS512, jwtKey).compact();
        if (valueMap.get(PLATFORM) != null && Platform.PC.name().equals(valueMap.get(PLATFORM).toString())) {
            // PC 端生成的token，将该token的issue 时间放入缓存
            setCacheStatus(subject, now);
        }
        return token;
    }

    private static void setCacheStatus(String subject, Date now) {
        //生成token记录的时间精确到秒
        LOG.info("Cache token issue time: {} - {}", subject, now.getTime() / 1000);
        JedisUtils.hSetStringElement(TokenCacheKey, getCacheKey(subject), Long.toString(now.getTime() / 1000));
    }

    public static String getCacheStatus(String subject) {
        if (StringUtils.isBlank(subject))
            return StringUtils.EMPTY;
        String issueTime = JedisUtils.hGetStringElement(TokenCacheKey, getCacheKey(subject));
        LOG.info("Get cache token issue time by subject:{} , value:{}", subject, issueTime);
        return issueTime;
    }

    private static Date getExpiration(int expirationHours) {
        return new Date(System.currentTimeMillis() + (1000L * 60 * 60 * expirationHours));
    }

    private static String getCacheKey(String subject) {
        if (StringUtils.isBlank(subject))
            return StringUtils.EMPTY;
        return subject + "|" + Platform.PC.name();
    }
}
