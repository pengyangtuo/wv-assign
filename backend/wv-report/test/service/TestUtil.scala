package service

import java.sql.Timestamp
import java.util.GregorianCalendar

/**
  * Created by ypeng on 2017-03-28.
  */
object TestUtil {
  def getTS(year: Int, month: Int, day: Int) =
    /* Calendar.MONTH starts from 0 */
    new Timestamp(new GregorianCalendar(year, month-1, day).getTimeInMillis)

  def randomIntWithin(start: Int, end: Int) = {
    val rnd = new scala.util.Random
    start + rnd.nextInt( (end - start) + 1 )
  }
}
