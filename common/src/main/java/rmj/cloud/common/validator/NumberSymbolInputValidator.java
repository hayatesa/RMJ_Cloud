package rmj.cloud.common.validator;

import org.apache.commons.lang.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("numberSymbolInputValidator")
public class NumberSymbolInputValidator implements Validator {
    //仅限输入数字、符号
    String REGEX = "^([0-9]" + "+|[\\x21-\\x2F]+|[\\x3A-\\x40]+|[\\x5A-\\x60]+|[\\x7A-\\x7E]+)" + "{0,}$";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (StringUtils.isNotEmpty(value.toString())) {
            if (!value.toString().matches(REGEX)) {
                FacesMessage message = new FacesMessage();
                message.setSummary("仅限输入数字、符号");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }

    }

}
