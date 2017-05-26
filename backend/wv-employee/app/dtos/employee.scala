package dtos

import java.sql.Timestamp
import java.util.{Calendar, Date, UUID}

import models.Employee
import play.api.libs.json.{JsError, Json, Reads, Writes}

/**
  * Created by ypeng on 2017-03-24.
  */
case class CreateEmployee(firstName: String, lastName: String, group: Int)
case class UpdateEmployee(id: Int, firstName: Option[String], lastName: Option[String], group: Option[Int])
case class ResponseEmployee(id: Int, firstName: String, lastName: String, group: Int, createdTime: Timestamp, lastModifiedTime: Timestamp)

object CreateEmployee {
  implicit class CreateEmployeeOps(createEmployee: CreateEmployee) {
    def toModel: Employee = {
      val now = new Timestamp(Calendar.getInstance.getTime.getTime)
      /* this id=0 will be ignored by Slick TableQuery since it's set as a AutoInc field in Table definition */
      Employee(0, createEmployee.firstName, createEmployee.lastName, createEmployee.group, now, now)
    }
  }

  implicit val readCreateEmployee: Reads[CreateEmployee] = Reads[CreateEmployee] {
    json => for {
      firstName <- (json \ "firstName").validate[String]
        .filter(JsError("First name must not be empty"))(_.size > 0)
      lastName <- (json \ "lastName").validate[String]
        .filter(JsError("Last name must not be empty"))(_.size > 0)
      group <- (json \ "group").validate[Int]
    } yield CreateEmployee(firstName, lastName, group)
  }
}

object UpdateEmployee {
  implicit class UpdateEmployeeOps(updateEmployee: UpdateEmployee) {
    def toModel(employee: Employee): Employee = {
      val now = new Timestamp(Calendar.getInstance.getTime.getTime)
      Employee(
        updateEmployee.id,
        updateEmployee.firstName.getOrElse(employee.firstName),
        updateEmployee.lastName.getOrElse(employee.lastName),
        updateEmployee.group.getOrElse(employee.group),
        employee.createdTime,
        now
      )
    }
  }

  implicit val readUpdateEmployee: Reads[UpdateEmployee] = Reads[UpdateEmployee] {
    json => for {
      id <- (json \ "id").validate[Int]
      firstName <- (json \ "firstName").validateOpt[String]
        .filter(JsError("First name must not be empty"))(_.size > 0)
      lastName <- (json \ "lastName").validateOpt[String]
        .filter(JsError("Last name must not be empty"))(_.size > 0)
      group <- (json \ "group").validateOpt[Int]
    } yield UpdateEmployee(id, firstName, lastName, group)
  }
}

object ResponseEmployee {
  implicit val writeResponseEmployee: Writes[ResponseEmployee] = Json.writes[ResponseEmployee]

  implicit class ResponseEmployeeOps(responseEmployee: ResponseEmployee) {
    def toJson = Json.toJson(responseEmployee)(writeResponseEmployee)
  }

  implicit class ResponseEmployeeListOps(responseEmployeeList: List[ResponseEmployee]) {
    def toJson = Json.toJson(responseEmployeeList)
  }
}