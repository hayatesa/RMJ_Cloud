package rmj.cloud.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LXSmsClientSendUtils {
    private static final Logger LOG = LoggerFactory.getLogger(LXSmsClientSendUtils.class);

    /**
     * <p>
     * <date>2012-03-01</date><br/>
     * <span>发送信息方法1--必须传入必填内容</span><br/>
     * <p>
     * 其一：发送方式，默认为POST<br/>
     * 其二：发送内容编码方式，默认为UTF-8
     * </p>
     * <br/>
     * </p>
     *
     * @param url
     *            ：必填--发送连接地址URL--比如>http://inter.smswang.net:7803/sms
     * @param account
     *            ：必填--用户帐号
     * @param password
     *            ：必填--用户密码
     * @param mobile
     *            ：必填--发送的手机号码，多个可以用逗号隔比如>13512345678,13612345678
     * @param content
     *            ：必填--实际发送内容，
     * @param scorpid
     *            ：必填--公司代码 * @param sprdid ：必填--产品编号
     * @return 返回发送信息之后返回字符串
     */
    public static boolean sms(String url, String account, String password, String scorpid, String sprdid, String mobile,
            String content) {

        try {
            account = URLEncoder.encode(account, "UTF-8");
            password = URLEncoder.encode(password, "UTF-8");
            StringBuilder send = new StringBuilder();
            send.append("&sname=").append(account);
            send.append("&spwd=").append(password);
            send.append("&scorpid=").append(scorpid);
            send.append("&sprdid=").append(sprdid);
            send.append("&sdst=").append(mobile);
            send.append("&smsg=").append(URLEncoder.encode(content, "UTF-8"));
            return send(send.toString(), url);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    private static boolean send(String postData, String postUrl) {
        try {
            // 发送POST请求
            URL url = new URL(postUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Length", "" + postData.length());
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(postData);
            out.flush();
            out.close();

            // 获取响应状态
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LOG.error("sendVerifyCode -> connect failed! ");
                return false;
            }
            // 获取响应内容体
            String line, result = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            int state = Integer
                    .parseInt(result.substring(result.indexOf("<State>") + 7, result.lastIndexOf("</State>")));
            if (state == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

}
