
package com.knoldus.controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NonFatal

import akka.http.javadsl.model.{ContentTypes, HttpEntities}
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.knoldus.domains.User
import com.knoldus.services.UserService
import com.knoldus.util.{JsonHelper, LoggerHelper}
import com.knoldus.responses.ErrorResponses._


trait UserController extends JsonHelper with LoggerHelper{

  def userRoutes: Route = userPOST
  val userService:UserService
  val logger = getLogger(this.getClass)

  def userPOST: Route = path("kip" / "createusers") {

    import com.knoldus.domains.User
    post {
      entity(as[User]) { data =>
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
  private def handleUserCreationRequest(userReq: User): Future[HttpResponse] = {
        val user = userService.createUser(userReq)
          Future.successful(HttpResponse(OK, entity = HttpEntities.create(ContentTypes.APPLICATION_JSON,OK_PARSE(user))))
        }.recover {
          case exception:Exception =>
            logger.error(exception.getStackTrace.toString, exception)
            HttpResponse(InternalServerError, entity = HttpEntities.create(ContentTypes.APPLICATION_JSON, INTERNAL_SERVER_ERROR))
        }

  private def invalidJson(userReq:User): Future[HttpResponse] ={
      Future.successful(HttpResponse(BadRequest, entity = HttpEntities.create(ContentTypes.APPLICATION_JSON,getJsonDataTypeError("Invalid Json Field"))))
  }
  }



