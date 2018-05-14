package com.knoldus.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.knoldus.controller.UserController
import akka.http.scaladsl.server.Directives._
import com.knoldus.services.{UserService, UserServiceImpl}

trait Kip_Portal_Api {
  implicit val system: ActorSystem

  lazy implicit val executor = system.dispatcher

  lazy implicit val materializer = ActorMaterializer()

  val singletoneRoute: Route = pathSingleSlash {
    get {
      complete {
        HttpResponse(StatusCodes.OK, entity = "Welcome to kip api!")
      }
    } ~
    post {
      complete {
        HttpResponse(StatusCodes.OK, entity = "Welcome to kip api!")
      }
    }
  }
}

object StartKipServer extends App with UserController with Kip_Portal_Api{
  implicit val system: ActorSystem = ActorSystem("kip-api-routes")
  val userService:UserService = UserServiceImpl


  Http().bindAndHandle(userRoutes,"localhost",8080)
  println(s"Kip_Portal_Server is started")
}
