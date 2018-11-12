package rmj.cloud.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CommonUtils {

    public static final String RESOURCE_BASE_URL = "res/";

    public static final String FOLDER_PATH = "folder_path";
    public static final String FILE_NAMES = "file_names";

    /** for update images */
    public static final String DELETED_FILE_NAMES = "deleted_file_names";
    public static final String RESERVED_FILE_NAMES = "freserved_ile_names";

    public static String createFilePath(String folderPath, String fileNames) {
        if (StringUtils.isAnyEmpty(folderPath, fileNames))
            return null;

        if (!folderPath.endsWith("/")) {
            folderPath = folderPath + "/";
        }
        StringBuffer filePath = new StringBuffer();
        String[] fileNameArr = fileNames.split(";");
        for (String fileName : fileNameArr) {
            filePath.append(folderPath + fileName);
            filePath.append(";");
        }
        return filePath.toString().substring(0, filePath.toString().lastIndexOf(";")).replaceAll(" ", "%20");
    }

    public static List<String> createFilePathList(String folderPath, String fileNames) {
        if (StringUtils.isNoneBlank(folderPath, fileNames)) {
            List<String> filePaths = Lists.newArrayList();
            for (String fileName : fileNames.split(";")) {
                filePaths.add(folderPath + fileName);
            }
            return filePaths;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public static String createFilePath(String folderPath, List<String> fileNames) {
        if (StringUtils.isAnyEmpty(folderPath) || CollectionUtils.isEmpty(fileNames))
            return null;

        StringBuffer filePath = new StringBuffer();
        for (String fileName : fileNames) {
            filePath.append(folderPath + "/" + fileName);
            filePath.append(";");
        }
        return filePath.toString().substring(0, filePath.toString().lastIndexOf(";")).replaceAll(" ", "%20");
    }

    public static String hideRealName(String name) {
        if (StringUtils.isEmpty(name))
            return StringUtils.EMPTY;
        if (name.length() < 2)
            return name + "**";
        String hideName = name.substring(0, 1);
        int i = 0;
        for (; i < name.length(); i++) {
            if (i > 1)
                break;
            hideName += "*";
        }
        if (name.length() > i)
            hideName += name.substring(i + 1, name.length());
        return hideName;
    }

    public static String hideFrontRealName(String name) {
        if (StringUtils.isEmpty(name))
            return StringUtils.EMPTY;
        String hideName = name.substring(name.length() - 1, name.length());
        int i = 0;
        StringBuffer stringBuffer = new StringBuffer();
        for (; i < name.length() - 1; i++) {
            stringBuffer.append('*');
        }
        return stringBuffer.append(hideName).toString();
    }

    public static String hideMobile(String mobile) {
        if (StringUtils.isEmpty(mobile))
            return StringUtils.EMPTY;
        String hideMobile = mobile.substring(0, 3);
        int i = 3;
        for (; i < mobile.length(); i++) {
            if (i > 6)
                break;
            hideMobile += "*";
        }
        if (mobile.length() > i)
            hideMobile += mobile.substring(i, mobile.length());
        return hideMobile;
    }

    public static Map<String, String> splitToFolderAndNames(List<String> fileUrls) {
        if (CollectionUtils.isEmpty(fileUrls))
            return Maps.newHashMap();
        Map<String, String> folderNameMap = Maps.newHashMap();
        final String[] folder = new String[1];
        final String[] fileNames = new String[1];
        fileUrls.forEach(fileUrl -> {
            int splitIndex = fileUrl.lastIndexOf("/");
            folder[0] = fileUrl.substring(0, splitIndex + 1);
            String fileName = fileUrl.substring(splitIndex + 1, fileUrl.length());
            fileNames[0] = StringUtils.isEmpty(fileNames[0]) ? fileName : (fileNames[0] + ";" + fileName);
        });
        if (StringUtils.isNotEmpty(folder[0])) {
            folderNameMap.put(FOLDER_PATH, folder[0]);
        }
        if (StringUtils.isNotEmpty(fileNames[0])) {
            folderNameMap.put(FILE_NAMES, fileNames[0]);
        }
        return folderNameMap;
    }

    /**
     * 隐藏银行卡信息
     *
     * @param bankNo
     * @return
     */
    public static String hideBankNo(String bankNo) {
        if (StringUtils.isNoneBlank(bankNo)) {
            //显示后面四位
            final String REGEX = "\\w(?=\\w{4})";
            final String REPLACEMENT = "*";

            return bankNo.replaceAll(REGEX, REPLACEMENT);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 每四个字符增加一个空格
     */
    public static String bankNoAddBlank(String bankNo) {
        if (StringUtils.isNoneBlank(bankNo) && bankNo.length() > 4) {
            String bankString = "";
            char[] charr = bankNo.toCharArray();
            for (int i = 0; i < charr.length; i++) {
                if (i % 4 == 0 && i > 0) {
                    bankString += " ";
                }
                bankString += charr[i];
            }
            return bankString;
        }
        return bankNo;
    }

    /**
     * 隐藏身份证件号信息
     *
     * @param identityNo
     * @return
     */
    public static String hideIdentityNo(String identityNo) {
        if (StringUtils.isNoneBlank(identityNo)) {
            char[] chars = identityNo.toCharArray();
            StringBuilder hideIdentityNo = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                if (i == 0 || i == chars.length - 1) {
                    hideIdentityNo.append(chars[i]);
                } else {
                    hideIdentityNo.append('*');
                }
            }
            return hideIdentityNo.toString();
        }
        return StringUtils.EMPTY;
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = null;

        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 改用UnitTreeComparator 工具里面的newComparator
     * @return
     */
    @Deprecated
    public static Comparator<String> newUnitTreeComparator() {
        return new UnitTreeComparator();
    }

    static class UnitTreeComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            // 如果以数字开头 ，通过长度比较
            if (Character.isDigit(s1.charAt(0)) || Character.isDigit(s2.charAt(0))) {
                //只有一个是数字，则数字排前面
                if (Character.isDigit(s1.charAt(0)) && !Character.isDigit(s2.charAt(0)))
                    return -1;
                if (!Character.isDigit(s1.charAt(0)) && Character.isDigit(s2.charAt(0)))
                    return 1;
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
                if (s1.length() == s2.length())
                    return s1.compareTo(s2);
                else
                    return s1.length() - s2.length();
            } else {
                return s1.charAt(0) - s2.charAt(0);
            }

        }

        private int compareDigitInString(String s1, String s2) {
            return new Long(s1.replaceAll("[^\\d]+", "")).compareTo(new Long(s2.replaceAll("[^\\d]+", "")));
        }
    }

    public static Map<String, List<String>> getDiffFileNames(String oldFileNames, String newFileNames) {
        if (StringUtils.isEmpty(oldFileNames))
            return Maps.newHashMap();
        String[] oldNameArr = oldFileNames.split(";");
        Map<String, List<String>> fileNameMap = Maps.newHashMap();
        if (StringUtils.isEmpty(newFileNames)) {
            fileNameMap.put(DELETED_FILE_NAMES, Arrays.asList(oldNameArr));
            return fileNameMap;
        }
        String[] newNameArr = newFileNames.split(";");
        List<String> delNameList = Lists.newArrayList();
        List<String> newNameList = Arrays.asList(newNameArr);
        List<String> reservedNameList = Lists.newArrayList();
        for (String oldName : oldNameArr) {
            if (newNameList.contains(oldName)) {
                reservedNameList.add(oldName);
            } else {
                delNameList.add(oldName);
            }
        }
        fileNameMap.put(DELETED_FILE_NAMES, delNameList);
        fileNameMap.put(RESERVED_FILE_NAMES, reservedNameList);
        return fileNameMap;
    }

    public static String generateUniqueFileName(String originalName) {
        if (StringUtils.isEmpty(originalName) || !originalName.contains("."))
            return StringUtils.EMPTY;
        String fileType = originalName.substring(originalName.lastIndexOf("."));
        String uniqueStr = UUID.randomUUID().toString().replace("-", "") + (new Long(new Date().getTime()).toString());
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            Base64.Encoder base64en = Base64.getEncoder();
            uniqueStr = base64en.encodeToString(md5.digest(uniqueStr.getBytes("utf-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return uniqueStr.replace("=", "").replace("+", "-").replace("/", "_") + fileType;
    }

}
