package wv.service.services

import java.io.File

import scala.concurrent.Future

/**
  * Created by yangtuopeng on 2017-03-26.
  */
trait FileServiceError
case object CorruptedFile extends FileServiceError
case object MissParsingData extends FileServiceError

trait FileService[ResultType] {
  def handleFile(input: File): Future[Either[FileServiceError, ResultType]]
}
