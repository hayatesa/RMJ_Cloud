/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package rmj.cloud.common.util;

import com.google.common.collect.Maps;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.Map;

public class UnitTreeComparator {

    public static Comparator<String> newComparator() {
        return new UnitComparator();
    }

    static class UnitComparator implements Comparator<String> {

        private static final Map<String, String> CN_NUMBER_MAP = Maps.newHashMap();

        static {
            CN_NUMBER_MAP.put("一", "1");
            CN_NUMBER_MAP.put("二", "2");
            CN_NUMBER_MAP.put("三", "3");
            CN_NUMBER_MAP.put("四", "4");
            CN_NUMBER_MAP.put("五", "5");
            CN_NUMBER_MAP.put("六", "6");
            CN_NUMBER_MAP.put("七", "7");
            CN_NUMBER_MAP.put("八", "8");
            CN_NUMBER_MAP.put("九", "9");
            CN_NUMBER_MAP.put("十", "10");
        }

        @Override
        public int compare(String s1, String s2) {

            if (StringUtils.isBlank(s1) && StringUtils.isBlank(s2)) {
                return 0;
            }

            if (StringUtils.isBlank(s1)) {
                return -1;
            }

            if (StringUtils.isBlank(s2)) {
                return 1;
            }

            //如果已中文数字字符开头
            char s1StartChar = s1.charAt(0);
            char s2StartChar = s2.charAt(0);

            if (CN_NUMBER_MAP.keySet().contains(s1StartChar + "")) {
                s1 = s1.replace(s1StartChar + "", CN_NUMBER_MAP.get(s1StartChar + ""));
            }

            if (CN_NUMBER_MAP.keySet().contains(s2StartChar + "")) {
                s2 = s2.replace(s2StartChar + "", CN_NUMBER_MAP.get(s2StartChar + ""));
            }

            // 如果以数字开头 ，通过长度比较
            if (Character.isDigit(s1.charAt(0)) || Character.isDigit(s2.charAt(0))) {

                //只有一个是数字，则数字排前面
                if (Character.isDigit(s1.charAt(0)) && !Character.isDigit(s2.charAt(0))) {
                    return -1;
                }

                if (!Character.isDigit(s1.charAt(0)) && Character.isDigit(s2.charAt(0))) {
                    return 1;
                }

                //匹配到有字符 "-" 的楼栋
                if (s1.contains("-") || s2.contains("-")) {
                    String beginCharacter1 = s1.split("-")[0];
                    String beginCharacter2 = s2.split("-")[0];
                    return compareDigitInString(beginCharacter1, beginCharacter2);
                }

                return compareDigitInString(s1, s2);
            }

            // 非数字开头

            // 首个字符相等，比较长度
            if (s1.charAt(0) == s2.charAt(0)) {

                if (s1.length() == s2.length()) {
                    return s1.compareTo(s2);
                }

                return s1.length() - s2.length();
            }

            for (int i = 0; i < s1.length() && i < s2.length(); i++) {

                int codePoint1 = s1.charAt(i);
                int codePoint2 = s2.charAt(i);

                if (Character.isSupplementaryCodePoint(codePoint1) || Character.isSupplementaryCodePoint(codePoint2)) {
                    i++;
                }

                if (codePoint1 != codePoint2) {

                    if (Character.isSupplementaryCodePoint(codePoint1)
                            || Character.isSupplementaryCodePoint(codePoint2)) {
                        return codePoint1 - codePoint2;
                    }

                    String pinyin1 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint1) == null ? null
                            : PinyinHelper.toHanyuPinyinStringArray((char) codePoint1)[0];
                    String pinyin2 = PinyinHelper.toHanyuPinyinStringArray((char) codePoint2) == null ? null
                            : PinyinHelper.toHanyuPinyinStringArray((char) codePoint2)[0];

                    if (pinyin1 != null && pinyin2 != null) { // 两个字符都是汉字
                        if (!pinyin1.equals(pinyin2)) {
                            return pinyin1.compareTo(pinyin2);
                        }
                    }

                    return codePoint1 - codePoint2;
                }
            }

            return s1.compareTo(s2);
        }

        private int compareDigitInString(String s1, String s2) {
            return new Long(s1.replaceAll("[^\\d]+", "")).compareTo(new Long(s2.replaceAll("[^\\d]+", "")));
        }
    }
}
