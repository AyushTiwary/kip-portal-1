package com.knoldus.domains

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class User(emailId: String, userType: Option[String] = Some("Trainee"))

final case class UserResponse(emailId: String, passWord: String, userType: Option[String] = Some("Trainee"))


case object User extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userProtocol: RootJsonFormat[User] = jsonFormat2(User.apply)
}
