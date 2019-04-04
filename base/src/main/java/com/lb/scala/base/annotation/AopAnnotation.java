package com.lb.scala.base.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AopAnnotation {

    String type() default "all";


}
