package com.lb.scala.base.csv

import java.io.FileOutputStream

import org.apache.poi.hssf.usermodel.{HSSFSheet, HSSFWorkbook}

/**
  * @ClassName ExcelUtil
  * @Description @TODO
  * @Author liubing
  * @Date 2019/9/5 14:41
  * @Version 1.0
  **/
object ExcelUtil extends  App{

  create_excel()

  def create_excel(): Unit = {
    //文件路径
    val filePath = s"//Users//liubing//Desktop//123.xls"
    //创建Excel文件(Workbook)
    val workbook = new HSSFWorkbook
    //创建工作表(Sheet)
    var sheet = workbook.createSheet

    //创建工作表(Sheet)
     sheet = workbook.createSheet("Test")



    for(i <- 0 to 10){
      setCell(sheet, i)
    }

    val out = new FileOutputStream(filePath)



    //保存Excel文件
    workbook.write(out)
    //关闭文件流
    out.close()
  }


  def  setCell(sheet: HSSFSheet, i: Int): Unit ={
    println(i)
   // val sheet = workbook.createSheet("Test");// 创建工作表(Sheet)
    val row = sheet.createRow(i);// 创建行,从0开始
    val cell = row.createCell(0);// 创建行的单元格,也是从0开始
    cell.setCellValue("李志伟");// 设置单元格内容
    row.createCell(1).setCellValue(false);// 设置单元格内容,重载
    row.createCell(2).setCellValue("2019-09-05 13:34:41");// 设置单元格内容,重载
    row.createCell(3).setCellValue(12.345);// 设置单元格内容,重载
  }

  def getData(){


  }
}
