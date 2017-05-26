package configuration

import play.api.Configuration

/**
  * Created by ypeng on 2017-03-23.
  */
case class Settings(config: Configuration) {
  object db {
    val employeeTable = config.getString("db.employeeTable").getOrElse("")
    val payGroupTable = config.getString("db.payGroupTable").getOrElse("")
  }
}
