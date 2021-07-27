package com.lb.scala.spark2.dataset

import java.net.{URL, URLClassLoader}
import java.util.Properties

import org.apache.spark.{SparkConf}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.slf4j.LoggerFactory
import scala.reflect.runtime.{universe => ru}

object JdbcDfDemo {

  val log = LoggerFactory.getLogger("com.lb.scala.spark2.dataset.JdbcDfDemo")

  val conf = new SparkConf().setAppName("MoveDataToMySqlTest") .setMaster("local[*]")
  val spark = SparkSession.builder.config(conf).getOrCreate


//  #内: 192.168.192.75   外: 123.59.154.80
//  # 生产: 192.168.193.149:3306
//  #driverClassName=com.mysql.jdbc.Driver
//  username=finup_ecology
//  password=wy613PXTCNB75RiVsrzqZNIZe8YsqBI


  val connectionProperties = new Properties()
  connectionProperties.put("user", "root")
  connectionProperties.put("password", "tidb@root")
  connectionProperties.put("driver" , "com.mysql.jdbc.Driver")
  // fetchsize：仅适用于read数据。JDBC提取大小，用于确定每次获取的行数。
  // 这可以帮助JDBC驱动程序调优性能，这些驱动程序默认具有较低的提取大小（例如，Oracle每次提取10行）。
  connectionProperties.put("fetchsize" , "5000")

//192.168.192.75:3306
  val  url = s"""jdbc:mysql://123.59.154.80:3306/nirvana?user=root&password=tidb@root"""

  val  dbtable = s"""nirvana.user"""





  def main(args: Array[String]): Unit = {


    val urls =  Array[URL] { new URL("file:/Users/liubing/IdeaProjects/decepticon_2.11/spark2/target/spark2-1.0-SNAPSHOT.jar")}
    val myClassLoader: URLClassLoader = new URLClassLoader(urls)
    // val classMirror = ru.runtimeMirror(getClass.getClassLoader)         //获取运行时类镜像
    val classMirror = ru.runtimeMirror(myClassLoader)
    val classTest = classMirror.staticModule("com.lb.scala.spark2.distributed_datax.plugin.MysqlSourceLb")          //获取需要反射object
    val methods = classMirror.reflectModule(classTest)                  //构造获取方式的对象
    val objMirror = classMirror.reflect(methods.instance)               //反射结果赋予对象
    val method = methods.symbol.typeSignature.member(ru.TermName(s"mysqlRead")).asMethod  //反射调用函数
    val result = objMirror.reflectMethod(method)(spark)           //最后带参数,执行这个反射调用的函数
    result.asInstanceOf[DataFrame].show(88)



    val classTest_write = classMirror.staticModule("com.lb.scala.spark2.distributed_datax.plugin.MysqlSinkLb")          //获取需要反射object
    val methods_write = classMirror.reflectModule(classTest_write)                  //构造获取方式的对象
    val objMirror_write = classMirror.reflect(methods_write.instance)               //反射结果赋予对象
    val method_write = methods_write.symbol.typeSignature.member(ru.TermName(s"mysqlWrite")).asMethod  //反射调用函数
    val result2 = objMirror_write.reflectMethod(method_write)(result.asInstanceOf[DataFrame], "append")         //最后带参数,执行这个反射调用的函数
    //result.asInstanceOf[DataFrame].show(88)






    println("======================================")
    println("======================================")
    println("======================================")
    println("======================================")
    println("======================================")
    println("======================================")
    println("======================================")



    Thread.sleep(10000)

    val jdbcDF1 = spark.read.jdbc("jdbc:mysql://123.59.154.80:3306?useUnicode=true&characterEncoding=UTF-8", "finup_ecology.zoology_dictionary", connectionProperties)
    jdbcDF1.schema
    jdbcDF1.count()
    jdbcDF1.toDF().limit(1000).foreach{
      x =>
        println(x.mkString("---"))
    }
    jdbcDF1.show(88)

    // 注册成临时表
    jdbcDF1.createOrReplaceTempView("dict")

    jdbcDF1.sqlContext.sql(" select * from  dict where parent_id = 1 ").show()
    jdbcDF1.sqlContext.sql(" select parent_id, count(1) from  dict  group by parent_id ").show()


    println("##########################-----------------_###############################")

    /**
      * partitionColumn, lowerBound, upperBound, numPartitions：
        这些options仅适用于read数据。这些options必须同时被指定。他们描述，如何从多个workers并行读取数据时，分割表。
        partitionColumn：必须是表中的数字列。
        lowerBound和upperBound仅用于决定分区的大小，而不是用于过滤表中的行。
        表中的所有行将被分割并返回。
      */
    val jdbcDF2 = spark.read.format("jdbc").options(
      Map("url" ->  url,
        "dbtable" -> dbtable,
        "partitionColumn" -> "id",
        "lowerBound" -> "1",
        "upperBound" -> "5000",
        "numPartitions" -> "20"
      )).load()
    jdbcDF2.createOrReplaceTempView("user")
    // partitions 数量, 由numPartitions定义
    println("-------"+jdbcDF2.rdd.partitions.size +"========================")
    println(jdbcDF2.count)
    jdbcDF2.show(66)
    jdbcDF2.sqlContext.sql("  select * from user limit 10").show()
    jdbcDF2.sqlContext.sql(" select sex, count(1) from  user  group by sex ").show()

    println("##########################=================###############################")

    // 过滤条件
    val jdbcDF3 = spark.read.jdbc(  url, dbtable, Array("sex = 1"), connectionProperties)
    // 注册成临时表
    jdbcDF3.createOrReplaceTempView("user3")

    jdbcDF3.sqlContext.sql(" select * from  user3 where sex = 1 ").show()
    jdbcDF3.sqlContext.sql(" select sex, count(1) from  user3  group by sex ").show()



    //val jdbcDF3 = spark.read.jdbc("jdbc:mysql://127.0.0.1:3306", "lb.lend_customer", connectionProperties)

   // jdbcDF3.createOrReplaceTempView("lend_customer")


  // val jdbcDF4 =  jdbcDF3.sqlContext.sql("select a.id, a.id_no,a.name from lend_customer as a ,lend_customer_1 as b where  a.id = b.id order by id desc  limit 1")
  //  jdbcDF4.show()

//    jdbcDF4.write
//      .format("jdbc")
//      .option("url", "jdbc:mysql://127.0.0.1:3306/lb")
//      .option("dbtable", "tablename3")
//      .option("user", "root")
//      .option("password", "123456")
//      .save()
//
//     jdbcDF4.write.jdbc("jdbc:mysql://127.0.0.1:3306/lb", "tablename2", connectionProperties)

    spark.close()
  }
}
