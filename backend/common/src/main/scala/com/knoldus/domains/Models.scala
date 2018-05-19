package com.knoldus.domains

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class UserInfo(emailId: String, userType: Option[String] = None)

final case class UserDetails(emailId: String, password: String, userType: Option[String] = None)

final case class UpdateUserRequest(emailId: String, userType: String)

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

final case class HolidayInfo(date: String, content: String)
final case class UserEmail(emailId:String)

case object UserInfo extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userProtocol: RootJsonFormat[UserInfo] = jsonFormat2(UserInfo.apply)
}

case object UserDetails extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userLoginRequestProtocol = jsonFormat3(UserDetails.apply)
}
case object UserEmail extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userEmailRequestProtocol = jsonFormat1(UserEmail.apply)
}

case object UpdateSessionDetails extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val updateSessionDetailsRequestProtocol = jsonFormat2(UpdateSessionDetails.apply)
}

case object SessionDetails extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val sessionDetailsRequestProtocol = jsonFormat6(SessionDetails.apply)
}

case object HolidayInfo extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val holidaysRequestProtocol = jsonFormat2(HolidayInfo.apply)
}

case object DisplaySchedule extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val displayRequestProtocol = jsonFormat7(DisplaySchedule.apply)
}

case object UpdateUserRequest extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val updateUserRequestProtocol = jsonFormat2(UpdateUserRequest.apply)
}