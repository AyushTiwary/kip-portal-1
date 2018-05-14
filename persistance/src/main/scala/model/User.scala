package model

import com.datastax.driver.core.ResultSet
import com.outworkers.phantom.CassandraTable
import com.outworkers.phantom.dsl._

import scala.concurrent.Future

case class UserInfo(email: String, passWord: String, category: Category.Value)

abstract class User extends CassandraTable[User, UserInfo] with RootConnector {

  def createUser(userInfo: UserInfo): Future[ResultSet] = {
    insert.value(_.email, userInfo.email)
      .value(_.passWord, userInfo.passWord)
      .value(_.category, userInfo.category)
      .future()
  }.recoverWith {
    case _ => Future.failed(new Exception("Unable to create the user"))
  }

  def getUserByEmail(email: String): Future[UserInfo] =
    select.where(_.email eqs email).one().map { mayBeUserInfo =>
      mayBeUserInfo.fold[Future[UserInfo]] {
        Future.failed(new Exception("unable to get userInfo"))
      } { userInfo =>
        Future.successful(userInfo)
      }
    }.flatMap(identity).recoverWith {
      case _ => Future.failed(new Exception("unable to get User"))
    }

  def updateCategoryByEmail(email: String, category: Category.Value): Future[ResultSet] = {
      update.where(_.email eqs email)
        .modify(_.category setTo category).future()
    }.recoverWith{
    case _ => Future.failed(new Exception("unable to update the category"))
  }

  object email extends StringColumn(this) with PartitionKey

  object category extends Col[Category.Value](this) with Index

  object passWord extends StringColumn(this)

}
