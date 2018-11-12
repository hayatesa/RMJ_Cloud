package rmj.cloud.common.converter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.text.DecimalFormat;

@FacesConverter("moneyFormatConverter")
public class MoneyFormatConverter implements Converter {

    private static final Logger LOG = LoggerFactory.getLogger(MoneyFormatConverter.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || StringUtils.isEmpty(value.toString()))
            return StringUtils.EMPTY;
        String formatStr;
        try {
            formatStr = "￥" + new DecimalFormat("#,##0.00").format(value);
        } catch (Exception e) {
            LOG.warn("Money format exception, the value is: {}, and the exception is: {}", value.toString(),
                    e.getMessage());
            return value.toString();
        }
        return formatStr;
    }
}
