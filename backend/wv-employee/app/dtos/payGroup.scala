package dtos

import java.sql.Timestamp
import java.util.Calendar

import models.PayGroup
import play.api.libs.json.{JsError, Json, Reads, Writes}

/**
  * Created by ypeng on 2017-03-24.
  */
case class CreatePayGroup(name: String, rate: Double)
case class UpdatePayGroup(id: Int, name: Option[String], rate: Option[Double])
case class ResponsePayGroup(id: Int, name: String, rate: Double, createdTime: Timestamp, lastModifiedTime: Timestamp)

object CreatePayGroup {
  implicit class CreatePayGroupOps(createPayGroup: CreatePayGroup) {
    def toModel: PayGroup = {
      val now = new Timestamp(Calendar.getInstance.getTime.getTime)
      PayGroup(0, createPayGroup.name, createPayGroup.rate, now, now)
    }
  }

  implicit val readCreatePayGroup: Reads[CreatePayGroup] = Reads[CreatePayGroup] {
    json => for {
      name <- (json \ "name").validate[String]
        .filter(JsError("Group name must not be empty"))(_.size > 0)
      rate <- (json \ "rate").validate[Double]
    } yield CreatePayGroup(name, rate)
  }
}

object UpdatePayGroup {
  implicit class UpdatePayGroupOps(updatePayGroup: UpdatePayGroup) {
    def toModel(payGroup: PayGroup): PayGroup = {
      val now = new Timestamp(Calendar.getInstance.getTime.getTime)
      PayGroup(
        updatePayGroup.id,
        updatePayGroup.name.getOrElse(payGroup.name),
        updatePayGroup.rate.getOrElse(payGroup.rate),
        payGroup.createdTime,
        now
      )
    }
  }

  implicit val readUpdatePayGroup: Reads[UpdatePayGroup] = Reads[UpdatePayGroup] {
    json => for {
      id <- (json \ "id").validate[Int]
      name <- (json \ "name").validateOpt[String]
        .filter(JsError("Group name must not be empty"))(_.size > 0)
      rate <- (json \ "rate").validateOpt[Double]
    } yield UpdatePayGroup(id, name, rate)
  }
}

object ResponsePayGroup {
  implicit val writeResponsePayGroup: Writes[ResponsePayGroup] = Json.writes[ResponsePayGroup]

  implicit class ResponsePayGroupOps(responsePayGroup: ResponsePayGroup) {
    def toJson = Json.toJson(responsePayGroup)(writeResponsePayGroup)
  }

  implicit class ResponsePayGroupListOps(responsePayGroupList: List[ResponsePayGroup]) {
    def toJson = Json.toJson(responsePayGroupList)
  }
}