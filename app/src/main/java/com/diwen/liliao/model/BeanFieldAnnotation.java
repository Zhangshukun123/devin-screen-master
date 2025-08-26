package com.diwen.liliao.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created By  tian on 2021/8/31
 * Describe:
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanFieldAnnotation {

    /**
     * 标注该属性的顺序
     * @return 该属性的顺序
     */
    int order();
}