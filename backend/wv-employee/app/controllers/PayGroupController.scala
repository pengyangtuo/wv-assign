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
import models.PayGroup
import models.PayGroup._
import services.payGroup.PayGroupService

class PayGroupController @Inject()(payGroupService: PayGroupService, config: Configuration) extends Controller {

  val log = Logger(this.getClass)

  def create = Action.async(parse.json) { request =>
    request.body
      .validate[CreatePayGroup]
      .asEither
      .fold(handleValidationError, createPayGroup)
  }

  def update = Action.async(parse.json) { request =>
    request.body
      .validate[UpdatePayGroup]
      .asEither
      .fold(handleValidationError, updatePayGroup)
  }

  def getAll = Action.async { _ =>
    getPayGroups
  }

  def get(id: Int) = Action.async { _ =>
    getPayGroup(id)
  }

  def createPayGroup(payGroup: CreatePayGroup) = {
    // TODO: json validation type error message does not show field name
    payGroupService
      .create(payGroup.toModel)
      .map{
        case Right(payGroup) =>
          Created(payGroup.toDto.toJson)
        case Left(_) =>
          ServiceUnavailable(ErrorResponse(ErrorCode.ServiceNotAvailable, List.empty).toJson)
      }
  }

  def updatePayGroup(payGroup: UpdatePayGroup) = {
    // TODO: try for comprehension
    payGroupService
      .find(payGroup.id)
      .flatMap{
        case Right(None) => Future.successful(NotFound)
        case Right(Some(oldPayGroup)) =>
          val newPayGroup = payGroup.toModel(oldPayGroup)
          if(PayGroup.isSame(newPayGroup, oldPayGroup)){
            Future.successful(NotModified)
          }else{
            payGroupService
              .update(newPayGroup)
              .map{
                case Right(payGroup) =>
                  Ok(payGroup.toDto.toJson)
                case Left(_) =>
                  ServiceUnavailable(ErrorResponse(ErrorCode.ServiceNotAvailable, List.empty).toJson)
              }
          }
        case Left(_) =>
          Future.successful(ServiceUnavailable(ErrorResponse(ErrorCode.ServiceNotAvailable, List.empty).toJson))
      }
  }

  def getPayGroups = {
    payGroupService
      .list
      .map{
        case Right(payGroup) =>
          Ok(payGroup.map(_.toDto).toJson)
        case Left(_) =>
          ServiceUnavailable(ErrorResponse(ErrorCode.ServiceNotAvailable, List.empty).toJson)
      }
  }

  def getPayGroup(id: Int) = {
    payGroupService
      .find(id)
      .map{
        case Right(Some(payGroup)) =>
          Ok(payGroup.toDto.toJson)
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
