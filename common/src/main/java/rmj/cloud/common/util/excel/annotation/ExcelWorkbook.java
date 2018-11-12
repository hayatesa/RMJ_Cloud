package rmj.cloud.common.util.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 作者 E-mail:ben.chen@accentrix.com
 * @version 创建时间：2018/7/2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ExcelWorkbook {
    String PASSWORD = "123456";

    String protectPassword() default PASSWORD;

    boolean protect() default false;
}
