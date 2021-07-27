package com.lb.scala.base.cglib;

import com.lb.scala.base.annotation.AopAnnotation;
import com.lb.scala.base.annotation.MyJavaAnnotation;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class DaoProxy implements MethodInterceptor {

    Logger log =  LoggerFactory.getLogger(DaoProxy.class);


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

        // 注解中的属性
        String type = "";

        // 增强的方法名
        log.info("需增强的方法名: "+ method.getName());
        log.info(method.getParameterCount()+"");
        for(int i = 0; i<method.getParameterCount() ; i++){
            log.info("遍历参数: "+ i);
            log.info(method.getParameterTypes()[i]+"");
            log.info(objects[i]+"");

        }

        // 遍历打印所有注解
        log.info("遍历所有的注解");
        for(Annotation ann : method.getAnnotations()){
            log.info(ann.toString());
        }
        log.info("遍历所有的注解结束!!");

        // 判断是否存在前置增强  方式1
        if(method.isAnnotationPresent(MyJavaAnnotation.Befoer.class)){
            log.info("存在前置增强, Before Method Invoke!!");
        }



        // 判断是否存在前置增强  方式2
        if(method.isAnnotationPresent(AopAnnotation.class)){
            log.info("判断是否存在前置增强  方式2");
            type = method.getAnnotation(AopAnnotation.class).type();
        }

        if( type.equals("befoer") || type.equals("all")){
            log.info("存在前置增强  方式2");
        }


        // 执行程序
        methodProxy.invokeSuper(o, objects);


        // 判断是否存在后置增强
        if(method.isAnnotationPresent(MyJavaAnnotation.After.class)){
            log.info("存在后置增强, After Method Invoke");
        }


        // 判断是否存在后置增强  方式2
        if( type.equals("after") || type.equals("all")){
            log.info("后置增强  方式2");
        }

        log.info(method.getName()+"增强结束  \n\n"  );
        return o;
    }
}
