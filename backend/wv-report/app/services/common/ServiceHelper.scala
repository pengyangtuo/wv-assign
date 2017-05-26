package services.common

import java.sql.Timestamp
import java.util.Calendar

import scala.concurrent.Future

/**
  * Created by ypeng on 2017-03-27.
  */
object ServiceHelper {
  /**
    * Generate pay period tuple base on input target date
    * @param date
    * @return
    */
  def getPayPeriod(date: Timestamp): (Timestamp, Timestamp) = {
    val cal = Calendar.getInstance()
    cal.setTimeInMillis(date.getTime)

    val day = cal.get(Calendar.DAY_OF_MONTH)

    val firstDayInMonth = 1
    val lastDayInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val midDayInMonth = 15

    if(day <= midDayInMonth){
      cal.set(Calendar.DAY_OF_MONTH, firstDayInMonth)
      val start = new Timestamp(cal.getTime.getTime)
      cal.set(Calendar.DAY_OF_MONTH, midDayInMonth)
      val end = new Timestamp(cal.getTime.getTime)
      (start, end)
    }else {
      cal.set(Calendar.DAY_OF_MONTH, midDayInMonth+1)
      val start = new Timestamp(cal.getTime.getTime)
      cal.set(Calendar.DAY_OF_MONTH, lastDayInMonth)
      val end = new Timestamp(cal.getTime.getTime)
      (start, end)
    }
  }

  /**
    * Reduce list of either to create a either of list
    * @param list
    * @tparam A
    * @tparam B
    * @return Left if list contains any Left, Right[List] if all element in list is Right
    */
  def reduceListOfEither[A, B](list: List[Either[A, B]]) =
      list.partition(_.isLeft) match {
        case (Nil, rights) =>
          Right(for (Right(content) <- rights) yield content)
        case (errors, _) =>
          Left((for (Left(error) <- errors) yield error).head)
      }
}
