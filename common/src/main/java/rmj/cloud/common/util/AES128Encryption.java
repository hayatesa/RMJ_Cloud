package rmj.cloud.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * AES 对称加密解密工具类
 */
public class AES128Encryption {

    private static AES128Encryption instance = null;

    private AES128Encryption() {
    }

    public static AES128Encryption getInstance() {
        if (instance == null)
            instance = new AES128Encryption();
        return instance;
    }

    public static String encrypt(String encData, String secretKey, String vector) throws Exception {

        if (secretKey == null) {
            return null;
        }
        if (secretKey.length() != 16) {
            return null;
        }
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = secretKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
        return Base64.getEncoder().encodeToString(encrypted);// 此处使用BASE64做转码。
    }

    public String decrypt(String sSrc, String key, String ivs) throws Exception {
        try {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivs.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.getDecoder().decode(sSrc);// 先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            // 需要加密的字串
            String cSrc = "CAIS+QF1q6Ft5B2yfSjIr4j9Pdjyv5lUhaWdVVPy3HMAbfZOhrbxlzz2IH9PfXFrB+wdvvU1n2FV5/sTlqVoRoReREvCKM1565kPKLpkgzSF6aKP9rUhpMCPOwr6UmzWvqL7Z+H+U6muGJOEYEzFkSle2KbzcS7YMXWuLZyOj+wMDL1VJH7aCwBLH9BLPABvhdYHPH/KT5aXPwXtn3DbATgD2GM+qxsmuPzkkpLHsEGD1AGkmr9NnemrfMj4NfsLFYxkTtK40NZxcqf8yyNK43BIjvwp1/Ucommb5YHMWQIAvkTbYvCy6cF0JRRie603F7RDqPXsHTbtbARJT+8agAEkfwl8rg6r/Cbh/gdhe2rUhcJzBdBtmifEvFIt0o6SIH+6BnWdj7Eam9QsUQRAdAu+HTSmGCKGMMI8xZMmXLVZBkblvgOYo4o7pbAwJ5npoyCjOOgJq53/vQQd1D30FbCvyYiBzMGrSNXifsAWYSQ40XwR7hTZjsxX/INEyi3Wvg==";
            /*
            * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
            */
            String sKey = "smkldospdosldaaa";//key，可自行修改
            String ivParameter = "0392039203920300";//偏移量,可自行修改

            // 加密
            long lStart = System.currentTimeMillis();
            String enString = AES128Encryption.getInstance().encrypt(cSrc, sKey, ivParameter);
            System.out.println("加密后的字串是：" + enString);

            long lUseTime = System.currentTimeMillis() - lStart;
            System.out.println("加密耗时：" + lUseTime + "毫秒");
            // 解密
            lStart = System.currentTimeMillis();
            String DeString = AES128Encryption.getInstance().decrypt(enString, sKey, ivParameter);
            System.out.println("解密后的字串是：" + DeString);
            lUseTime = System.currentTimeMillis() - lStart;
            System.out.println("解密耗时：" + lUseTime + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
