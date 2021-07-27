package com.lb.scala.base.cglib;

import com.lb.scala.base.annotation.AopAnnotation;
import com.lb.scala.base.annotation.MyJavaAnnotation;

/**
 * 需要被增强的类
 */
public class Dao {

    @MyJavaAnnotation.After
    @MyJavaAnnotation.Befoer
    public void update(String s, int i){
        System.out.println("dao.update");
    }

    @AopAnnotation(type="after")
    public void select(){
        System.out.println("dao.select");
    }
}
