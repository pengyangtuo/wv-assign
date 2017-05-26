package repositories.reportRecord

import java.sql.Timestamp

import models.ReportRecord
import slick.driver.MySQLDriver.api._
/**
  * Created by yangtuopeng on 2017-03-25.
  */
class ReportRecordTable(tableName: String, tag: Tag) extends Table[ReportRecord](tag, tableName) {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def reportId = column[Int]("report_id")
  def employeeId = column[Int]("employee_id")
  def hours = column[Double]("hours")
  def rate = column[Double]("rate")
  def date = column[Timestamp]("date")

  override def * =
    (id, reportId, employeeId, hours, rate, date) <> ((ReportRecord.apply _).tupled, ReportRecord.unapply)
}

object ReportRecordTable{
  def apply(tableName: String)(tag: Tag) = new ReportRecordTable(tableName, tag)
}