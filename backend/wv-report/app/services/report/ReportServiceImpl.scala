package services.report

import java.io.File
import java.nio.file.Paths
import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

import actors.ReportRecordGenerated
import akka.actor.ActorSystem

import scala.concurrent.Future
import scala.io.Source
import configuration.Settings
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.{Configuration, Logger}
import models.{Report, ReportRecord}
import repositories.report.ReportRepository
import services.reportRecord.ReportRecordService
import services.reportSummary.ReportSummaryService
import wv.service.repository.{ActionNotAllowed, DatabaseError}
import wv.service.services._

/**
  * Created by yangtuopeng on 2017-03-26.
  */
class ReportServiceImpl @Inject()(
                                   system: ActorSystem,
                                   reportSummaryService: ReportSummaryService,
                                   reportRecordService: ReportRecordService,
                                   reportRepo: ReportRepository,
                                   ws: WSClient,
                                   config: Configuration
                                 )
  extends ReportService {

  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext
  val settings = Settings(config)
  val uploadPath = settings.app.uploadPath
  val paygroupEndpoint = settings.ws.paygroup
  val log = Logger(this.getClass)

  def parseDBError[T](databaseResult: Either[DatabaseError, T]) =
    databaseResult.left.map {
      case ActionNotAllowed => MethodNotSupported
      case _ => RepositoryError
    }

  override def create(report: Report): Future[Either[ServiceError, Report]] = {
    reportRepo
      .find(report.id)
      .flatMap {
        case Right(Some(_)) =>
          Future.successful(Left(DuplicateCreation))
        case Right(None) =>
          val reportFile = Paths.get(report.location)
          handleFile(reportFile.toFile).flatMap {
            case Left(_) => Future.successful(Left(UnableToHandleFile))
            case Right(records) =>
              reportRepo
                .create(report.copy(location=report.location.split(uploadPath).apply(1))) // remove uploadPath prefix
                .map(parseDBError)
                .map(_.right.map(right => {
                  system.eventStream.publish(ReportRecordGenerated(records))
                  right
                }))
          }
        case Left(_) =>
          Future.successful(Left(RepositoryError))
      }
  }

  override def find(id: Int): Future[Either[ServiceError, Option[Report]]] =
    reportRepo
      .find(id)
      .map(parseDBError)

  override def list: Future[Either[ServiceError, List[Report]]] =
    reportRepo
      .list
      .map(parseDBError)

  override def update(report: Report) =
    reportRepo
      .update(report)
      .map(parseDBError)

  override def handleFile(file: File): Future[Either[FileServiceError, List[ReportRecord]]] = {
    // TODO: data validation: empty fields, ids not Int, group does not exist
    getPayGroupRate.map {
      case payGroup if payGroup.isEmpty =>
        Left(MissParsingData)
      case payGroup =>
        log.info("Received pay group map " + payGroup.toString)

        val bufferedSource = Source.fromFile(file.getPath)
        val recordIterator = bufferedSource.getLines
        recordIterator.toList match {
          case header :: (body :+ footer) =>
            val reportId = footer.split(",").map(_.trim).apply(1)
            val timestampFormat = new SimpleDateFormat("d/MM/yyy")
            val records = body.map(line => {
              val Array(date, hours, employeeId, group) = line.split(",").map(_.trim)

              val dateTimestamp = new Timestamp(timestampFormat.parse(date).getTime)
              ReportRecord(0, reportId.toInt, employeeId.toInt, hours.toDouble, payGroup(group.toUpperCase), dateTimestamp)
            })
            Right(records)
          case _ =>
            Left(CorruptedFile)
        }
    }
  }

  def getPayGroupRate: Future[Map[String, Double]] = {
    // TODO: failure handling
    val request: WSRequest = ws.url(paygroupEndpoint)
    val response: Future[WSResponse] = request.get()
    response.map(response => {
      val payGroupNames = (response.json \\ "name").map(_.as[String])
      val payGroupRates = (response.json \\ "rate").map(_.as[Double])
      payGroupNames zip payGroupRates toMap
    }).recover {
      case ex: Exception =>
        log.error("Unable to get pay group data: " + ex.getMessage)
        Map.empty[String, Double]
    }
  }
}
