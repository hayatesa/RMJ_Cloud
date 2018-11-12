package rmj.cloud.common.validator;

import org.apache.commons.lang.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * Created by wiley.luo on 2018/7/12
 */
@FacesValidator("identityNumberValidator")
public class IdentityNumberValidator implements Validator {
    //仅限输入中文、英文、小括号
    String REGEX = "^[A-Za-z0-9()]+$";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (StringUtils.isNotEmpty(value.toString())) {
            if (!value.toString().matches(REGEX)) {
                FacesMessage message = new FacesMessage();
                message.setSummary("仅限输入数字和英文");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }

    }

}
