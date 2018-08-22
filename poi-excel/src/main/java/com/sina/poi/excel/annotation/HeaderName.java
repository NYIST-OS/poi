package com.sina.poi.excel.annotation;

import java.lang.annotation.*;

/**
 * @ClassName HeaderName
 * @Description:
 * @Author 段浩杰
 * @Date 2018/8/22 13:23
 * @Version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HeaderName {

    /**
     * 定义 excel header 的名称
     */
    String value();

    /**
     * 从 0 开始
     */
    int index();

}
