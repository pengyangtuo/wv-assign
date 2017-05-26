package dtos

import dtos.ErrorCode.ErrorCode
import play.api.libs.json.Json

/**
  * Created by ypeng on 2017-03-25.
  */
object ErrorCode extends Enumeration {
  type ErrorCode = Value

  val BadInput = Value("BadInput")
  val ServiceNotAvailable = Value("ServiceNotAvailable")
  val EmployeeAlreadyExist = Value("EmployeeAlreadyExist")
  val EmployeeDoesNotExist = Value("EmployeeDoesNotExist")
  val RepositoryError = Value("RepositoryError")
}

case class ErrorResponse(errorCode: ErrorCode, errorMsg: Seq[String])

object ErrorResponse {
  implicit val jsonWrites = Json.writes[ErrorResponse]

  implicit class ErrorResponseOps(errorResponse: ErrorResponse) {
    def toJson = Json.toJson(errorResponse)(jsonWrites)
  }
}