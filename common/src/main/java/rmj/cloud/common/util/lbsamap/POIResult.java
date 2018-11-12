package rmj.cloud.common.util.lbsamap;

import java.util.List;

/**
 * @author 作者 E-mail:ben.chen@accentrix.com
 * @version 创建时间：2018/7/17
 */

public class POIResult {
    private String suggestion;
    private Integer count;
    private Integer infocode;
    private List<POIVO> pois;
    private Boolean status;
    private String info;

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getInfocode() {
        return infocode;
    }

    public void setInfocode(Integer infocode) {
        this.infocode = infocode;
    }

    public List<POIVO> getPois() {
        return pois;
    }

    public void setPois(List<POIVO> pois) {
        this.pois = pois;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean isStatus) {
        this.status = isStatus;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
