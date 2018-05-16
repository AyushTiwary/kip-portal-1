package com.knoldus.controller

import scala.concurrent.Future

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.knoldus.domains.{UserDetails, UserInfo}
import com.knoldus.services.UserService
import com.knoldus.util.JsonHelper
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}


class UserControllerTest
  extends WordSpec with JsonHelper with MockitoSugar with Matchers with ScalatestRouteTest {

  val routes = UserTest.userRoutes
  val user = new UserInfo("anubhav.tarar@knoldus.in", None)
  val userResponse = new UserInfo("anubhav.tarar@knoldus.in", user.userType)
  val jsonRequest =
    """{
        "emailId":"anubhav.tarar@knoldus.in"
        }""".stripMargin

  val loginjsonRequest =
    """{
        "emailId":"anubhav.tarar@knoldus.in",
        "password":"abc"
        }""".stripMargin

  when(UserTest.userService.createUser(user)) thenReturn Future.successful(userResponse)
  when(UserTest.userService.validateUser(UserDetails("anubhav.tarar@knoldus.in", "abc")))
    .thenReturn(Future.successful(Option(UserInfo("anubhav.tarar@knoldus.in", Some("Trainee")))))

  val invalidJsonRequest =
    """{
        "emailId":""
        }""".stripMargin
  val entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)

  object UserTest extends UserController {
    val userService = mock[UserService]
  }

  "The User routes" should {

    " user creation should success with correct data" in {
      Post("/kip/createuser", entity) ~> routes ~> check {
        handled shouldBe true
        val result = responseAs[String]
        result shouldEqual """{"data":{"emailId":"anubhav.tarar@knoldus.in"}}"""

      }
    }
    " user creation should not success with incorrect data" in {
      val newEntity = HttpEntity(MediaTypes.`application/json`, invalidJsonRequest)
      Post("/kip/createuser", newEntity) ~> routes ~> check {
        handled shouldBe true
        val result = responseAs[String]
        result shouldEqual """{"message":"Invalid Json Field"}"""
      }
    }
    " user must be able to login with correct data" in {
      val newEntity1 = HttpEntity(MediaTypes.`application/json`, loginjsonRequest)
      Post("/kip/login", newEntity1) ~> routes ~> check {
        handled shouldBe true
        val result = responseAs[String]
        result shouldEqual
        """{"data":{"emailId":"anubhav.tarar@knoldus.in","userType":"Trainee"}}"""
      }
    }
  }
}

