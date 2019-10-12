package com.lb.scala.spark2.rdd

import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark._


object Data2Es {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("CombineTextInputFormat Demo")
    //sparkConf.setJars(List(SparkContext.jarOfClass(this.getClass).getOrElse("")))
    //System.setProperty("hadoop.home.dir", "D:\\hadoop\\")
    sparkConf.setMaster("local[*]")


    sparkConf.set("es.nodes", "localhost")
    sparkConf.set("es.port","9200")
    //sparkConf.set("es.index.auto.create", "true")
    //sparkConf.set("es.write.operation", "index")
    //sparkConf.set("es.mapping.id", "id")
    //如果装有x-pack 可以使用下面方式添加用户名密码
    //sc.set("es.net.http.auth.user","username")
    //sc.set("es.net.http.auth.pass","password")

    //sparkConf.set("es.cluster.name", "liubing")

    val sc = new SparkContext(sparkConf)




    val crmdata = sc.textFile("/Users/liubing/analysis-day.log").saveToEs("/abcd/test")

  }

}
