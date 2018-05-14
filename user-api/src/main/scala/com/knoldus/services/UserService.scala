package com.knoldus.services

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import com.knoldus.actor.DirectNotificationActor
import com.knoldus.domains.{User, UserResponse}
import com.knoldus.service.MailServiceImpl
import com.knoldus.util.{LoggerHelper, PassWordUtility, RandomUtil}

trait UserService extends LoggerHelper {

  val logger = getLogger(this.getClass)

  def createUser(userRequest: User): User = {
    val password = PassWordUtility.hashedPassword(RandomUtil.alphanumeric())
    //TODO store the info in cassandra db
    val system = ActorSystem("directNotificationActor")


    val directNotifcationActor = system
      .actorOf(Props(new DirectNotificationActor(new MailServiceImpl)))
    logger.info(s"Sending Email to ${ userRequest.emailId }}")
    import scala.concurrent.duration._

    import akka.util.Timeout

    implicit val timeout = Timeout(15 seconds)
    val user = UserResponse(userRequest.emailId, password)

    val isEmailDispatched = Try(
      directNotifcationActor ? user)

    isEmailDispatched match {
      case Success(_) =>
        User(user.emailId)
      case Failure(exception) => logger.error(s"Sending email failed ${ exception.getMessage }")
        throw new Exception(exception.getMessage)
    }
  }
}

object UserServiceImpl extends UserService
