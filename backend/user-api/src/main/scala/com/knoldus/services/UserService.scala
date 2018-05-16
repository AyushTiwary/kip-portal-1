package com.knoldus.services

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import com.knoldus.actor.DirectNotificationActor
import com.knoldus.domains.{UserDetails, UserInfo}
import com.knoldus.exceptions.{DbException, UserAlreadyExistsException}
import com.knoldus.service.MailServiceImpl
import com.knoldus.util.{LoggerHelper, PassWordUtility, RandomUtil}


trait UserService extends LoggerHelper {
  val logger = getLogger(this.getClass)
  val userDbService = new UserDbServiceImpl

  def createUser(userRequest: UserInfo): Future[UserInfo] = {
    val password = RandomUtil.alphanumeric()
    val system = ActorSystem("directNotificationActor")
    isUserExists(userRequest).map {
      flag =>
        if (!flag) {
          val directNotificationActor = system
            .actorOf(Props(new DirectNotificationActor(new MailServiceImpl)))
          logger.info(s"Sending Email to ${ userRequest.emailId }}")
          import scala.concurrent.duration._

          import akka.util.Timeout
          val userType = if (userRequest.userType.isDefined) {
            userRequest.userType
          } else {
            Some(
              "Trainee")
          }
          implicit val timeout = Timeout(5 seconds)
          val userDetails = UserDetails(userRequest.emailId, password)
          val mailDispatch = Try {
            directNotificationActor ?
            userDetails
          }
          mailDispatch match {
            case Success(_) =>
              logger.info("Email is sent successfully !")
            case Failure(exception) => logger
              .error(s"Sending email failed ${ exception.getMessage }")
              throw new Exception(exception.getMessage)
          }

          UserDetails(userRequest.emailId, password, userType)

          val isRecordPersistsInDatabase = userDbService
            .createUser(UserDetails(userRequest.emailId,
              PassWordUtility.hashedPassword(password),
              userType))
          if (!isRecordPersistsInDatabase) {
            throw DbException("Can not insert the user in database")
          }
          else {
            UserInfo(userDetails.emailId, userType)
          }
        }
        else {
          throw UserAlreadyExistsException("User with this emailid Already exists in database")
        }
    }
  }

  private def isUserExists(userInfo: UserInfo): Future[Boolean] = {
    userDbService.getUserByEmail(userInfo.emailId).map {
      case Some(_: UserDetails) => true
      case None => false
    }
  }

  def validateUser(userLogin: UserDetails): Future[Option[UserInfo]] = {
    val userOpt = userDbService.getUserByEmail(userLogin.emailId)

    userOpt
      .map(user => user
        .filter(password => PassWordUtility.verifyPassword(userLogin.password, password.password))
        .map(userDetail => UserInfo(userDetail.emailId, userDetail.userType)))
  }
}

object UserServiceImpl extends UserService
