package repositories.employee

import java.util.UUID
import javax.inject.Inject

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import configuration.Settings
import models.Employee
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
class EmployeeRepositoryImpl @Inject()(dbConfigProvider: DatabaseConfigProvider, config: Configuration)
  extends EmployeeRepository {

  val log = Logger(this.getClass)
  val settings = Settings(config)
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val employeesDb = TableQuery(EmployeeTable(settings.db.employeeTable))

  override def create(employee: Employee) = {
    val queryResult = dbConfig.db
      .run((employeesDb returning employeesDb.map(_.id)) += employee)
      .map(newId => Right(employee.copy(id = newId)))

    recoverFromDatabaseError(queryResult)
  }

  override def find(id: Int): Future[Either[DatabaseError, Option[Employee]]] = {
    val queryResult = dbConfig.db
      .run(employeesDb.filter(_.id === id).result.headOption)
      .map(employeeOpt => Right(employeeOpt))

    recoverFromDatabaseError(queryResult)
  }

  override def update(employee: Employee): Future[Either[DatabaseError, Employee]] = {
    val queryResult = dbConfig.db
      .run(employeesDb.insertOrUpdate(employee))
      .map(_ => Right(employee))

    recoverFromDatabaseError(queryResult)
  }

  override def list: Future[Either[DatabaseError, List[Employee]]] = {
    val queryResult = dbConfig.db
      .run(employeesDb.result)
      .map(employees => Right(employees.toList))

    recoverFromDatabaseError(queryResult)
  }
}
