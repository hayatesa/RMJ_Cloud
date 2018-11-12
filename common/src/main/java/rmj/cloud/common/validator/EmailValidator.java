package rmj.cloud.common.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;

@FacesValidator("emailValidator")
public class EmailValidator implements Validator {
    //仅限输入中文、英文
    String REGEX = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (StringUtils.isNotEmpty(value.toString())) {
            if (!value.toString().matches(REGEX)) {
                FacesMessage message = new FacesMessage();
                message.setSummary("邮箱格式错误");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }

    }

}
