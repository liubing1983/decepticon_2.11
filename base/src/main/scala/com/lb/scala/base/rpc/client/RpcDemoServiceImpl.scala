package com.lb.scala.base.rpc.client

class RpcDemoServiceImpl  extends RpcDemoService{
  override def max(a: Int, b: Int): Int = {
    if(a > b) a else b
  }
}
