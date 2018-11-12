package rmj.cloud.common.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;

@FacesValidator("chineseAndEnglishInputValidator")
public class ChineseAndEnglishInputValidator implements Validator {
    //仅限输入中文、英文
    String REGEX = "^([\\u4e00-\\u9fa5]" + "+|[a-zA-Z])" + "{0,}$";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (StringUtils.isNotEmpty(value.toString())) {
            if (!value.toString().matches(REGEX)) {
                FacesMessage message = new FacesMessage();
                message.setSummary("只能输入中文、英文");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }

    }

}
