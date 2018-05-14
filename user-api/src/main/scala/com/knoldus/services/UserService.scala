package com.knoldus.services

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import com.knoldus.actor.DirectNotificationActor
import com.knoldus.domains.{User, UserResponse}
import com.knoldus.service.MailServiceImpl
import com.knoldus.util.{LoggerHelper, PassWordUtility, RandomUtil}

import scala.util.{Failure, Success, Try}


trait UserService extends LoggerHelper {
  val logger = getLogger(this.getClass)

  def createUser(userRequest: User): User = {
    val password = PassWordUtility.hashedPassword(RandomUtil.alphanumeric())
    //TODO store the info in cassandra db
    val system = ActorSystem("directNotificationActor")

    val directNotificationActor = system
      .actorOf(Props(new DirectNotificationActor(new MailServiceImpl)))
    logger.info(s"Sending Email to ${userRequest.emailId}}")
    import akka.util.Timeout

    import scala.concurrent.duration._

    implicit val timeout = Timeout(5 seconds)
    val mailDispatch = Try {
      directNotificationActor ?
        UserResponse(userRequest.emailId, password, userRequest.userType)
    }
    mailDispatch match {
      case Success(_) =>
        logger.info("Email is sent successfully !")
        userRequest
      case Failure(exception) => logger.error(s"Sending email failed ${exception.getMessage}")
        throw new Exception(exception.getMessage)
    }
  }
}

object UserServiceImpl extends UserService
