package rmj.cloud.common.converter;

import org.apache.commons.lang3.StringUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.text.DecimalFormat;

@FacesConverter("radixPointFormatConverter")
public class RadixPointFormatConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || StringUtils.isEmpty(value.toString()))
            return null;
        try {
            StringBuffer pattern = new StringBuffer("#0.00");
            String valueStr = value.toString();
            if (valueStr.contains(".")) {
                int endIndex = valueStr.substring(valueStr.indexOf(".") + 1).length() > 5 ? 5
                        : valueStr.substring(valueStr.indexOf(".") + 1).length();
                String integerStr = valueStr.substring(0, valueStr.indexOf("."));
                String decimalStr = valueStr.substring(valueStr.indexOf(".") + 1, integerStr.length() + 1 + endIndex);
                for (int i = endIndex - 1; i > 0; i--) {
                    if (decimalStr.charAt(decimalStr.length() - 1) == '0')
                        decimalStr = decimalStr.substring(0, decimalStr.length() - 1);
                }
                int zeroNumber = decimalStr.length() < 2 ? 2 : decimalStr.length();
                for (int i = 2; i < zeroNumber; i++)
                    pattern.append("0");
            }
            return new DecimalFormat(pattern.toString()).format(value);
        } catch (Exception e) {
            e.printStackTrace();
            return new DecimalFormat("#0.00").format(value);
        }
    }

    public static void main(String[] args) {
        String value = "123.10600027";
        StringBuffer pattern = new StringBuffer("#0.00");
        String valueStr = value.toString();
        if (valueStr.contains(".")) {
            int endIndex = valueStr.substring(valueStr.indexOf(".") + 1).length() > 5 ? 5
                    : valueStr.substring(valueStr.indexOf(".") + 1).length();
            String integerStr = valueStr.substring(0, valueStr.indexOf("."));
            String decimalStr = valueStr.substring(valueStr.indexOf(".") + 1, integerStr.length() + 1 + endIndex);
            for (int i = endIndex - 1; i > 0; i--) {
                if (decimalStr.charAt(decimalStr.length() - 1) == '0')
                    decimalStr = decimalStr.substring(0, decimalStr.length() - 1);
            }
            int zeroNumber = decimalStr.length() < 2 ? 2 : decimalStr.length();
            for (int i = 2; i < zeroNumber; i++)
                pattern.append("0");
        }
        System.out.println("pattern: " + pattern.toString());
    }
}
