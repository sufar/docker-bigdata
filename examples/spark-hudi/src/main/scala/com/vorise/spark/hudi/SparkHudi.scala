package com.vorise.spark.hudi

import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{DatasetUtil, SparkSession}


object SparkHudi extends Logging {

  def main(args: Array[String]): Unit = {
    log.info("SparkHudi starting...")
    // System.setProperty("HADOOP_USER_NAME", "hive")

    val sparkConf = new SparkConf()
      .setMaster("spark://localhost:7077")
      .setAppName("SparkHudi读取表数据")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    sparkConf.set("spark.sql.extensions", "org.apache.spark.sql.hudi.HoodieSparkSessionExtension")
    sparkConf.set("spark.sql.shuffle.partitions", "2")
    sparkConf.set("hive.exec.dynamici.partition", "true")
    sparkConf.set("hive.exec.dynamic.partition.mode", "nonstrict")
    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    sparkConf.set("spark.executor.cores", "2")
    sparkConf.set("spark.executor.memory", "4g")

    sparkConf
      .set("hive.metastore.uris", "thrift://localhost:9083")
      // .set("spark.sql.warehouse.dir", "hdfs://localhost:9000/user/hive/warehouse")
      // .set("spark.driver.port", "20002")
      // .set("spark.blockManager.port", "6066")
      // .set("spark.driver.bindAddress", "0.0.0.0")
      // .set("spark.driver.blockManager.port", "20003")
      // .set("spark.driver.host", "172.17.42.201")

    sparkConf.getAll.foreach(println)

    val spark = SparkSession
      .builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()

    spark.sql("show databases").show()

    val tableName = "test3"

    val createSql = s"""
      | create table $tableName (id int, name string)
      |""".stripMargin
    println(createSql)
    spark.sql(createSql)

    val insertSql = s"""
    | insert into $tableName select 1 id, 'zugle' name
    """.stripMargin
    println(insertSql)
    spark.sql(insertSql)

    val selectSql = s"""
    | select * from $tableName limit 10
    """.stripMargin
    println(selectSql)
    spark.sql(selectSql)

    spark.stop()
    return
    val timestamp_sql =
      """
        |create table datax_timestamp_cow (
        |  id int,
        |  name string,
        |  create_date timestamp
        |) using hudi
        |tblproperties (
        |  type = 'cow',
        |  primaryKey = 'id'
        |)
        |""".stripMargin

    spark.sql(timestamp_sql)

    spark.stop()

    return

    val hiveTableName = "bb.my_hoodie58"
    log.info("查询表10条数据: {}", hiveTableName)
    try {
      //    var hiveTableName = "default.mytest_bb"
      spark.catalog.clearCache()
      spark.catalog.refreshTable(hiveTableName)
      val hiveSql =
        s"""
           |select *
           |from ${hiveTableName} limit 10
           |""".stripMargin
      val hiveDf = spark.sql(hiveSql)
      DatasetUtil.show(hiveDf)

//      update2(spark, tableName = hiveTableName)

      update(spark, tableName = hiveTableName)
    } finally {
      spark.stop()
    }

  }

  def update(spark: SparkSession, tableName: String): Unit = {
    val sql =
      s"""
        | update ${tableName} set rider = 'haha223' where uuid = 'a018cdc2-2f49-4e71-86ef-29c0ac9822d1'
        |""".stripMargin
    spark.sql(sql)
  }

  def update2(spark: SparkSession, tableName: String): Unit = {
    val sql =
      s"""
         | update ${tableName} set name = 'haha22' where id = 1
         |""".stripMargin
    spark.sql(sql)
  }
}

