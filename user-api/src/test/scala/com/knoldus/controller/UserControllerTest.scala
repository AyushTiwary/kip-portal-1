package com.knoldus.controller

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.knoldus.domains.User
import com.knoldus.services.UserService
import com.knoldus.util.JsonHelper
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}


class UserControllerTest
  extends WordSpec with JsonHelper with MockitoSugar with Matchers with ScalatestRouteTest {


  val routes = UserTest.userRoutes
  val user = new User("anubhav.tarar@knoldus.in", None)
  val userResponse = new User("anubhav.tarar@knoldus.in", user.userType)
  val jsonRequest =
    """{
        "emailId":"anubhav.tarar@knoldus.in"
        }""".stripMargin
  when(UserTest.userService.createUser(user)) thenReturn (userResponse)
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
      Post("/kip/createusers", entity) ~> routes ~> check {
        handled shouldBe true
        val result = responseAs[String]
        result shouldEqual """{"data":{"emailId":"anubhav.tarar@knoldus.in"}}"""

      }
    }
    " user creation should not success with incorrect data" in {
      val newEntity = HttpEntity(MediaTypes.`application/json`, invalidJsonRequest)
      Post("/kip/createusers", newEntity) ~> routes ~> check {
        handled shouldBe true
        val result = responseAs[String]
        result shouldEqual """{"message":"Invalid Json Field"}"""
      }
    }
  }
}

