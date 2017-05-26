package service

import java.sql.Timestamp
import java.util.GregorianCalendar

import org.scalatest.{FunSpec, MustMatchers}
import services.common.ServiceHelper._
import TestUtil._

/**
  * Created by ypeng on 2017-03-27.
  */
class ServiceHelperSpec extends FunSpec with MustMatchers{
  describe("getPayPeriod()") {

    it("should return (2017/2/1, 2017/2/15) for input date 2017/2/15") {
      val date = getTS(2017, 2, 15)

      val expectedStart = getTS(2017, 2, 1)
      val expectedEnd = getTS(2017, 2, 15)
      getPayPeriod(date) mustBe (expectedStart, expectedEnd)
    }

    it("should return (2017/1/1, 2017/1/15) for input date 2017/1/1") {
      val date = getTS(2017, 1, 1)

      val expectedStart = getTS(2017, 1, 1)
      val expectedEnd = getTS(2017, 1, 15)
      getPayPeriod(date) mustBe (expectedStart, expectedEnd)
    }

    it("should return (2017/3/16, 2017/3/31) for input date 2017/3/19") {
      val date = getTS(2017, 3, 19)

      val expectedStart = getTS(2017, 3, 16)
      val expectedEnd = getTS(2017, 3, 31)
      getPayPeriod(date) mustBe (expectedStart, expectedEnd)
    }

    it("should return (2017/4/16, 2017/4/30) for input date 2017/4/16") {
      val date = getTS(2017, 4, 16)

      val expectedStart = getTS(2017, 4, 16)
      val expectedEnd = getTS(2017, 4, 30)
      getPayPeriod(date) mustBe (expectedStart, expectedEnd)
    }
  }

  describe("reduceListOfEither()") {
    it("should return the original list if all element in the list is of type Right"){
      val list = List(1, 2, 3, 4, 5, 6)
      val input = list.map(Right(_))
      reduceListOfEither(input) mustBe Right(list)
    }

    it("should return first left in the list if there is one in the list"){
      val firstLeft = Left(2)
      val input = List(Right(1), firstLeft, Right(3), Right(4), Left(5), Right(6))
      reduceListOfEither(input) mustBe firstLeft
    }
  }
}
