package com.lb.scala.base.cglib;

import com.lb.scala.base.annotation.AopAnnotation;
import com.lb.scala.base.annotation.MyJavaAnnotation;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DaoProxy implements MethodInterceptor {


    /**
     *
     * @param o  表示要进行增强的对象
     * @param method  表示拦截的方法
     * @param objects  表示参数列表，基本数据类型需要传入其包装类型，如int-->Integer、long-Long、double-->Double
     * @param methodProxy 表示对方法的代理，invokeSuper方法表示对被代理对象方法的调用
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        String type = "";

        // 增强的方法名
        System.out.println( method.getName()+"-----------");

        // 遍历打印所有注解
        for(Annotation ann : method.getAnnotations()){
            System.out.println(ann.toString());
        }

        // 判断是否存在前置增强  方式1
        Annotation befoer = method.getAnnotation(MyJavaAnnotation.Befoer.class);
        if(null != befoer){
            System.out.println("Before Method Invoke");
        }


        // 判断是否存在前置增强  方式2
        if(null != method.getAnnotation(AopAnnotation.class) ){
            type = method.getAnnotation(AopAnnotation.class).type();
        }

        if( type.equals("befoer") || type.equals("all")){
            System.out.println("判断是否存在前置增强  方式2");
        }


        // 执行程序
        methodProxy.invokeSuper(o, objects);


        // 判断是否存在后置增强
        Annotation after = method.getAnnotation(MyJavaAnnotation.After.class);
        if(null != after){
            System.out.println("After Method Invoke");
        }


        // 判断是否存在后置增强  方式2
        if( type.equals("after") || type.equals("all")){
            System.out.println("判断是否存在前置增强  方式2");
        }

        return o;
    }
}
