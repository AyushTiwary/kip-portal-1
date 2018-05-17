package model

import com.datastax.driver.core.ResultSet
import com.knoldus.domains.{ScheduleInfo, SessionInfo, UserDetails}
import com.outworkers.phantom.CassandraTable
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl._

import scala.concurrent.Future

abstract class User extends CassandraTable[User, UserDetails] with RootConnector {

  def createUser(userInfo: UserDetails): Future[ResultSet] = {
    insert.value(_.email, userInfo.emailId)
      .value(_.passWord, userInfo.password)
      .value(_.category, userInfo.userType)
      .future()
  }.recoverWith {
    case _ => Future.failed(new Exception("Unable to create the user"))
  }

  def getUserByEmail(email: String): Future[Option[UserDetails]] = {
    select.where(_.email eqs email).one()
  }.recoverWith {
    case _ => Future.failed(new Exception("unable to get userResponse"))
  }

  def updateCategoryByEmail(email: String, category: String): Future[ResultSet] = {
    update.where(_.email eqs email)
      .modify(_.category setTo Option(category)).future()
  }.recoverWith {
    case _ => Future.failed(new Exception("unable to update the category"))
  }

  def updatePasswordByEmail(email: String, password: String): Future[ResultSet] = {
    update.where(_.email eqs email)
      .modify(_.passWord setTo password).future()
  }.recoverWith {
    case _ => Future.failed(new Exception("unable to update the password"))
  }

  object email extends StringColumn(this) with PartitionKey

  object category extends OptionalStringColumn(this)

  object passWord extends StringColumn(this)

}

abstract class Schedule extends CassandraTable[Schedule, ScheduleInfo] with RootConnector {

  def createSchedule(scheduleInfo: ScheduleInfo): Future[ResultSet] = {
    insert.value(_.sessionId, scheduleInfo.sessionId)
      .value(_.date, scheduleInfo.startDate)
      .value(_.trainee, scheduleInfo.trainee)
      .value(_.technologyName, scheduleInfo.technologyName)
      .value(_.numberOfDays, scheduleInfo.numberOfDays)
      .value(_.content, scheduleInfo.content)
      .value(_.assistantTrainer, scheduleInfo.assistantTrainer)
      .future()
  }.recoverWith {
    case _ => Future.failed(new Exception("unable to create the schedule"))
  }

  def getAll: Future[List[ScheduleInfo]] =
    select.fetch()

  def updateScheduleDate(sessionId: String, date: String): Future[ResultSet] = {
    update.where(_.sessionId eqs sessionId)
      .modify(_.date setTo date).future()
  }

  def updateAssistantTrainer(sessionId: String, assistantTrainer: Option[String]): Future[ResultSet] = {
    update.where(_.sessionId eqs sessionId)
      .modify(_.assistantTrainer setTo assistantTrainer).future()
  }

  def getScheduleBySessionId(sessionId: String): Future[Option[ScheduleInfo]] =
    select.where(_.sessionId eqs sessionId).one

  object sessionId extends StringColumn(this) with PartitionKey

  object date extends StringColumn(this)

  object trainee extends StringColumn(this)

  object technologyName extends StringColumn(this)

  object numberOfDays extends IntColumn(this)

  object content extends StringColumn(this)

  object assistantTrainer extends OptionalStringColumn(this)

}

abstract class KnolSession extends CassandraTable[KnolSession, SessionInfo] with RootConnector {

  def createSession(sessionId: String, date: String, numberOfDays: Int): Future[ResultSet] =
    insert.value(_.sessionId, sessionId)
      .value(_.date, date)
    .value(_.numberOfDays, numberOfDays)
      .future()

  def deleteSession(date: String): Future[ResultSet] =
    delete.where(_.date eqs date).future()

  def getAll: Future[List[SessionInfo]] =
    select.fetch()

  def getSessionByDate(date: String): Future[Option[SessionInfo]] =
    select.where(_.date eqs date).one()

  object date extends StringColumn(this) with PartitionKey

  object sessionId extends StringColumn(this)

  object numberOfDays extends IntColumn(this)

}

case class HolidayDetail(date: String, holiday: String)

abstract class Holiday extends CassandraTable[Holiday, HolidayDetail] with RootConnector {

  def createHoliday(date: String, holiday: String): Future[ResultSet] =
    insert.value(_.date, date)
      .value(_.holiday, holiday)
      .future()

  def getHoliday(date: String): Future[Option[HolidayDetail]] =
    select.where(_.date eqs date).one

  def deleteHoliday() = ??? //Todo(ayush)

  def updateHoliday() = ??? //Todo(ayush)


  object date extends StringColumn(this) with PartitionKey

  object holiday extends StringColumn(this)

}

abstract class SessionDate extends CassandraTable[SessionDate, String] with RootConnector {

  def book(date: String): Future[ResultSet] =
    insert.value(_.date, date).future()

  def getAll: Future[List[String]] =
    select.fetch()
  /*
    def delete(date: String): Future[ResultSet] =
      delete.where(_.date eqs date).future()*/

  def getOne(date: String): Future[Option[String]] =
    select.where(_.date eqs date).one

  object date extends StringColumn(this) with PartitionKey

}

class AppDatabase(val keyspace: CassandraConnection) extends Database[AppDatabase](keyspace) {

  object user extends User with Connector

  object schedule extends Schedule with Connector

  object knolSession extends KnolSession with Connector

  object holiday extends Holiday with Connector

  object sessionDate extends SessionDate with Connector
}
