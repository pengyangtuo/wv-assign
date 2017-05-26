package wv.service.services
import scala.concurrent.Future

/**
  * Created by yangtuopeng on 2017-03-26.
  */
trait ServiceError
case object UnableToHandleFile extends ServiceError
case object RepositoryError extends ServiceError
case object MethodNotSupported extends ServiceError
case object DuplicateCreation extends ServiceError

trait ApiService[ModelType, IdType] {
  def create(model: ModelType): Future[Either[ServiceError, ModelType]]
  def find(id: IdType): Future[Either[ServiceError, Option[ModelType]]]
  def update(model: ModelType): Future[Either[ServiceError, ModelType]]
  def list: Future[Either[ServiceError, List[ModelType]]]
}