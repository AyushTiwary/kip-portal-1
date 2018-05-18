package com.knoldus.services

import com.datastax.driver.core.ResultSet
import com.knoldus.domains._
import com.typesafe.config.ConfigFactory
import model.PortalDataBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionService {
  val config = ConfigFactory.load()
  val appDatabase = new PortalDataBase(config).getClusterDB
  val sessionHelper = new SessionServiceHelper

  def getMonthlySessionInfos(month: Int, year: Int): Future[List[SessionInfo]] = for{
    sessionInfoList <- appDatabase.knolSession.getAll
    sessionInfo = sessionInfoList.flatMap{sessionInfo =>
      val dateStr = sessionInfo.startDate
      val splitDate = dateStr.split("/")
      val getYear = splitDate(0).toInt
      val getMonth = splitDate(1).toInt
      if(getMonth == month && getYear == year) List(sessionInfo)
      else List.empty[SessionInfo]
    }} yield sessionInfo

  def addHoliday(holidayInfo: HolidayInfo): Future[ResultSet] =
    appDatabase.holiday.createHoliday(holidayInfo.date, holidayInfo.content)

  def createSession(sessionDetails: SessionDetails): Future[DisplaySchedule] = {
    val sessionId = sessionDetails.technologyName + "-" + sessionHelper.parseDateStringToDate(sessionDetails.startDate)
    val startDate = sessionHelper.parseDateToDateString(sessionHelper.parseDateStringToDate(sessionDetails.startDate))
    if (sessionHelper.isDateAvailable(startDate)) {
      val numberOfDays = sessionDetails.numberOfDays
      val calculativeEndDate = sessionHelper.addDaysToDate(startDate, numberOfDays - 1)
      val endDate = if (sessionHelper.isDateAvailable(calculativeEndDate)) calculativeEndDate
      else sessionHelper.nextAvailableDate(calculativeEndDate)
      for {
        canCreate <- sessionHelper.checkDatesToCreateSession(startDate, sessionHelper.getNumberOfDaysBetweenDates(startDate, endDate))
        _ <- Future.sequence(sessionHelper.createListForDate(startDate, numberOfDays).map(dateStr => appDatabase.sessionDate.book(dateStr)))
        displaySchedule <- if (canCreate) {
          for {
            _ <- appDatabase.knolSession.createSession(sessionId, startDate, numberOfDays)
            scheduleInfo = ScheduleInfo(sessionId, startDate, sessionDetails.trainee, sessionDetails.technologyName,
              sessionDetails.numberOfDays, sessionDetails.content, sessionDetails.assistantTrainer)
            _ <- appDatabase.schedule.createSchedule(scheduleInfo)
            displaySchedule = DisplaySchedule(startDate, endDate, sessionDetails.trainee, sessionDetails.technologyName,
              sessionDetails.numberOfDays, sessionDetails.content, sessionDetails.assistantTrainer)
          } yield displaySchedule
        } else {
          Future.failed(new Exception("dates are not available for creating the session"))
        }
      } yield displaySchedule
    } else {
      Future.failed(new Exception(" choose some other starting date"))
    }
  }

  def getAllSession: Future[List[DisplaySchedule]] = {
    appDatabase.schedule.getAll.map { sessionDetailsList =>
      sessionDetailsList.map { sessionDetails =>
        val startDate = sessionDetails.startDate
        val numberOfDays = sessionDetails.numberOfDays
        val calculativeEndDate = sessionHelper.addDaysToDate(startDate, numberOfDays - 1)
        val endDate = if (sessionHelper.isDateAvailable(calculativeEndDate)) calculativeEndDate
        else sessionHelper.nextAvailableDate(calculativeEndDate)
        DisplaySchedule(startDate, endDate, sessionDetails.trainee, sessionDetails.technologyName,
          sessionDetails.numberOfDays, sessionDetails.content, sessionDetails.assistantTrainer)
      }
    }
  }

  //Todo(ayush) verify it using the test case
  def updateSession(updateSessionDetails: UpdateSessionDetails): Future[DisplaySchedule] = {
    val updateDate = updateSessionDetails.updateDate
    val previousDate = updateSessionDetails.previousDate
    if (sessionHelper.isDateAvailable(updateDate) && sessionHelper.isDateAvailable(previousDate)) {
      for {
        _ <- updateAllSession(updateSessionDetails.previousDate, sessionHelper.getNumberOfDaysBetweenDates(updateSessionDetails.previousDate, updateSessionDetails.updateDate))
        displaySchedule <- update(updateSessionDetails)
      } yield displaySchedule
    } else {
      Future.failed(new Exception("Unable to update session"))
    }
  }.recoverWith {
    case _ => Future.failed(new Exception("Unable to update session"))
  }

  def update(updateSessionDetails: UpdateSessionDetails): Future[DisplaySchedule] = {
    val updateDate = updateSessionDetails.updateDate
    val previousDate = updateSessionDetails.previousDate
    if (sessionHelper.isDateAvailable(updateDate) && sessionHelper.isDateAvailable(previousDate)) {
      val numberOfDays = sessionHelper.getNumberOfDaysBetweenDates(previousDate, updateDate)
      for {
        maybeSessionInfo <- appDatabase.knolSession.getSessionByDate(previousDate)
        sessionInfo <- maybeSessionInfo.fold[Future[SessionInfo]](Future.failed(new Exception("Unable to get session")))(info => Future.successful(info))
        _ <- appDatabase.knolSession.deleteSession(previousDate)
        _ <- appDatabase.knolSession.createSession(sessionInfo.sessionId, updateDate, sessionInfo.numberOfDays)
        _ <- appDatabase.schedule.updateScheduleDate(sessionInfo.sessionId, updateDate)
        mayBeNewScheduleInfo <- appDatabase.schedule.getScheduleBySessionId(sessionInfo.sessionId)
        newScheduleInfo <- mayBeNewScheduleInfo.fold[Future[ScheduleInfo]](Future.failed(new Exception("Unable to get schedule")))(info => Future.successful(info))
        newEndDate = sessionHelper.addDaysToDate(previousDate, numberOfDays + sessionInfo.numberOfDays - 1)
        displaySchedule = DisplaySchedule(updateDate, if (sessionHelper.isDateAvailable(newEndDate)) newEndDate else sessionHelper.nextAvailableDate(newEndDate),
          newScheduleInfo.trainee, newScheduleInfo.technologyName, newScheduleInfo.numberOfDays, newScheduleInfo.content, newScheduleInfo.assistantTrainer)
      } yield displaySchedule
    } else {
      Future.failed(new Exception("Unable to update session"))
    }
  }

  private def updateAllSession(previousDate: String, daysCount: Int): Future[List[DisplaySchedule]] = for {
    sessionInfoList <- appDatabase.knolSession.getAll
    displayScheduleList <- Future.sequence(sessionInfoList.map { sessionInfo =>
      if (sessionHelper.parseDateStringToDate(sessionInfo.startDate).after(sessionHelper.parseDateStringToDate(previousDate))) {
        val preDate = sessionInfo.startDate
        val calculativeUpdateDate = sessionHelper.addDaysToDate(preDate, daysCount - 1)
        val updateDate = if (sessionHelper.isDateAvailable(calculativeUpdateDate)) calculativeUpdateDate else sessionHelper.nextAvailableDate(calculativeUpdateDate)
        update(UpdateSessionDetails(preDate, updateDate))
      } else {
        for {
          mayBeNewScheduleInfo <- appDatabase.schedule.getScheduleBySessionId(sessionInfo.sessionId)
          newScheduleInfo <- mayBeNewScheduleInfo.fold[Future[ScheduleInfo]](Future.failed(new Exception("Unable to get schedule")))(info => Future.successful(info))
          preDate = sessionInfo.startDate
          calculativeUpdateDate = sessionHelper.addDaysToDate(preDate, daysCount - 1)
          updateDate = if (sessionHelper.isDateAvailable(calculativeUpdateDate)) calculativeUpdateDate else sessionHelper.nextAvailableDate(calculativeUpdateDate)
          displaySchedule = DisplaySchedule(updateDate, sessionHelper.addDaysToDate(previousDate, sessionInfo.numberOfDays), newScheduleInfo.trainee, newScheduleInfo.technologyName,
            newScheduleInfo.numberOfDays, newScheduleInfo.content, newScheduleInfo.assistantTrainer)
        } yield displaySchedule
      }
    })
  } yield displayScheduleList
}
