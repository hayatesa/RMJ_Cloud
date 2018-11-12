package rmj.cloud.common.converter;

import rmj.cloud.common.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "bankAccountNoConverter")
public class BankAccountNoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        //是否需要转换
        Boolean isShow = (Boolean) component.getAttributes().get("isShow");
        if (isShow == null || isShow) {
            return value;
        }
        return CommonUtils.bankNoAddBlank(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return null;
        }
        //是否需要转换
        Boolean isShow = (Boolean) component.getAttributes().get("isShow");
        if (isShow == null || isShow) {
            return value.toString();
        }
        return CommonUtils.bankNoAddBlank(value.toString());
    }

}
