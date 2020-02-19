package com.lb.scala.spark2.jobhistory

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.v2.hs.JobHistory
import java.io.IOException

import org.apache.hadoop.mapred.{JobClient, JobConf}
import org.apache.hadoop.yarn.api.records.ApplicationId
import org.apache.spark.SparkContext
import org.apache.hadoop.mapreduce.JobID
import org.apache.hadoop.mapreduce.TypeConverter
import scala.collection.JavaConversions._


/**
  * @ClassName getJobHistory
  * @Description @TODO
  * @Author liubing
  * @Date 2019/11/26 14:46
  * @Version 1.0
  **/
object getJobHistory {


  def main(args: Array[String]): Unit = {
    //var jobClient: JobClient = null
    //var jobs: Array[JobStatus];

    //Configuration conf = new Configuration();
    //conf.addResource(new Path("/hadoop/etc/hadoop/mapred-site.xml"));

    println("==========---------------")
    try {
     // val sc = SparkContext.getOrCreate()
      //jobClient = new JobClient(conf)


      val conf2: Configuration = new Configuration()
      val his: JobHistory = new JobHistory()

      // val appid : ApplicationId =  ApplicationId.newInstance(1574738619799L, 9999)

      // println("==========---------------"+ appid.toString)

      his.init(conf2)

      // newInstance(long clusterTimestamp, int id)
      // application_1574738619799_9999
      // ApplicationId.clusterTimestamp(1574738619799L, 9999)


      // appid.newInstance(1574738619799L, 9999)

      val JobID = TypeConverter.fromYarn(ApplicationId.newInstance(1574738619799L, 31971));
      println(his.getJob(TypeConverter.toYarn(JobID)).getAllCounters.mkString(","))


      //        his.getAllJobs(ApplicationId.newInstance(1574738619799L, 9999)).foreach { x =>
      //          println("11111----------------")
      //          println(x._1.toString)
      //          println("22222----------------")
      //          x._2.getAllCounters.foreach(println)
      //          println("33333----------------")
      //        }
      //        jobClient.getAllJobs().foreach{ x =>
      //          println("========"+ x.getJobID+"====="+x.getUsername)
      //        }


    } catch {
      case e: IOException => e.printStackTrace()
    }

  }


}
