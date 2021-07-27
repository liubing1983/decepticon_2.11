package com.lb.scala.spark2.rdd

import org.apache.hadoop.io.compress.SnappyCodec
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @ClassName Data2SnappyHdfs
  * @Description @TODO
  * @Author liubing
  * @Date 2020/4/16 10:43
  * @Version 1.0
  **/
object Data2SnappyHdfs extends App{

  // spark-shell --master  yarn  --driver-memory 2G  --driver-cores 2  --executor-memory  4G    --total-executor-cores 10  --name spark-shell-liubing


  val sparkConf = new SparkConf().setAppName("Data2SnappyHdfs Demo")
  //sparkConf.setMaster("local[10]")

  val sc = new SparkContext(sparkConf)


  val crmdata = sc.textFile("/user/hive/warehouse/tmp_qianzhan_da.db/user_lifecycle_holmes_info_d11_temp/dt_=2020-04-15/")
    .saveAsTextFile(args(0), classOf[SnappyCodec] )


}
