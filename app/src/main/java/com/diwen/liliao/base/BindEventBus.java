package com.diwen.liliao.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created By  tian on 2020/3/16
 * Describe:：需要使用eventbus的activit和Fragment都需要以注解的方式绑定到此
 */



@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindEventBus {

}
