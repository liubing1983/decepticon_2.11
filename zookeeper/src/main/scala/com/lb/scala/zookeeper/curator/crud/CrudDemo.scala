package com.lb.scala.zookeeper.curator.crud

import com.lb.scala.zookeeper.curator.ZkConn
import org.apache.curator.framework.CuratorFramework
import org.apache.zookeeper.CreateMode

object CrudDemo  extends  App{

  val client: CuratorFramework = new ZkConn().getZKConnection()

  client.start()

  println(s" Namespace : ${client.getNamespace}")



  // 判断节点是否存在
//  if (client.checkExists.forPath("/a") == null){
//    //创建永久节点
//    client.create.forPath("/a", "永久节点".getBytes)
//    // 和上一行代码一致, 默认永久节点
//    //client.create.withMode(CreateMode.PERSISTENT).forPath("a", "永久节点".getBytes)
//  }else{
//    // 修改
//    client.setData.forPath("/a" , "修改a".getBytes)
//
//    Thread.sleep(10 * 1000)
//
//    //删除
//    client.delete.forPath("/a")
//  }


  //创建永久有序节点
  client.create.withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/b", "永久有序节点".getBytes)
  //创建永久有序节点
  client.create.withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/b", "永久有序节点2".getBytes)

  //创建临时节点
  client.create.withMode(CreateMode.EPHEMERAL).forPath("/c", "临时节点".getBytes)

  //创建临时有序节点
  client.create.withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/d", "临时有序节点".getBytes)

  client.create.withProtection.withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/e", "临时有序节点2".getBytes)


  /**
       1、PERSISTENT
        持久化目录节点，存储的数据不会丢失。
        2、PERSISTENT_SEQUENTIAL
        顺序自动编号的持久化目录节点，存储的数据不会丢失，并且根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名。
        3、EPHEMERAL
        临时目录节点，一旦创建这个节点的客户端与服务器端口也就是session 超时，这种节点会被自动删除。
        4、EPHEMERAL_SEQUENTIAL
        临时自动编号节点，一旦创建这个节点的客户端与服务器端口也就是session 超时，这种节点会被自动删除，并且根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名。
    */

  Thread.sleep(60 * 1000)
  client.close()
}
