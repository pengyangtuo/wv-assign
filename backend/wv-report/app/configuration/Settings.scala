package configuration

import play.api.Configuration

/**
  * Created by ypeng on 2017-03-23.
  */
case class Settings(config: Configuration) {
  object app {
    val uploadPath = config.getString("uploadPath").getOrElse("./temp")
  }

  object db {
    val reportTable = config.getString("db.reportTable").getOrElse("")
    val reportRecordTable = config.getString("db.reportRecordTable").getOrElse("")
    val reportSummaryTable = config.getString("db.reportSummaryTable").getOrElse("")
  }

  object ws {
    val paygroup =
      config.getString("webservice.endpoints.paygroup").getOrElse("http://localhost:9000/paygroup")
  }
}
