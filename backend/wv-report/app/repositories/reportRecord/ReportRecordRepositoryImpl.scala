package repositories.reportRecord

import com.google.inject.Inject
import configuration.Settings
import models.{Report, ReportRecord}
import play.api.db.slick.DatabaseConfigProvider
import play.api.{Configuration, Logger}
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import slick.driver.MySQLDriver.api._
import wv.service.repository.{ActionNotAllowed, DatabaseError}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yangtuopeng on 2017-03-27.
  */
class ReportRecordRepositoryImpl @Inject()(dbConfigProvider: DatabaseConfigProvider, config: Configuration) extends ReportRecordRepository {

  val settings = Settings(config)
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val reportRecordDb = TableQuery(ReportRecordTable(settings.db.reportRecordTable))

  override def createBatch(records: List[ReportRecord]): Future[Either[DatabaseError, Int]] = {
    val queryResult = dbConfig.db
      .run(reportRecordDb ++= records)
      .map(n => Right(n.getOrElse(0)))

    recoverFromDatabaseError(queryResult)
  }

  override def create(reportRecord: ReportRecord) = {
    val queryResult = dbConfig.db
      .run(reportRecordDb += reportRecord)
      .map(_ => Right(reportRecord))

    recoverFromDatabaseError(queryResult)
  }

  override def find(id: Int): Future[Either[DatabaseError, Option[ReportRecord]]] = {
    val queryResult = dbConfig.db
      .run(reportRecordDb.filter(_.id === id).result.headOption)
      .map(reportRecordOpt => Right(reportRecordOpt))

    recoverFromDatabaseError(queryResult)
  }

  /* Not supported yet */
  override def update(reportRecord: ReportRecord): Future[Either[DatabaseError, ReportRecord]] =
    Future.successful(Left(ActionNotAllowed))

  override def list: Future[Either[DatabaseError, List[ReportRecord]]] = {
    val queryResult = dbConfig.db
      .run(reportRecordDb.result)
      .map(reportRecords => Right(reportRecords.toList))

    recoverFromDatabaseError(queryResult)
  }
}
