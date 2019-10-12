package com.lb.scala.base.jdbc


import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, SQLException}

class ImpalaJdbc {



  def test(): Unit = {
    var con: Connection = null
    var rs: ResultSet = null
    var ps : PreparedStatement = null
    val JDBC_DRIVER = "com.cloudera.impala.jdbc41.Driver"
    val CONNECTION_URL = "jdbc:impala://192.168.2.20:21050"
    try {
      Class.forName(JDBC_DRIVER)
      con = DriverManager.getConnection(CONNECTION_URL).asInstanceOf[Connection]

      ps = con.prepareStatement("select count(*) from billdetail;")
      rs = ps.executeQuery
      while ( {
        rs.next
      }) System.out.println(rs.getString(1))
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally try {
      rs.close()
      ps.close
      con.close
    } catch {
      case e: SQLException =>
        e.printStackTrace()
    }
  }

  def main(args: Array[String]): Unit = {
    test()
  }

}
