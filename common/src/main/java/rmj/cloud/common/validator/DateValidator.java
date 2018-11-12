package rmj.cloud.common.validator;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("dateValidator")
public class DateValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        UIInput startDateComponent = (UIInput) component.getAttributes().get("startDateComponent");

        if (!startDateComponent.isValid()) {
            return; // Already invalidated. Don't care about it then.
        }

        Date startDate = (Date) startDateComponent.getValue();
        if (value == null && startDate == null) {
            return;
        }

        Date endDate = (Date) value;

        if (value == null && startDate != null) {
            FacesMessage message = new FacesMessage();
            message.setSummary("已填写开始日期，请填写结束日期！");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            //startDateComponent.resetValue();
            throw new ValidatorException(message);
        }

        if (value != null && startDate == null) {
            FacesMessage message = new FacesMessage();
            message.setSummary("已填写结束日期，请填写开始日期！");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            //startDateComponent.resetValue();
            throw new ValidatorException(message);
        }
        if (startDate.after(endDate)) {
            FacesMessage message = new FacesMessage();
            message.setSummary("结束日期必须晚于开始日期。");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            //startDateComponent.resetValue();
            throw new ValidatorException(message);

        }
    }

}
