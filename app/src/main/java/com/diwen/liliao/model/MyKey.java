package com.diwen.liliao.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自用key
 * Created by Administrator on 2018/3/5 0005.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyKey {
    String majorKey();
    String assistantKey() default "";
    /* 是否检查参数能否为空 */
    boolean checkEmpty() default false;
    Class observableFieldType() default String.class;

}
