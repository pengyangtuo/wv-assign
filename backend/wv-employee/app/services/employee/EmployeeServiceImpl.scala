package services.employee

import javax.inject.Inject

import models.Employee
import repositories.employee.EmployeeRepository
import wv.service.repository.{DatabaseError, UnhandledDatabaseError}
import wv.service.services.{RepositoryError, ServiceError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by ypeng on 2017-03-25.
  */

class EmployeeServiceImpl @Inject()(employeeRepo: EmployeeRepository) extends EmployeeService {
  def parseDBError[T](databaseResult: Either[DatabaseError, T]) =
    databaseResult.left.map{
      case UnhandledDatabaseError(msg) =>

        RepositoryError
      case _ => RepositoryError
    }

  override def create(employee: Employee) =
    employeeRepo
      .create(employee)
      .map(parseDBError)

  override def find(id: Int): Future[Either[ServiceError, Option[Employee]]] =
    employeeRepo
      .find(id)
      .map(parseDBError)

  override def update(employee: Employee): Future[Either[ServiceError, Employee]] =
    employeeRepo
      .update(employee)
      .map(parseDBError)

  override def list: Future[Either[ServiceError, List[Employee]]] =
    employeeRepo
      .list
      .map(parseDBError)
}
