package org.apache.spark.sql

import org.apache.spark.internal.Logging

object DatasetUtil extends Logging {

  def show(df: DataFrame): Unit = {
    log.info(df.showString(20, 20))
  }

}
