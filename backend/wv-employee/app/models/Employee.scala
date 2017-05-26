package models

import java.util.UUID

import dtos.ResponseEmployee
import java.sql.Timestamp

/**
  * Created by ypeng on 2017-03-24.
  */
case class Employee(
                     id: Int,
                     firstName: String,
                     lastName: String,
                     group: Int,
                     createdTime: Timestamp,
                     lastModifiedTime: Timestamp
                   )

object Employee {

  implicit class EmployeeOps(employee: Employee) {
    def toDto: ResponseEmployee =
      ResponseEmployee(employee.id, employee.firstName, employee.lastName, employee.group, employee.createdTime, employee.lastModifiedTime)
  }

  def isSame(e1: Employee, e2: Employee) =
    e1.id == e2.id &&
      e1.firstName == e2.firstName &&
      e1.lastName == e2.lastName &&
      e1.group == e2.group
}
