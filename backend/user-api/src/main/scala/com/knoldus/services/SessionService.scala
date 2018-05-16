package com.knoldus.services

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.datastax.driver.core.ResultSet
import com.knoldus.domains.{ScheduleInfo, SessionDetails}
import com.knoldus.util.LoggerHelper
import com.typesafe.config.ConfigFactory
import model.{HolidayDetail, PortalDataBase}

import scala.concurrent.Future

class SessionService extends LoggerHelper {
  val config = ConfigFactory.load()
  val appDatabase = new PortalDataBase(config).getClusterDB
  val logger = getLogger(this.getClass)


  def createSession(sessionDetails: SessionDetails): Future[ResultSet] = {
    val sessionId = sessionDetails.technologyName + "-" + sessionDetails.date
    val dateStr = parseDateToDateString(sessionDetails.date)
    for {
      _ <- appDatabase.session.createSession(sessionId, dateStr)
      scheduleInfo = ScheduleInfo(sessionId, dateStr, sessionDetails.trainee, sessionDetails.technologyName,
        sessionDetails.numberOfDays, sessionDetails.content, sessionDetails.assistantTrainer)
      resultSet <- appDatabase.schedule.createSchedule(scheduleInfo)
    } yield resultSet
  }

  def updateSession(previousDate: String, updateDate: String): Future[List[ScheduleInfo]] ={
    for{
      sessionInfoLIst <- appDatabase.session.getAll()
      _ <- Future.sequence(sessionInfoLIst.map{ sessionInfo =>
        val preSessionDate = sessionInfo.date
        if(isDateAfter(preSessionDate, previousDate)){
         for{
           dateToUpdate <- nextAvailableDate(preSessionDate)
           _ <- updateStoreForSession(preSessionDate, dateToUpdate)
         } yield dateToUpdate
        }
        else {
          Future.failed(new Exception("unable to update the session"))

        }
      })
      updateScheduleInfoList <- appDatabase.schedule.getAll()
    } yield updateScheduleInfoList
  }

  def updateStoreForSession(previousDate: String, updateDate: String): Future[String] = {
    for{
      mayBeSessionInfo <- appDatabase.session.getSessionByDate(previousDate)
      sessionInfoForUpdate <- mayBeSessionInfo match {
        case Some(session) => Future.successful(session)
        case None => Future.failed(new Exception(s"Session does not exist for date : $previousDate"))
      }
      _ <- appDatabase.schedule.updateScheduleDate(sessionInfoForUpdate.sessionId, updateDate)
      _ <- appDatabase.session.deleteSession(sessionInfoForUpdate.date)
      _ <- appDatabase.session.createSession(sessionInfoForUpdate.sessionId, updateDate)
    } yield sessionInfoForUpdate.date
  }

  //Todo(ayush) make nextAvailableDate tailrecursive
  def nextAvailableDate(currentDateString: String): Future[String] = {
    for {
      isHoliday <- isHoliday(currentDateString)
      nextAvailableDate <- if(isHoliday || isWeekend(currentDateString)){
        nextAvailableDate(currentDateString)
      }
      else {
        Future.successful(currentDateString)
      }
    } yield nextAvailableDate
  }

  private def isDateAfter(preDate: String, dateToCheck: String): Boolean = {
    parseDateStringToDate(dateToCheck).after(parseDateStringToDate(preDate))
  }

  private def isWeekend(dateString: String): Boolean = {
    val date = parseDateStringToDate(dateString)
    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    Calendar.SUNDAY == dayOfWeek || Calendar.SATURDAY == dayOfWeek
  }

  private def isHoliday(dateString: String): Future[Boolean] = {
      appDatabase.holiday.getHoliday(dateString).map{
        case Some(_: HolidayDetail) => true
        case None => false
      }
  }

  private def addDaytoDate(dateString: String): String = {
    val calendar = Calendar.getInstance()
    calendar.setTime(parseDateStringToDate(dateString))
    calendar.add(Calendar.DATE, 1)
    parseDateToDateString(calendar.getTime)
  }

  private def parseDateToDateString(date: Date): String = {
    val formatter = new SimpleDateFormat("yyyy/MM/dd")
    formatter.format(date)
  }

  private def parseDateStringToDate(dateString: String): Date = {
    val formatter = new SimpleDateFormat("yyyy/MM/dd")
    formatter.parse(dateString)
  }
}
