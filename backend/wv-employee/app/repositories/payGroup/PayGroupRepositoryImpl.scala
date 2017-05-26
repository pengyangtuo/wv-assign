package repositories.payGroup

import javax.inject.Inject

import configuration.Settings
import models.PayGroup
import play.api.db.slick.DatabaseConfigProvider
import play.api.{Configuration, Logger}
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery
import wv.service.repository.{DatabaseError, UnhandledDatabaseError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by ypeng on 2017-03-25.
  */
class PayGroupRepositoryImpl @Inject()(dbConfigProvider: DatabaseConfigProvider, config: Configuration)
  extends PayGroupRepository {

  val log = Logger(this.getClass)
  val settings = Settings(config)
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val payGroupDb = TableQuery(PayGroupTable(settings.db.payGroupTable))

  override def create(payGroup: PayGroup) = {
    val queryResult = dbConfig.db
      .run((payGroupDb returning payGroupDb.map(_.id)) += payGroup)
      .map(newId => Right(payGroup.copy(id=newId)))

    recoverFromDatabaseError(queryResult)
  }

  override def find(id: Int): Future[Either[DatabaseError, Option[PayGroup]]] = {
    val queryResult = dbConfig.db
      .run(payGroupDb.filter(_.id === id).result.headOption)
      .map(groupOpt => Right(groupOpt))

    recoverFromDatabaseError(queryResult)
  }

  override def update(payGroup: PayGroup): Future[Either[DatabaseError, PayGroup]] = {
    val queryResult = dbConfig.db
      .run(payGroupDb.insertOrUpdate(payGroup))
      .map(_ => Right(payGroup))

    recoverFromDatabaseError(queryResult)
  }

  override def list: Future[Either[DatabaseError, List[PayGroup]]] = {
    val queryResult = dbConfig.db
      .run(payGroupDb.result)
      .map(payGroups => Right(payGroups.toList))

    recoverFromDatabaseError(queryResult)
  }
}
