package rmj.cloud.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;

/**
 * @author 作者 E-mail:ben.chen@accentrix.com
 * @version 创建时间：2018/7/16
 */

public class HttpClientUtils {

    private static CloseableHttpClient httpclient;

    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000)
            .setConnectionRequestTimeout(20000).build();

    static {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(200);
        connManager.setDefaultMaxPerRoute(20);
        httpclient = HttpClients.custom().setConnectionManager(connManager).build();
    }

    public static CloseableHttpResponse httpGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpGet.setProtocolVersion(HttpVersion.HTTP_1_1);
        return httpclient.execute(httpGet);
    }

    public static CloseableHttpResponse httpPost(String url, JSONObject jsonParam) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);
        StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");// 解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        return httpclient.execute(httpPost);
    }

}
