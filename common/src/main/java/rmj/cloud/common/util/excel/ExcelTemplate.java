package rmj.cloud.common.util.excel;

import rmj.cloud.common.util.excel.annotation.ExcelField;

import java.io.Serializable;

public abstract class ExcelTemplate implements Serializable, GenerateExcel {

    public enum ExcelTemplateAction {
        EXPORT_ERROR_LIST, EXPORT_DATA_LIST, IMPORT_FOR_NEW, IMPORT_FOR_UPDATE
    }

    public ExcelTemplateAction action;

    private static final long serialVersionUID = 1L;

    @ExcelField(title = "失败原因", errorMessageField = true)
    private String errorMessages = "";

    public String getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(String errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void addErrorMessages(String errorMessages) {
        this.errorMessages = this.errorMessages + errorMessages + ",";
    }

    /**
     * 自定义检查方法
     *
     * @throws Exception
     */
    public abstract void check4New() throws Exception;

    /**
     * 自定义检查方法
     *
     * @throws Exception
     */
    public abstract void check4Update() throws Exception;

    /**
     * 模板之间的比较
     * 当两个模板相等时，则抛出异常
     *
     * @param excelTemplate1
     * @param excelTemplate2
     * @return
     */
    protected boolean templateEqual(ExcelTemplate excelTemplate1, ExcelTemplate excelTemplate2) {
        return excelTemplate1.equals(excelTemplate2);
    }

    public ExcelTemplateAction getAction() {
        return action;
    }

    public void setAction(ExcelTemplateAction action) {
        this.action = action;
    }

}
