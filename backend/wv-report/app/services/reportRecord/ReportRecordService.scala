package services.reportRecord

import models.ReportRecord
import wv.service.services.{ApiService, ServiceError}

import scala.concurrent.Future

trait ReportRecordService extends ApiService[ReportRecord, Int] {
  /**
    * Insert record in batch, return number of records inserted if succeed
    * @param records
    * @return
    */
  def createBatch(records: List[ReportRecord]): Future[Either[ServiceError, Int]]
}
