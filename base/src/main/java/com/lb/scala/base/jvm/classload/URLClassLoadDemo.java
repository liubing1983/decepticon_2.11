package com.lb.scala.base.jvm.classload;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @ClassName URLClassLoadDemo
 * @Description @TODO
 * @Author liubing
 * @Date 2020/6/18 10:25
 * @Version 1.0
 **/
public class URLClassLoadDemo {

    //val file = new File("")

    public static void main(String[] args) throws Exception{

        URL[] urls = new URL[] { new URL("file:/Users/liubing/IdeaProjects/decepticon_2.11/soundwave/target/soundwave-1.0-SNAPSHOT.jar")};

        URLClassLoader myClassLoader = new URLClassLoader(urls);


        Class<?> myClass = myClassLoader.loadClass("com.lb.scala.soundwave.tools.RandomData");

        // val logClass = myClassLoader.loadClass("com.lb.scala.base.log.LogDemo");


        System.out.println(myClass.isLocalClass());

        Object obj  = myClass.newInstance();
        Method method = myClass.getMethod("getRandomChineseCharacters", int.class);

        System.out.println( method.getName());

        String s = method.invoke(obj, 100).toString();
        System.out.println(s);
    }



}
