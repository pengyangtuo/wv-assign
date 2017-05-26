package service

import java.sql.Timestamp
import java.util.Calendar

import models.{ReportRecord, ReportSummary}
import org.scalatest.{BeforeAndAfterEach, FunSpec, MustMatchers}
import TestUtil._
import org.scalatest.concurrent.ScalaFutures
import repositories.recordSummary.ReportSummaryRepository
import services.reportSummary.ReportSummaryServiceImpl
import wv.service.repository.{DatabaseError, UnhandledDatabaseError}
import wv.service.services.ServiceError

import scala.concurrent.Future

/**
  * Created by ypeng on 2017-03-28.
  */

class MockSummaryRepo extends ReportSummaryRepository {
  var summary: Map[(Int, Timestamp, Timestamp), ReportSummary] = Map.empty[(Int, Timestamp, Timestamp), ReportSummary]

  def flush = {
    summary = summary.empty
  }

  override def upsertBatch(records: List[ReportSummary]): Future[Either[DatabaseError, Int]] = {
    records.map{record =>
      summary = summary + ((record.employeeId, record.payPeriodStart, record.payPeriodEnd) -> record)
    }
    Future.successful(Right(records.size))
  }

  override def create(record: ReportSummary): Future[Either[DatabaseError, ReportSummary]] = {
    summary = summary + ((record.employeeId, record.payPeriodStart, record.payPeriodEnd) -> record)
    Future.successful(Right(record))
  }

  override def find(id: (Int, Timestamp, Timestamp)): Future[Either[DatabaseError, Option[ReportSummary]]] =
    Future.successful(Right(summary.get(id)))

  override def update(record: ReportSummary): Future[Either[DatabaseError, ReportSummary]] =
    Future.successful(Left(UnhandledDatabaseError("mock repo")))

  override def list: Future[Either[DatabaseError, List[ReportSummary]]] =
    Future.successful(Right(summary.values.toList))
}

class ReportSummaryServiceSpec extends FunSpec with MustMatchers with ScalaFutures with BeforeAndAfterEach {

  val now = new Timestamp(Calendar.getInstance.getTime.getTime)
  val rateA = 20
  val rateB = 30
  val mockRepo = new MockSummaryRepo
  val reportSummaryService = new ReportSummaryServiceImpl(mockRepo)

  override def beforeEach = {
    mockRepo.flush
  }

  describe("ReportSummaryService") {

    def assertSummaryIsSameDisgardPayPeriod(s1: ReportSummary, s2: ReportSummary) = {
      s1.employeeId mustBe s2.employeeId
      s1.payPeriodStart mustBe s2.payPeriodStart
      s1.payPeriodEnd mustBe s2.payPeriodEnd
      s1.amount mustBe s2.amount
    }

    def assertUpdateSuccess[T](updateFuture: Future[Either[ServiceError, T]]) =
      whenReady(updateFuture){ result => result.isRight mustBe true }

    def assertFindSuccess[T](findFuture: Future[Either[ServiceError, T]], target: T) =
      whenReady(findFuture){ result => result mustBe Right(Some(target)) }

    def assertFindFailure[T](findFuture: Future[Either[ServiceError, T]], target: T) =
      whenReady(findFuture){ result => result mustBe Right(None) }

    describe("find()") {
      it("should return Right(None) if summary does not exist") {
        val pk = (1, getTS(2016,1,1), getTS(2016,1,15))
        val summary = ReportSummary(pk._1, pk._2, pk._3 , 300, now, now)
        assertFindFailure(reportSummaryService.find(pk), summary)
      }

      it("should return corresponding summary if it exist") {
        val pk = (1, getTS(2016,1,1), getTS(2016,1,15))
        val summary = ReportSummary(pk._1, pk._2, pk._3 , 300, now, now)
        assertUpdateSuccess(reportSummaryService.upsertBatchSummary(List(summary)))
        assertFindSuccess(reportSummaryService.find(pk), summary)
      }
    }

    describe("list()") {
      it("should return empty list if repo is empty") {
        whenReady(reportSummaryService.list){ result => result mustBe Right(List.empty) }
      }

      it("should return all summary in repe if repo is not empty") {
        val pk1 = (1, getTS(2016,1,1), getTS(2016,1,15))
        val pk2 = (2, getTS(2016,2,1), getTS(2016,2,15))

        val summary = List(
          ReportSummary(pk1._1, pk1._2, pk1._3 , 300, now, now),
          ReportSummary(pk2._1, pk2._2, pk2._3 , 400, now, now)
        )

        assertUpdateSuccess(reportSummaryService.upsertBatchSummary(summary))
        whenReady(reportSummaryService.list){ result => result mustBe Right(mockRepo.summary.values.toList) }
      }
    }

    describe("update()") {
      it("should return ServiceError") {
        val pk = (1, getTS(2016,1,1), getTS(2016,1,15))
        val summary = ReportSummary(pk._1, pk._2, pk._3 , 300, now, now)
        whenReady(reportSummaryService.update(summary)){ result => result.isLeft mustBe true }
      }
    }

    describe("upsertBatchSummary()") {

      it("should insert new report summary to repo if their employee id and pay period does not exist in repo") {

        val pk1 = (1, getTS(2016,1,1), getTS(2016,1,15))
        val pk2 = (2, getTS(2016,2,1), getTS(2016,2,15))
        val newSummary1 = ReportSummary(pk1._1, pk1._2, pk1._3 , 300, now, now)
        val newSummary2 = ReportSummary(pk2._1, pk2._2, pk2._3 , 400, now, now)

        val upsertRes= reportSummaryService.upsertBatchSummary(List(newSummary1, newSummary2))
        assertUpdateSuccess(upsertRes)

        assertFindSuccess(reportSummaryService.find(pk1), newSummary1)
        assertFindSuccess(reportSummaryService.find(pk2), newSummary2)
      }

      it("should merge new report summary with existing summary in repo with createdTime unchanged") {

        val pk1 = (1, getTS(2016,1,1), getTS(2016,1,15))
        val pk2 = (2, getTS(2016,2,1), getTS(2016,2,15))
        val modifiedTime = new Timestamp(now.getTime+10000)

        val newSummary1 = ReportSummary(pk1._1, pk1._2, pk1._3 , 300, now, now)
        val newSummary2 = ReportSummary(pk2._1, pk2._2, pk2._3 , 400, now, now)
        val updatedSummary1 = ReportSummary(pk1._1, pk1._2, pk1._3, 1000, modifiedTime, modifiedTime)

        // first update
        val upsertRes1 = reportSummaryService.upsertBatchSummary(List(newSummary1))
        assertUpdateSuccess(upsertRes1)
        assertFindSuccess(reportSummaryService.find(pk1), newSummary1)

        // first update
        val upsertRes2 = reportSummaryService.upsertBatchSummary(List(updatedSummary1, newSummary2))
        assertUpdateSuccess(upsertRes2)
        // the create time should not change on a updated summary
        assertFindSuccess(
          reportSummaryService.find(pk1),
          updatedSummary1.copy(amount = (updatedSummary1.amount+newSummary1.amount), createdTime = now)
        )
        assertFindSuccess(reportSummaryService.find(pk2), newSummary2)

      }
    }

    describe("mergeSingleEmployeeRecords()") {
      val reportId = 1
      val employeeId = 1
      val rate = rateA

      it("should calculate pay amount within each pay period correctly") {
        val hours = Array(20, 40, 60)
        val records = List(
          ReportRecord(1, reportId, employeeId, hours(0), rate, getTS(2016, 1, 1)),
          ReportRecord(2, reportId, employeeId, hours(1), rate, getTS(2016, 1, 16)),
          ReportRecord(3, reportId, employeeId, hours(2), rate, getTS(2016, 2, 1))
        )

        val expectedAmounts = hours.map(_ * rate)
        val actualSummary = reportSummaryService.mergeSingleEmployeeRecords(employeeId, records)

        actualSummary.size mustEqual 3
        actualSummary.zip(expectedAmounts).map(summaryAndAmountTuple => {
          val summary = summaryAndAmountTuple._1
          val amount = summaryAndAmountTuple._2
          summary.amount mustBe amount
        })
      }

      it("should merge report records in the same pay period") {
        val hours = Array(20, 40, 60)
        val records = List(
          ReportRecord(1, reportId, employeeId, hours(0), rate, getTS(2016, 1, randomIntWithin(1, 15))),
          ReportRecord(2, reportId, employeeId, hours(1), rate, getTS(2016, 1, randomIntWithin(1, 15))),
          ReportRecord(3, reportId, employeeId, hours(2), rate, getTS(2016, 1, randomIntWithin(1, 15)))
        )

        val expectedAmount = hours.fold(0)((sum, h) => sum + (h * rateA))
        val expectedSummary = ReportSummary(employeeId, getTS(2016,1,1), getTS(2016,1,15), expectedAmount, now, now)
        val actualSummary = reportSummaryService.mergeSingleEmployeeRecords(employeeId, records)

        actualSummary.size mustEqual 1
        assertSummaryIsSameDisgardPayPeriod(expectedSummary, actualSummary.head)
      }
    }

    describe("generateSummaryFromRecords()") {
      val reportId = 1

      it("should generate list of report summary with correct pay amount and pay period") {
        val employees = Array(1, 2)
        // this record list should generate 2 report summary entry
        val employeeRecords1 = List(
          ReportRecord(0, reportId, employees(0), 10, rateA, getTS(2016, 1, randomIntWithin(1, 15))),
          ReportRecord(0, reportId, employees(0), 20, rateA, getTS(2016, 1, randomIntWithin(1, 15))),
          ReportRecord(0, reportId, employees(0), 40, rateA, getTS(2016, 1, randomIntWithin(16, 31)))
        )

        // this record list should generate 1 report summary entry
        val employeeRecords2 = List(
          ReportRecord(0, reportId, employees(1), 10, rateB, getTS(2016, 2, randomIntWithin(1, 15))),
          ReportRecord(0, reportId, employees(1), 20, rateB, getTS(2016, 2, randomIntWithin(1, 15))),
          ReportRecord(0, reportId, employees(1), 30, rateB, getTS(2016, 2, randomIntWithin(1, 15)))
        )

        val expectedSummary = List(
          ReportSummary(employees(0), getTS(2016, 1, 1), getTS(2016, 1, 15), (10+20)*rateA, now, now),
          ReportSummary(employees(0), getTS(2016, 1, 16), getTS(2016, 1, 31), 40*rateA, now, now),
          ReportSummary(employees(1), getTS(2016, 2, 1), getTS(2016, 2, 15), (10+20+30)*rateB, now, now)
        )
        val actualSummary = reportSummaryService.generateSummaryFromRecords(employeeRecords1 ::: employeeRecords2)

        actualSummary.size mustBe 3
        actualSummary.sortWith(_.employeeId < _.employeeId).zip(expectedSummary).map(pair => {
          assertSummaryIsSameDisgardPayPeriod(pair._1, pair._2)
        })
      }
    }
  }
}
