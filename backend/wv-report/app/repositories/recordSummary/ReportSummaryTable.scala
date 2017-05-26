package repositories.recordSummary

import java.sql.Timestamp

import models.ReportSummary
import slick.driver.MySQLDriver.api._

/**
  * Created by ypeng on 2017-03-27.
  */
class ReportSummaryTable(tableName: String, tag: Tag) extends Table[ReportSummary](tag, tableName) {

  def employeeId = column[Int]("employee_id")
  def payPeriodStart = column[Timestamp]("pay_period_start")
  def payPeriodEnd = column[Timestamp]("pay_period_end")
  def amount = column[Double]("amount_paid")
  def createdTime = column[Timestamp]("created_time")
  def lastModifiedTime = column[Timestamp]("last_modified_time")

  def pk = primaryKey("pk", (employeeId, payPeriodStart, payPeriodEnd))

  override def * =
    (employeeId, payPeriodStart, payPeriodEnd, amount, createdTime, lastModifiedTime) <> ((ReportSummary.apply _).tupled, ReportSummary.unapply)
}

object ReportSummaryTable{
  def apply(tableName: String)(tag: Tag) = new ReportSummaryTable(tableName, tag)
}
