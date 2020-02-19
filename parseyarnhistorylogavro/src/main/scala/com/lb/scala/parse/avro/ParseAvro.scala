package com.lb.scala.parse.avro

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.mapreduce.jobhistory.{Event, JobHistoryParser}

import scala.collection.JavaConversions._

import scala.reflect.io.File

/**
  * @ClassName ParseAvro
  * @Description @TODO
  * @Author liubing
  * @Date 2019/11/27 11:09
  * @Version 1.0
  **/
object ParseAvro extends App {
// 使用原生avro 解析
  //val  reader: DatumReader[Event] = new SpecificDatumReader[Event](Event.SCHEMA$)
  println(Event.SCHEMA$)
  // val  reader: DatumReader[Event] = new SpecificDatumReader[Event](classOf[Event])
  //val  file = new File("/Users/liubing/job_1574558942012_88996-1574637473440.jhist")
  // println(file.exists())
  // val dataFileReader: DataFileReader[Event]  =  new DataFileReader[Event](file, reader)


  // 从resource读取对应配置文件
  val conf: Configuration = new Configuration();
  val fs: FileSystem = FileSystem.get(conf)

  // history log 文件地址
   val path: Path = new Path("/user/root/hdfs/job_1574558942012_88996-1574637473440.jhist")

  // 解析avro文件, 使用原生avro类解析有问题, 如上注释代码
  val parser: JobHistoryParser = new JobHistoryParser(fs, path)
  val jobInfo = parser.parse()

  // job 执行情况
  println(jobInfo.getJobStatus)

  // job task信息
  jobInfo.getAllTasks.foreach { x =>
    println(x._1)  // taskID
    println(x._2)  // task 信息, 还可以扩展
  }


  // job content信息
  jobInfo.getTotalCounters.foreach { x =>
    x.foreach { xx =>
      println(s"name: ${xx.getName}, value: ${xx.getValue}, displayname:  ${xx.getDisplayName}")
    }
  }


  //  while(dataFileReader.hasNext){
  //    println("Schema: "+dataFileReader.next().getSchema)
  //    //println("Schema: "+dataFileReader.next().getSchema)
  //    // dataFileReader.next().getSchema
  //  }


}
