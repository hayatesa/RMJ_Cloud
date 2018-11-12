package rmj.cloud.common.util.lbsamap;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;

/**
 * @author 作者 E-mail:ben.chen@accentrix.com
 * @version 创建时间：2018/7/17
 */

public class POIVO {
    private String address;
    private String distance;
    private String bizExt;
    private String pname;
    private String importance;
    private String bizType;
    private String cityname;
    private String type;
    private String typecode;
    private String shopinfo;
    private String poiweight;
    private String name;
    private String location;
    private String tel;
    private String shopid;
    private String id;

    private BigDecimal getLongitude() {
        if (StringUtils.isNotBlank(getLocation())) {
            if (getLocation().split(",").length > 1) {
                return new BigDecimal(getLocation().split(",")[0]);
            }
        }
        return null;
    }

    private BigDecimal getLatitude() {
        if (StringUtils.isNotBlank(getLocation())) {
            if (getLocation().split(",").length > 1) {
                return new BigDecimal(getLocation().split(",")[1]);
            }
        }
        return null;

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBizExt() {
        return bizExt;
    }

    public void setBizExt(String bizExt) {
        this.bizExt = bizExt;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypecode() {
        return typecode;
    }

    public void setTypecode(String typecode) {
        this.typecode = typecode;
    }

    public String getShopinfo() {
        return shopinfo;
    }

    public void setShopinfo(String shopinfo) {
        this.shopinfo = shopinfo;
    }

    public String getPoiweight() {
        return poiweight;
    }

    public void setPoiweight(String poiweight) {
        this.poiweight = poiweight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
