package controllers

import javax.inject._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api._
import play.api.mvc._
import play.api.Logger
import play.api.data.validation.ValidationError
import play.api.libs.json.JsPath
import dtos._
import models.Employee
import models.Employee._
import services.employee.EmployeeService
import wv.service.services.RepositoryError

class EmployeeController @Inject()(employeeService: EmployeeService, config: Configuration) extends Controller {

  val log = Logger(this.getClass)

  def create = Action.async(parse.json) { request =>
    request.body
      .validate[CreateEmployee]
      .asEither
      .fold(handleValidationError, createEmployee)
  }

  def update = Action.async(parse.json) { request =>
    request.body
      .validate[UpdateEmployee]
      .asEither
      .fold(handleValidationError, updateEmployee)
  }

  def getAll = Action.async { _ =>
    getEmployees
  }

  def get(id: Int) = Action.async { _ =>
    getEmployee(id)
  }

  def createEmployee(employee: CreateEmployee) = {
    // TODO: json validation type error message does not show field name
    employeeService
      .create(employee.toModel)
      .map{
        case Right(employee) =>
          Created(employee.toDto.toJson)
        case Left(RepositoryError) =>
          BadRequest(ErrorResponse(ErrorCode.RepositoryError, List.empty).toJson)
        case Left(_) =>
          ServiceUnavailable(ErrorResponse(ErrorCode.ServiceNotAvailable, List.empty).toJson)
      }
  }

  def updateEmployee(employee: UpdateEmployee) = {
    // TODO: try for comprehension
    employeeService
        .find(employee.id)
        .flatMap{
          case Right(None) => Future.successful(NotFound)
          case Right(Some(oldEmployee)) =>
            val newEmployee = employee.toModel(oldEmployee)
            if(Employee.isSame(newEmployee, oldEmployee)){
              Future.successful(NotModified)
            }else{
              employeeService
                .update(newEmployee)
                .map{
                  case Right(employee) =>
                    Ok(employee.toDto.toJson)
                  case Left(_) =>
                    ServiceUnavailable(ErrorResponse(ErrorCode.ServiceNotAvailable, List.empty).toJson)
                }
            }
          case Left(_) =>
            Future.successful(ServiceUnavailable(ErrorResponse(ErrorCode.ServiceNotAvailable, List.empty).toJson))
        }
  }

  def getEmployees = {
    employeeService
      .list
      .map{
        case Right(employees) =>
          Ok(employees.map(_.toDto).toJson)
        case Left(_) =>
          ServiceUnavailable(ErrorResponse(ErrorCode.ServiceNotAvailable, List.empty).toJson)
      }
  }

  def getEmployee(id: Int) = {
    employeeService
      .find(id)
      .map{
        case Right(Some(employee)) =>
          Ok(employee.toDto.toJson)
        case Right(None) =>
          NotFound
        case Left(_) =>
          ServiceUnavailable(ErrorResponse(ErrorCode.ServiceNotAvailable, List.empty).toJson)
      }
  }

  def handleValidationError(error: Seq[(JsPath, Seq[ValidationError])]) = {
    val msg: Seq[String] = error.map {
      case (jsPath, validationErrors) =>
        validationErrors.map(_.message).mkString(
          start = jsPath.path.mkString(start = "", sep = "/", end = ""),
          sep = ",",
          end = ""
        )
    }

    Future.successful(BadRequest(ErrorResponse(ErrorCode.BadInput, msg).toJson))
  }
}
