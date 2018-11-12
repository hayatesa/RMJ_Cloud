package rmj.cloud.common.util;

/**
 * @author 作者 E-mail:ben.chen@accentrix.com
 * @version 创建时间：2018/7/23
 * <p>
 * 原理：LD算法计算两个字符串相似度，依据的是一个字符串变为另一个字符串时，最少需要编辑操作的次数，许可的编辑操作包括将一个字符替换成另一个字符，插入一个字符，删除一个字符，次数越少越相似。
 * 计算LD(str1, str2)的过程：
 * 1. 计算str1的长度len1, str2的长度len2, 若其中有一个长度为0, 则返回非0长度的值
 * 2. 初始化一个二维数组d[ len1+1 ][ len2+1 ]用来计算两个字符串之间的距离，并初始化第一行，第一列的数据从0开始按照行列数自然增长
 * 3. 循环比较str1中每个字符与str2中每个字符的关系，若str1[i]等于str2[j], 记eq=1, 否侧记eq=0, 计算d[i][j] = min(d[i][j-1]+1, d[i-1][j]+1, d[i-1][j-1]+eq)
 * 4. 返回最后的结果d[len1][len2]，其值最大为max(len1, len2), 时间复杂度为O(len1*len2)
 */

public class CalSimilarityUtils {
    //默认相似百分比
    static final double DEFAULT_SIMILAR_PERCENT = 0.5;

    //测试
    public static void main(String args[]) {
        String str1 = "小2wa";
        String str2 = "小wa";
        System.out.println(str1 + " is '" + isSimilar(str1, str2) + "' similar to " + str2);
    }

    //计算编辑距离
    public static int calDistance(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();
        if (len1 == 0) {
            return len2;
        }
        if (len2 == 0) {
            return len1;
        }

        int d[][] = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) {//初始化第一列
            d[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {//初始化第一行
            d[0][j] = j;
        }
        int eq = 0;
        char char1, char2;
        for (int i = 1; i <= len1; i++) {
            char1 = str1.charAt(i - 1);
            for (int j = 1; j <= len2; j++) {
                char2 = str2.charAt(j - 1);
                if (char1 == char2 || char1 + 32 == char2 || char1 - 32 == char2) {
                    eq = 0;
                } else {
                    eq = 1;
                }
                d[i][j] = Math.min(d[i - 1][j - 1] + eq, Math.min(d[i][j - 1] + 1, d[i - 1][j] + 1));
            }
        }
        return d[len1][len2];
    }

    //计算相似度
    public static double calSimilar(String str1, String str2) {
        if (str1 == null || str2 == null || (str1.length() == 0 && str2.length() == 0)) {
            return 0;
        }
        return 1 - ((double) calDistance(str1, str2) / Math.max(str1.length(), str2.length()));
    }

    public static boolean isSimilar(String str1, String str2, double similarPercent) {
        return calSimilar(str1, str2) >= similarPercent;
    }

    public static boolean isSimilar(String str1, String str2) {
        return isSimilar(str1, str2, DEFAULT_SIMILAR_PERCENT);
    }
}
