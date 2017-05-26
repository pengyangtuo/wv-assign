package repositories.employee

import java.sql.Timestamp
import java.util.UUID

import models.Employee
import slick.driver.MySQLDriver.api._

/**
  * Created by yangtuopeng on 2017-03-25.
  */
class EmployeeTable(tableName: String, tag: Tag) extends Table[Employee](tag, tableName) {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def group = column[Int]("pay_group_id")
  def createdTime = column[Timestamp]("created_time")
  def lastModifiedTime = column[Timestamp]("last_modified_time")

  override def * =
    (id, firstName, lastName, group, createdTime, lastModifiedTime) <> ((Employee.apply _).tupled, Employee.unapply)
}

object EmployeeTable{
  def apply(tableName: String)(tag: Tag) = new EmployeeTable(tableName, tag)
}