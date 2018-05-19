package com.knoldus.services

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import com.knoldus.domains.{UserDetails, UserEmail, UserInfo}
import com.knoldus.util.LoggerHelper
import com.typesafe.config.ConfigFactory
import model.PortalDataBase

import scala.concurrent.ExecutionContext.Implicits.global

trait UserDbService extends LoggerHelper {
  val config = ConfigFactory.load()
  val appDatabase = new PortalDataBase(config).getClusterDB
  val logger = getLogger(this.getClass)

  def createUser(user: UserDetails): Boolean = {
    logger.info("creating the user in database")
    Try(appDatabase.user.createUser(user)) match {
      case Success(_) => true
      case Failure(exception) => logger.error(exception.getMessage)
        false
    }
  }

  def getUserByEmail(email: String): Future[Option[UserDetails]] = {
    logger.info(s"searching for user in database $email")
    val result = appDatabase.user.getUserByEmail(email)
    result
  }.recoverWith{
    case _ => Future.failed(new Exception("unable to get userResponse"))
  }
  def getAllEmails: Future[List[UserDetails]] ={
    appDatabase.user.getAllUsers
  }
  def changeUserType(email:String,userType:String)={
    appDatabase.user.updateCategoryByEmail(email,userType)
  }
}
class UserDbServiceImpl extends UserDbService
