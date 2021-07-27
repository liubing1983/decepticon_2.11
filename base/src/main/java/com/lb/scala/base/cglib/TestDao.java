package com.lb.scala.base.cglib;

import net.sf.cglib.proxy.Enhancer;

public class TestDao {

    public static  void main(String[] args) {

        // 增强逻辑的实现
        DaoProxy daoProxy = new DaoProxy();

        //
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Dao.class);  // 代理目标
        enhancer.setCallback(daoProxy);   // 回调函数

        Dao dao = (Dao)enhancer.create();
        dao.update("before", 10);
        // dao.select();
    }
}
