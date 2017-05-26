package controllers

import java.io.File
import java.nio.file.attribute.PosixFilePermission._
import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, Paths}
import java.util.{Calendar, UUID}

import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.ByteString
import play.api._
import play.api.mvc._
import play.api.Logger
import play.api.libs.streams.Accumulator
import play.api.mvc.MultipartFormData.FilePart
import play.core.parsers.Multipart.FileInfo
import javax.inject._

import actors.ReportServiceAdmin
import akka.actor._
import akka.stream.IOResult
import akka.stream.scaladsl.FileIO
import configuration.Settings
import dto.CreateReport
import services.report.ReportService
import services.reportRecord.ReportRecordService
import services.reportSummary.ReportSummaryService
import wv.service.services.DuplicateCreation

import scala.concurrent.Future

class ReportController @Inject()(
                                  reportService: ReportService,
                                  reportRecordService: ReportRecordService,
                                  reportSummaryService: ReportSummaryService,
                                  system: ActorSystem, config: Configuration
                                )
  extends Controller {

  val log = Logger(this.getClass)
  val settings = Settings(config).app
  val uploadPath = settings.uploadPath

  val reportAdmin = system.actorOf(ReportServiceAdmin.props(reportRecordService, reportSummaryService), "report-admin")

  def upload = Action.async(parse.multipartFormData(handleFilePartAsFile)) { implicit request =>
    request.body.file("report").map {
      case FilePart(key, filename, contentType, file) =>
        if (contentType.getOrElse("") != "text/csv") {
          Future.successful(UnsupportedMediaType("contentType should only be \"text/csv\""))
        } else {
          reportService
            .create(CreateReport(file).toModel)
            .map{
              case Left(DuplicateCreation) => Conflict
              case Left(_) => ServiceUnavailable
              case Right(report) => Created(report.toDto.toJson)
            }
        }
    }.getOrElse(Future.successful(BadRequest("field \"report\" is required")))
  }

  def getAll = Action.async { _ =>
    getReports
  }

  def get(id: Int) = Action.async { _ =>
    getReport(id)
  }

  def getReport(id: Int) = {
    reportService
      .find(id)
      .map{
        case Right(Some(report)) =>
          Ok(report.toDto.toJson)
        case Right(None) =>
          NotFound
        case Left(_) =>
          ServiceUnavailable
      }
  }

  def getReports = {
    reportService
      .list
      .map{
        case Right(reports) =>
          Ok(reports.map(_.toDto).toJson)
        case Left(_) =>
          ServiceUnavailable
      }
  }

  type FilePartHandler[A] = FileInfo => Accumulator[ByteString, FilePart[A]]

  def handleFilePartAsFile: FilePartHandler[File] = {
    case FileInfo(partName, filename, contentType) =>
      val now = Calendar.getInstance()
      val fileDir = s"${uploadPath}/${now.get(Calendar.YEAR)}/${now.get(Calendar.MONTH)}/${now.get(Calendar.DATE)}"
      val fileUUID = UUID.randomUUID()

      val perms = java.util.EnumSet.of(OWNER_READ, OWNER_WRITE)
      val attr = PosixFilePermissions.asFileAttribute(perms)

      Files.createDirectories(Paths.get(fileDir))
      val path = Files.createFile(Paths.get(s"$fileDir/$fileUUID.csv"), attr).toAbsolutePath

      val file = path.toFile
      val fileSink = FileIO.toPath(file.toPath)
      val accumulator = Accumulator(fileSink)
      accumulator.map { case IOResult(count, status) =>
        FilePart(partName, filename, contentType, file)
      }(play.api.libs.concurrent.Execution.defaultContext)
  }
}
