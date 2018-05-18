
package com.knoldus.controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.http.javadsl.model.{ContentTypes, HttpEntities}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.knoldus.domains._
import com.knoldus.exceptions.UserAlreadyExistsException
import com.knoldus.responses.ErrorResponses._
import com.knoldus.services.{SessionService, UserService}
import com.knoldus.util.{JsonHelper, LoggerHelper}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._


trait UserController extends JsonHelper with LoggerHelper {

  val userService: UserService
  val sessionService = new SessionService
  val logger = getLogger(this.getClass)

  val userRoutes: Route = userPOST ~ userLoginPOST ~ createSessionPOST ~ updateSessionPOST ~ addHolidaysPOST

  def userPOST: Route = {
    cors() {
      path("kip" / "createuser") {

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
  }

  private def handleUserCreationRequest(userReq: UserInfo): Future[HttpResponse] = {
    val newUser = userService.createUser(userReq)

    newUser.map {
      res =>
        HttpResponse(OK,
          entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, OK_PARSE(res)))
    }.recoverWith {
      case userAlreadyException: UserAlreadyExistsException =>
        logger.error(userAlreadyException.printStackTrace().toString, userAlreadyException)
        Future
          .successful(HttpResponse(BadRequest,
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
    cors() {
      path("kip" / "login") {
        import com.knoldus.domains.UserDetails
        post {
          entity(as[UserDetails]) { data =>
            complete(handleLoginRequest(data))
          }
        }
      }
    }
  }

  private def handleLoginRequest(userLogin: UserDetails): Future[HttpResponse] = {
    userService.validateUser(userLogin).flatMap {
      user =>
        user.fold {
          Future
            .successful(HttpResponse(NotFound,
              entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, INVALID_CREDENTIALS)))
        } {
          result =>
            Future
              .successful(HttpResponse(OK,
                entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, OK_PARSE(result))))
        }
          .recoverWith {
            case _: Exception => Future.successful(HttpResponse(InternalServerError,
              entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, INTERNAL_SERVER_ERROR)))
          }
    }
  }

  def createSessionPOST: Route = {
    cors() {
      path("kip" / "createsession") {
        post {
          import com.knoldus.domains.SessionDetails
          entity(as[SessionDetails]) { data =>
            logger.info("------->" + data)
            complete(createSessionRequestHandler(data))
          }
        }
      }
    }
  }

  private def createSessionRequestHandler(data: SessionDetails): Future[HttpResponse] = {
    sessionService.createSession(data) map { displaySchedule: DisplaySchedule =>
      HttpResponse(OK,
        entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, OK_MSG("Your session successfully scheduled")))
    }
  }.recoverWith {
    case ex: Exception => Future.successful(HttpResponse(InternalServerError,
      entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, BAD_REQUEST(ex.getMessage))))
  }

  def updateSessionPOST: Route = {
    cors() {
      path("kip" / "updateSession") {
        post {
          import com.knoldus.domains.UpdateSessionDetails
          entity(as[UpdateSessionDetails]) { data =>
            logger.info("------->" + data)
            complete(updateSessionRequestHandler(data))
          }
        }
      }
    }
  }

  private def updateSessionRequestHandler(data: UpdateSessionDetails): Future[HttpResponse] = {
    sessionService.updateSession(data) map { displaySchedule: DisplaySchedule =>
      HttpResponse(OK,
        entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, OK_MSG("Your session successfully updated")))
    }
  }.recoverWith {
    case _: Exception => Future.successful(HttpResponse(InternalServerError,
      entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, INTERNAL_SERVER_ERROR)))
  }
  def addHolidaysPOST: Route = {
    cors() {
      path("kip" / "addholiday") {
        post {
          import com.knoldus.domains.HolidayInfo
          entity(as[HolidayInfo]) { data =>
            logger.info("------->" + data)
            complete(handleAddHolidayRequest(data))
          }
        }
      }
    }
  }

  private def handleAddHolidayRequest(holidayInfo: HolidayInfo)= {
    sessionService.addHoliday(holidayInfo).map{ _ =>
      HttpResponse(OK,
        entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, OK_MSG("Your holiday is successful")))
    }
  }.recoverWith {
    case _: Exception => Future.successful(HttpResponse(InternalServerError,
      entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, INTERNAL_SERVER_ERROR)))
  }
}



