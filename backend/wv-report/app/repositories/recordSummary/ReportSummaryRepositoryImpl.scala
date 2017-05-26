package repositories.recordSummary

import java.sql.Timestamp
import javax.inject.Inject

import configuration.Settings
import models.ReportSummary
import play.api.db.slick.DatabaseConfigProvider
import play.api.{Configuration, Logger}
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import slick.driver.MySQLDriver.api._
import wv.service.repository.DatabaseError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by ypeng on 2017-03-25.
  */
class ReportSummaryRepositoryImpl @Inject()(dbConfigProvider: DatabaseConfigProvider, config: Configuration)
  extends ReportSummaryRepository {

  val settings = Settings(config)
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val reportSummaryDb = TableQuery(ReportSummaryTable(settings.db.reportSummaryTable))

  override def find(pk: (Int, Timestamp, Timestamp)): Future[Either[DatabaseError, Option[ReportSummary]]] = {
    val queryResult = dbConfig.db
      .run(
        reportSummaryDb
          .filter(_.employeeId === pk._1)
          .withFilter(_.payPeriodStart === pk._2)
          .withFilter(_.payPeriodEnd === pk._3)
          .result.headOption
      )
      .map(reportSummaryOpt => Right(reportSummaryOpt))

    recoverFromDatabaseError(queryResult)
  }

  override def create(reportSummary: ReportSummary) = {
    val queryResult = dbConfig.db
      .run(reportSummaryDb += reportSummary)
      .map(_ => Right(reportSummary))

    recoverFromDatabaseError(queryResult)
  }

  override def update(reportSummary: ReportSummary) = {
    val queryResult = dbConfig.db
      .run(reportSummaryDb.insertOrUpdate(reportSummary))
      .map(_ => Right(reportSummary))

    recoverFromDatabaseError(queryResult)
  }

  override def list: Future[Either[DatabaseError, List[ReportSummary]]] = {
    val queryResult = dbConfig.db
      .run(reportSummaryDb.result)
      .map(summary => Right(summary.toList))

    recoverFromDatabaseError(queryResult)
  }

  /**
    *
    * @param records
    * @return
    */
  override def upsertBatch(records: List[ReportSummary]): Future[Either[DatabaseError, Int]] = {
    val queryResult = dbConfig.db
      .run(DBIO.sequence(records.map{ record =>
        reportSummaryDb.insertOrUpdate(record)
      }))
      .map(n => Right(n.size))

    recoverFromDatabaseError(queryResult)
  }
}
