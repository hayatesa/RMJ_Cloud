package rmj.cloud.common.validator;

import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;

@FacesValidator("generalInputValidator")
public class GeneralInputValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (!StringUtils.isEmpty(value.toString())) {
            // A to Z, a to z, and 0 to 9, and _
            if (!Pattern.compile("^(\\w|[\\u4e00-\\u9fa5]|\\s|\\(|\\))+$").matcher(value.toString()).matches()) {

                FacesMessage message = new FacesMessage();
                message.setSummary("请填写住户姓名");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);

            }
        }
    }
}
