package repositories.reportRecord

import models.ReportRecord
import wv.service.repository.{DatabaseError, Repository}

import scala.concurrent.Future

trait ReportRecordRepository extends Repository[ReportRecord, Int]{
  /**
    * Insert records in batch, return number of rows inserted
    * @param records
    * @return
    */
  def createBatch(records: List[ReportRecord]): Future[Either[DatabaseError, Int]]
}
