package com.knoldus.controller

import akka.http.javadsl.model.ContentTypes
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.ContentNegotiator.Alternative.ContentType
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.knoldus.services.UserService
import com.knoldus.util.JsonHelper
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

class UserControllerTest extends WordSpec with JsonHelper with MockitoSugar with Matchers with ScalatestRouteTest {

  object UserTest extends UserController {
    val userService = mock[UserService]
  }

  val routes = UserTest.userRoutes

  val jsonRequest = ByteString(
    s"""
       |{
       |    "emailId": "anubhav.tarar@knoldus.in"
       |}
        """.stripMargin
  )

  val entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)


  "The User routes" should {

    " user creation should success with correct data" in
    pending
      Post("/kip/createusers",entity) ~> routes ~> check {
  // println(responseAs[String] )

// println("***************"+responseAs[String])
/* responseAs[String] shouldEqual
{
|    "data": {
|        "emailId": "anubhav.tarar@knoldus.in",
|        "userType": "trainee"
|    }
|}*/
}
}

}
