package rmj.cloud.common.util.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

/**
 * Excel注解，用以生成Excel表格文件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Excel {

    //列名
    String name() default "";

    //宽度
    int width() default 20;

    //忽略该字段
    boolean skip() default false;

    //字段为空判断
    boolean checkNull() default false;

    int length() default 50;

}
