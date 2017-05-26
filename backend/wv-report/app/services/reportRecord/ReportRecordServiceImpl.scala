package services.reportRecord

import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import models.{Report, ReportRecord}
import play.api.Logger
import repositories.reportRecord.ReportRecordRepository
import services._
import wv.service.repository.{ActionNotAllowed, DatabaseError}
import wv.service.services.{MethodNotSupported, RepositoryError, ServiceError}

import scala.concurrent.Future

/**
  * Created by yangtuopeng on 2017-03-27.
  */
class ReportRecordServiceImpl @Inject()(reportRecordRepo: ReportRecordRepository)
  extends ReportRecordService {

  val log = Logger(this.getClass)

  def parseDBError[T](databaseResult: Either[DatabaseError, T]) =
    databaseResult.left.map {
      case ActionNotAllowed => MethodNotSupported
      case _ => RepositoryError
    }

  override def create(reportRecord: ReportRecord): Future[Either[ServiceError, ReportRecord]] = {
    reportRecordRepo
      .create(reportRecord)
      .map(parseDBError)
  }

  override def find(id: Int): Future[Either[ServiceError, Option[ReportRecord]]] =
    reportRecordRepo
      .find(id)
      .map(parseDBError)

  override def list: Future[Either[ServiceError, List[ReportRecord]]] =
    reportRecordRepo
      .list
      .map(parseDBError)

  override def update(reportRecord: ReportRecord) =
    reportRecordRepo
      .update(reportRecord)
      .map(parseDBError)

  override def createBatch(records: List[ReportRecord]): Future[Either[ServiceError, Int]] = {
    log.info("creating report record batch")
    records.map(r => log.info(r.toString))

    reportRecordRepo
      .createBatch(records)
      .map(parseDBError)
  }

}
