package services.reportSummary

import java.io.Serializable
import java.sql.Timestamp
import java.util.Calendar
import javax.inject.Inject

import models.{ReportRecord, ReportSummary}
import play.api.Logger
import repositories.recordSummary.ReportSummaryRepository
import services.common.ServiceHelper
import wv.service.repository.{ActionNotAllowed, DatabaseError}
import wv.service.services.{MethodNotSupported, RepositoryError, ServiceError}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by ypeng on 2017-03-27.
  */
class ReportSummaryServiceImpl @Inject()(reportSummaryRepository: ReportSummaryRepository) extends ReportSummaryService {
  val log = Logger(this.getClass)

  def parseDBError[T](databaseResult: Either[DatabaseError, T]) =
    databaseResult.left.map {
      case ActionNotAllowed => MethodNotSupported
      case _ => RepositoryError
    }

  def mergeSingleEmployeeRecords(employeeId: Int, records: List[ReportRecord]): List[ReportSummary] = {

    val mergeRecords = records.foldLeft(Map.empty[(Timestamp, Timestamp), Double])((result, newRecord) => {
      val payPeriod = ServiceHelper.getPayPeriod(newRecord.date)
      val oldAmount: Double = result.get(payPeriod).getOrElse(0)
      val newAmount: Double = oldAmount + (newRecord.hours * newRecord.rate).toDouble
      result + (payPeriod -> newAmount)
    })

    val now = new Timestamp(Calendar.getInstance.getTime.getTime)
    mergeRecords.toList.map { recordTuple =>
      val (start, end) = recordTuple._1
      val amount = recordTuple._2
      ReportSummary(employeeId, start, end, amount, now, now)
    }
  }

  override def generateSummaryFromRecords(records: List[ReportRecord]): List[ReportSummary] =
    records
      .groupBy(_.employeeId)
      .toList
      .map { (singleEmployeeRecords: (Int, List[ReportRecord])) =>
        mergeSingleEmployeeRecords(singleEmployeeRecords._1, singleEmployeeRecords._2)
      }.flatten

  /**
    * Insert or merge the input summary entry list to database
    *
    * @param summaries
    * @return
    */
  override def upsertBatchSummary(summaries: List[ReportSummary]): Future[Either[ServiceError, Int]] = {
    log.info("creating summary batch")
    summaries.map(s => log.info(s.toString))

    Future.traverse(summaries)(
      summary =>
        find((summary.employeeId, summary.payPeriodStart, summary.payPeriodEnd))
          .map {
            case Right(None) => Right(summary)
            case Right(Some(prevSum)) =>
              Right(summary.copy(amount = prevSum.amount + summary.amount, createdTime = prevSum.createdTime))
            case Left(_) => Left(RepositoryError)
          }
    )
      .map(ServiceHelper.reduceListOfEither)
      .flatMap {
        case Left(e) => Future.successful(Left(e))
        case Right(summaryList) =>
          reportSummaryRepository
            .upsertBatch(summaryList)
            .map(parseDBError)
      }
  }

  override def find(pk: (Int, Timestamp, Timestamp)): Future[Either[ServiceError, Option[ReportSummary]]] =
    reportSummaryRepository
      .find(pk)
      .map(parseDBError)

  override def create(model: ReportSummary): Future[Either[ServiceError, ReportSummary]] =
    Future.successful(Left(MethodNotSupported))

  override def update(model: ReportSummary): Future[Either[ServiceError, ReportSummary]] =
    Future.successful(Left(MethodNotSupported))

  override def list: Future[Either[ServiceError, List[ReportSummary]]] =
    reportSummaryRepository
      .list
      .map(parseDBError)
}
