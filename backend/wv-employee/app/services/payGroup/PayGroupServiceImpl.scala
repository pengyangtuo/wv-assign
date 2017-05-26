package services.payGroup

import javax.inject.Inject

import models.PayGroup
import repositories.payGroup.PayGroupRepository
import wv.service.repository.DatabaseError
import wv.service.services.{RepositoryError, ServiceError}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by ypeng on 2017-03-25.
  */
class PayGroupServiceImpl @Inject()(payGroupRepo: PayGroupRepository) extends PayGroupService {
  def parseDBError[T](databaseResult: Either[DatabaseError, T]) =
    databaseResult.left.map {
      case _ => RepositoryError
    }

  override def create(payGroup: PayGroup) =
    payGroupRepo
      .create(payGroup)
      .map(parseDBError)

  override def find(id: Int): Future[Either[ServiceError, Option[PayGroup]]] =
    payGroupRepo
      .find(id)
      .map(parseDBError)

  override def update(payGroup: PayGroup): Future[Either[ServiceError, PayGroup]] =
    payGroupRepo
      .update(payGroup)
      .map(parseDBError)

  override def list: Future[Either[ServiceError, List[PayGroup]]] =
    payGroupRepo
      .list
      .map(parseDBError)
}
