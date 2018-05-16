
package com.knoldus.controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import akka.http.javadsl.model.{ContentTypes, HttpEntities}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.knoldus.domains.{UserDetails, UserInfo}
import com.knoldus.exceptions.UserAlreadyExistsException
import com.knoldus.responses.ErrorResponses._
import com.knoldus.services.UserService
import com.knoldus.util.{JsonHelper, LoggerHelper}


trait UserController extends JsonHelper with LoggerHelper {

  val userService: UserService
  val logger = getLogger(this.getClass)

  def userRoutes: Route = userPOST ~ userLoginPOST

  def userPOST: Route = {
    path("kip" / "createusers") {

      import com.knoldus.domains.UserInfo
      post {
        entity(as[UserInfo]) { data =>
          logger.info(s"Get the new user with data $data")
          if (data.emailId.isEmpty) {
            complete(invalidJson(data))
          }
          else {
            complete(handleUserCreationRequest(data))
          }
        }
      }
    }
  }

  private def handleUserCreationRequest(userReq: UserInfo): Future[HttpResponse] = {
    val newUser = userService.createUser(userReq)

    newUser.map {
      res =>
        HttpResponse(OK,
          entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, OK_PARSE(res)))
    }.recoverWith {
      case userAlreadyException: UserAlreadyExistsException =>
        logger.error(userAlreadyException.printStackTrace.toString, userAlreadyException)
        Future
          .successful(HttpResponse(OK,
            entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, USER_ALREADY_EXISTS)))

      case exception: Exception =>
        logger.error(exception.getStackTrace.toString, exception)
        Future.successful(HttpResponse(InternalServerError,
          entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, INTERNAL_SERVER_ERROR)))
    }

  }

  private def invalidJson(userReq: UserInfo): Future[HttpResponse] = {
    Future
      .successful(HttpResponse(BadRequest,
        entity = HttpEntities
          .create(ContentTypes.APPLICATION_JSON, getJsonDataTypeError("Invalid Json Field"))))
  }

  def userLoginPOST: Route = {
    path("kip" / "login") {
      import com.knoldus.domains.UserDetails
      post {
        entity(as[UserDetails]) { data =>
          complete(handleLoginRequest(data))
        }
      }
    }
  }

  private def handleLoginRequest(userLogin: UserDetails): Future[HttpResponse] = {
    userService.validateUser(userLogin).flatMap {
      user =>
        user.fold {
          Future
            .successful(HttpResponse(OK,
              entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, INVALID_CREDENTIALS)))
        } {
          result => Future
            .successful(HttpResponse(OK,
              entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, OK_PARSE(result))))
        }
          .recoverWith {
            case _: Exception => Future.successful(HttpResponse(InternalServerError,
              entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, INTERNAL_SERVER_ERROR)))
          }
    }
  }
}



