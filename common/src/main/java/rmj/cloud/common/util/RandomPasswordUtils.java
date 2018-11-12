package rmj.cloud.common.util;

import java.util.Random;

/**
 * 随机生成8位密码 工具类
 *
 */
public class RandomPasswordUtils {

    public static String[] SYMBOL_ARRAY = new String[] { "{", "}", "@", "#", "$", "^", "&", "*", "(", ")", "-", "=",
            "+", "[", "]" };

    public static String getPassword() {
        Random symbolRandom = new Random();
        Random random = new Random(); // 创建随机对象
        String password = new String(); // 保存随机数
        int asciiNum; // 取得随机数
        do {
            if (random.nextInt() % 2 == 1) {
                asciiNum = Math.abs(random.nextInt()) % 10 + 48; // 产生48到57的随机数(0-9的键位值)
                password += SYMBOL_ARRAY[symbolRandom.nextInt(SYMBOL_ARRAY.length)];
            } else {
                asciiNum = Math.abs(random.nextInt()) % 26 + 97; // 产生97到122的随机数(a-z的键位值)
            }
            char num1 = (char) asciiNum; // int转换char
            String dd = Character.toString(num1);
            password += dd;
        } while (password.length() < 8);// 设定长度小于8
        return password;

    }

    public static void main(String... args) {
        System.out.println(RandomPasswordUtils.getPassword());
    }

}
