package repositories.report

import javax.inject.Inject

import configuration.Settings
import models.Report
import play.api.db.slick.DatabaseConfigProvider
import play.api.{Configuration, Logger}
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import slick.driver.MySQLDriver.api._
import wv.service.repository.{ActionNotAllowed, DatabaseError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by ypeng on 2017-03-25.
  */
class ReportRepositoryImpl @Inject()(dbConfigProvider: DatabaseConfigProvider, config: Configuration)
  extends ReportRepository {

  val settings = Settings(config)
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val reportDb = TableQuery(ReportTable(settings.db.reportTable))

  override def create(report: Report) = {
    val queryResult = dbConfig.db
      .run(reportDb += report)
      .map(_ => Right(report))

    recoverFromDatabaseError(queryResult)
  }

  override def find(id: Int): Future[Either[DatabaseError, Option[Report]]] = {
    val queryResult = dbConfig.db
      .run(reportDb.filter(_.id === id).result.headOption)
      .map(reportOpt => Right(reportOpt))

    recoverFromDatabaseError(queryResult)
  }

  /* Not supported yet */
  override def update(report: Report): Future[Either[DatabaseError, Report]] =
    Future.successful(Left(ActionNotAllowed))

  override def list: Future[Either[DatabaseError, List[Report]]] = {
    val queryResult = dbConfig.db
      .run(reportDb.result)
      .map(reports => Right(reports.toList))

    recoverFromDatabaseError(queryResult)
  }
}
