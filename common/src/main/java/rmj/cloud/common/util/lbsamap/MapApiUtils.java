package rmj.cloud.common.util.lbsamap;

import rmj.cloud.common.util.HttpClientUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * @author 作者 E-mail:ben.chen@accentrix.com
 * @version 创建时间：2018/7/16
 */

public class MapApiUtils {
    private static Logger LOG = LoggerFactory.getLogger(MapApiUtils.class);
    /**
     * 关键字搜索
     * api:https://restapi.amap.com/v3/place/text?parameters
     */
    final String KEYWORD_SEARCH_URL = "https://restapi.amap.com/v3/place/text?key={key}&keywords={keywords}";

    public POIVO findFirstLocationByKeyword(@NotNull String key, @NotNull String keywords) {
        //key = "10699eff22cb375c1cac8044ffa79027";
        //keywords = "津滨腾越大厦";
        try {
            String url = KEYWORD_SEARCH_URL;
            url = url.replace("{key}", key).replace("{keywords}", keywords);
            LOG.info("MapApiUtils keywordSearch url:{}", url);

            CloseableHttpResponse closeableHttpResponse = HttpClientUtils.httpGet(url);
            String result = EntityUtils.toString(closeableHttpResponse.getEntity(), "utf-8");

            LOG.info("MapApiUtils keywordSearch result:{}", result);
            POIResult poiResult = JSON.parseObject(result, POIResult.class);
            if (Boolean.TRUE.equals(poiResult.getStatus())) {
                List<POIVO> pois = poiResult.getPois();
                if (CollectionUtils.isNotEmpty(pois)) {
                    return pois.get(0);
                }
            }
        } catch (IOException e) {
            LOG.error("http io exception{}", e.getMessage());
        }
        return null;
    }

}
