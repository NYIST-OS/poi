package com.sina.poi.excel.annotation;

import java.lang.annotation.*;

/**
 * @ClassName ExcelMeta
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 13:22
 * @Version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelMeta {

    /**
     * excel 名称, ${date} 可表示当前日期(uuuu-MM-dd),
     */
    String name();

    /**
     * sheet 名称,默认为 {@link #name()} 不包含 ${date}
     */
    String sheet() default "";
}