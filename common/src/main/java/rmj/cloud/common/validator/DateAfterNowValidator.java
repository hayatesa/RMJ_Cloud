package rmj.cloud.common.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.Date;

@FacesValidator("dateAfterNowValidator")
public class DateAfterNowValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null) {
            return;
        }

        Date date = (Date) value;

        if (date.before(new Date())) {
            FacesMessage message = new FacesMessage();
            message.setSummary("选择日期必须晚于当前日期。");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

}
