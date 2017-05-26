package models

import java.sql.Timestamp

import dtos.ResponsePayGroup

/**
  * Created by yangtuopeng on 2017-03-26.
  */
case class PayGroup(id: Int, name: String, rate: Double, createdTime: Timestamp, lastModifiedTime: Timestamp)

object PayGroup {
  implicit class PayGroupOps(payGroup: PayGroup) {
    def toDto: ResponsePayGroup =
      ResponsePayGroup(payGroup.id, payGroup.name, payGroup.rate, payGroup.createdTime, payGroup.lastModifiedTime)
  }
  def isSame(g1: PayGroup, g2: PayGroup) =
    g1.id == g2.id &&
      g1.name == g2.name &&
      g1.rate == g2.rate
}
