package model

import com.datastax.driver.core.ResultSet
import com.knoldus.domains.UserResponse
import com.outworkers.phantom.CassandraTable
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._

import scala.concurrent.Future

abstract class User extends CassandraTable[User, UserResponse] with RootConnector {

  def createUser(userInfo: UserResponse): Future[ResultSet] = {
    insert.value(_.email, userInfo.emailId)
      .value(_.passWord, userInfo.passWord)
      .value(_.category, userInfo.userType)
      .future()
  }.recoverWith {
    case _ => Future.failed(new Exception("Unable to create the user"))
  }

  def getUserByEmail(email: String): Future[UserResponse] =
    select.where(_.email eqs email).one().map { maybeResponse =>
      maybeResponse.fold[Future[UserResponse]] {
        Future.failed(new Exception("unable to get userResponse"))
      } { userResponse =>
        Future.successful(userResponse)
      }
    }.flatMap(identity).recoverWith {
      case _ => Future.failed(new Exception("unable to get User"))
    }

  def updateCategoryByEmail(email: String, category: String): Future[ResultSet] = {
      update.where(_.email eqs email)
        .modify(_.category setTo Option(category)).future()
    }.recoverWith{
    case _ => Future.failed(new Exception("unable to update the category"))
  }

  def updatePasswordByEmail(email: String, password: String): Future[ResultSet] = {
      update.where(_.email eqs email)
        .modify(_.passWord setTo password).future()
    }.recoverWith{
    case _ => Future.failed(new Exception("unable to update the password"))
  }

  object email extends StringColumn(this) with PartitionKey

  object category extends OptionalStringColumn(this)

  object passWord extends StringColumn(this)

}
class AppDatabase(val keyspace: CassandraConnection) extends Database[AppDatabase](keyspace) {

  object user extends User with Connector
}
