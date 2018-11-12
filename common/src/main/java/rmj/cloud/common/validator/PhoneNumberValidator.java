package rmj.cloud.common.validator;

import org.apache.commons.lang.StringUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("phoneNumberValidator")
public class PhoneNumberValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null || StringUtils.isEmpty(value.toString()))
            return;

        String mobileRegex = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        String telephoneRegex = "[0]{1}[0-9]{2,3}[0-9]{1,20}"; // eg:"02089343"
        String HKMobileRegex = "^([3|5|6|9])\\d{7}$";
        if (((value.toString().length() != 8) && (value.toString().matches(mobileRegex) == false)
                && (value.toString().matches(telephoneRegex)) == false)
                || ((value.toString().length() == 8) && (value.toString().matches(HKMobileRegex)) == false)) {
            FacesMessage message = new FacesMessage();
            message.setSummary("手机号码输入有误，请重新输入");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

        if (value.toString().length() > 21) {
            FacesMessage message = new FacesMessage();
            message.setSummary("手机号码输入有误，请重新输入");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

    }
}
