package com.lb.scala.base.annotation;


import java.lang.annotation.*;


public class MyJavaAnnotation {


    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    public @interface Befoer {

    }


    @Target({ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    public @interface After {

    }
}
