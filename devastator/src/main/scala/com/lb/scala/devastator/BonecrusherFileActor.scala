package com.lb.scala.devastator

import java.text.SimpleDateFormat
import java.util.TimeZone

import akka.actor.{Actor, ActorLogging}
import org.apache.hadoop.fs.FileStatus

/**
  * @ClassName BonecrusherFileActor
  * @Description @TODO
  * @Author liubing
  * @Date 2020/12/22 16:49
  * @Version 1.0
  **/
class BonecrusherFileActor extends Actor with ActorLogging {

  // 文件类型(后缀名)
  val file_type_seq = Seq(".snappy.parquet", ".parquet", ".txt", ".jar", ".jpg", ".png", ".tif", ".zip")
  // 需过滤的文件名
  val file_filter_seq = Seq("_SUCCESS")

  val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  sdf.setTimeZone(TimeZone.getTimeZone("UTC"))


  override def receive: Receive = {
    case f: FileBean => f.fileSystem.getFileStatus(f.file.getPath)
      // 文件路径：
      f.file.getPath
      // 文件名称：
      f.file.getPath.getName
      //文件父目录：
      f.file.getPath.getParent
      //文件拥有者：
      f.file.getOwner
      //文件所属组：
      f.file.getGroup
      //文件块的大小：
      f.file.getBlockSize
      //文件长度：
      f.file.getLen
      //文件权限：
      f.file.getPermission
      //文件副本个数：
      f.file.getReplication

      // 读时间
      f.file.getAccessTime
      // 修改时间
      f.file.getModificationTime


      // log.info(s"""${f.file.getPath}-${f.file.getAccessTime}-${f.file.getReplication}-${f.file.getPermission}-${f.file.getLen}-${f.file.getBlockSize}""")
      if (!file_filter_seq.exists(x => x.equals(f.file.getPath.getName)))
        saveDateFileBo(f.file)

      Engine.refNum.decrement()
  }


  private def saveDateFileBo(file: FileStatus): Option[Int] = {

    var fileType = ""
    var flag = true
    for (x <- file_type_seq if flag) {
      if (file.getPath.getName.endsWith(x)) {
        fileType = x
        flag = false
      }
    }


    val sql =
      s"""
         | insert into hdfs_system_file (`name`, `filetype`, `path`, `owner`, `group`,
         |  `Permission`, `BlockSize`, `len`, `Replication`, `accessTime`,
         |  `modificationTime`, `session`,create_time )  values
         | ('${file.getPath.getName}', '${fileType}', '${file.getPath.getParent.toString}', '${file.getOwner}', '${file.getGroup}',
         |  '${file.getPermission}', ${file.getBlockSize}, ${file.getLen}, ${file.getReplication}, '${sdf.format(file.getAccessTime)}',
         |  '${sdf.format(file.getModificationTime)}', '${Engine.session}', now() )
         |
       """.stripMargin


    Engine.dao.saveData(sql)
  }
}