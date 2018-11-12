package rmj.cloud.common.validator;

import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;

@FacesValidator("chineseNumberEnglishInputValidator")
public class ChineseNumberEnglishInputValidator implements Validator {
    //仅限输入中文、英文、数字
    String REGEX = "^([\\u4e00-\\u9fa5]" + "+|[a-zA-Z0-9])" + "{0,}$";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (StringUtils.isNotEmpty(value.toString())) {
            if (!Pattern.compile(REGEX).matcher(value.toString()).matches()) {
                FacesMessage message = new FacesMessage();
                message.setSummary("可输入中文，数字和英文");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }

    }

}
