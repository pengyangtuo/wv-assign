package repositories.recordSummary

import java.sql.Timestamp

import models.ReportSummary
import wv.service.repository.DatabaseError
import wv.service.repository.Repository

import scala.concurrent.Future

/**
  * Created by yangtuopeng on 2017-03-26.
  */
trait ReportSummaryRepository extends Repository[ReportSummary, (Int, Timestamp, Timestamp)]{
  /**
    * upsert records in batch, return number of rows inserted
    * @param records
    * @return
    */
  def upsertBatch(records: List[ReportSummary]): Future[Either[DatabaseError, Int]]
}

