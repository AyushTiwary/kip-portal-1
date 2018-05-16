package com.knoldus.domains

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

final case class UserInfo(emailId: String, userType: Option[String] = None)

final case class UserDetails(emailId: String, password: String, userType: Option[String] = None)


case object UserInfo extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userProtocol: RootJsonFormat[UserInfo] = jsonFormat2(UserInfo.apply)
}


case object UserDetails extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val userLoginRequestProtocol = jsonFormat3(UserDetails.apply)
  }


