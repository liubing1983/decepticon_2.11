package com.lb.scala.base.rpc.client

trait RpcDemoService {

  def max (a: Int, b: Int) : Int

  val  max2 = (a: Int, b: Int) => Int
}
