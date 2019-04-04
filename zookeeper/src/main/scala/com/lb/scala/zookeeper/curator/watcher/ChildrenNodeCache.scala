package com.lb.scala.zookeeper.curator.watcher

import com.lb.scala.zookeeper.curator.ZkConn
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache.{PathChildrenCache, PathChildrenCacheEvent, PathChildrenCacheListener}
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent._


object ChildrenNodeCache {

  val client : CuratorFramework = ZkConn().getZKConnection();

  def main(args: Array[String]): Unit = {
    client.start()

    // 注册监控
    val cache : PathChildrenCache = new PathChildrenCache(client, "/", true)

    // 启动监控
    cache.start();

    // 监控回调函数
    cache.getListenable.addListener(new PathChildrenCacheListener {
      override def childEvent(client: CuratorFramework, event: PathChildrenCacheEvent): Unit = {
        event.getType match {
          case  Type.CHILD_ADDED => println(s"add: ${event.getData.getPath} - ${new String(event.getData.getData)}")
          case  Type.CHILD_UPDATED => println(s"update: ${event.getData.getPath} - ${new String(event.getData.getData)}")
          case  Type.CHILD_REMOVED => println(s"delete: ${event.getData.getPath} - ${new String(event.getData.getData)}")
          case  _ => println(s"other: ${event.getData.getPath} - ${new String(event.getData.getData)}")
        }
      }
    })

    while (true){
      println("123")
      Thread.sleep(10 * 1000)
    }
  }


}
