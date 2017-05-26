package repositories.report

import java.sql.Timestamp

import models.Report
import slick.driver.MySQLDriver.api._
/**
  * Created by yangtuopeng on 2017-03-25.
  */
class ReportTable(tableName: String, tag: Tag) extends Table[Report](tag, tableName) {

  def id = column[Int]("id", O.PrimaryKey)
  def location = column[String]("location")
  def uploadedTime = column[Timestamp]("uploaded_time")
  def desc = column[Option[String]]("desc")

  override def * =
    (id, location, uploadedTime, desc) <> ((Report.apply _).tupled, Report.unapply)
}

object ReportTable{
  def apply(tableName: String)(tag: Tag) = new ReportTable(tableName, tag)
}