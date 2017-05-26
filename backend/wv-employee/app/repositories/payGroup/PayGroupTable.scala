package repositories.payGroup

import java.sql.Timestamp

import models.PayGroup
import slick.driver.MySQLDriver.api._

/**
  * Created by yangtuopeng on 2017-03-25.
  */
class PayGroupTable(tableName: String, tag: Tag) extends Table[PayGroup](tag, tableName) {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def rate = column[Double]("rate")
  def createdTime = column[Timestamp]("created_time")
  def lastModifiedTime = column[Timestamp]("last_modified_time")

  override def * =
    (id, name, rate, createdTime, lastModifiedTime) <> ((PayGroup.apply _).tupled, PayGroup.unapply)
}

object PayGroupTable{
  def apply(tableName: String)(tag: Tag) = new PayGroupTable(tableName, tag)
}