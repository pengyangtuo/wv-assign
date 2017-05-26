package wv.service.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.slf4j.LoggerFactory

/**
  * Created by ypeng on 2017-03-25.
  */
sealed trait DatabaseError
case object ConnectionFailure extends DatabaseError
case object ActionNotAllowed extends DatabaseError
case class UnhandledDatabaseError(msg: String) extends DatabaseError

trait Repository[RecordType, PrimaryKeyType] {

  def logger = LoggerFactory.getLogger(this.getClass)

  def create(record: RecordType): Future[Either[DatabaseError, RecordType]]
  def find(id: PrimaryKeyType): Future[Either[DatabaseError, Option[RecordType]]]
  def update(record: RecordType): Future[Either[DatabaseError, RecordType]]
  def list: Future[Either[DatabaseError, List[RecordType]]]

  def recoverFromDatabaseError[T](future: Future[Either[DatabaseError, T]]) =
  // TODO: handle duplicate insert
    future.recover {
      case ex: Exception =>
        logger.error(ex.getMessage)
        Left(UnhandledDatabaseError(ex.getMessage))
    }
}
