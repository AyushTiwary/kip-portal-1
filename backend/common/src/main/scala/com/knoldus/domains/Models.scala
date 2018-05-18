package com.knoldus.domains

import java.text.SimpleDateFormat
import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat}

import scala.util.Try

final case class UserInfo(emailId: String, userType: Option[String] = None)

final case class UserDetails(emailId: String, password: String, userType: Option[String] = None)

final case class SessionDetails(startDate: String,
                                trainee: String,
                                technologyName: String,
                                numberOfDays: Int,
                                content: String,
                                assistantTrainer: Option[String])

final case class DisplaySchedule(startDate: String,
                                 endDate: String,
                                 trainee: String,
                                 technologyName: String,
                                 numberOfDays: Int,
                                 content: String,
                                 assistantTrainer: Option[String])

final case class UpdateSessionDetails(previousDate: String,
                                      updateDate: String)

final case class ScheduleInfo(sessionId: String,
                              startDate: String,
                              trainee: String,
                              technologyName: String,
                              numberOfDays: Int,
                              content: String,
                              assistantTrainer: Option[String])

case class SessionInfo(sessionId: String, startDate: String, numberOfDays: Int)

case object UserInfo extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userProtocol: RootJsonFormat[UserInfo] = jsonFormat2(UserInfo.apply)
}


case object UserDetails extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userLoginRequestProtocol = jsonFormat3(UserDetails.apply)
}

case object UpdateSessionDetails extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val updateSessionDetailsRequestProtocol = jsonFormat2(UpdateSessionDetails.apply)
}

case object SessionDetails extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val sessionDetailsRequestProtocol = jsonFormat6(SessionDetails.apply)
}
