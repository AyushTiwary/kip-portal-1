package com.knoldus.services

import com.knoldus.domains.{DisplaySchedule, ScheduleInfo, SessionDetails, UpdateSessionDetails, _}
import com.knoldus.util.LoggerHelper
import com.typesafe.config.ConfigFactory
import model.PortalDataBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionService extends LoggerHelper {
  val config = ConfigFactory.load()
  val appDatabase = new PortalDataBase(config).getClusterDB
  val sessionHelper = new SessionServiceHelper
  val loggerHelper = getLogger(this.getClass)

  def createSession(sessionDetails: SessionDetails): Future[DisplaySchedule] = {
    val sessionId = sessionDetails.technologyName + "-" + sessionHelper.parseDateStringToDate(sessionDetails.startDate)
    val numberOfDays = sessionDetails.numberOfDays
    val startDate = sessionHelper.parseDateToDateString(sessionHelper.parseDateStringToDate(sessionDetails.startDate))
    loggerHelper.info("->" + sessionDetails)
    val calculativeEndDate = sessionHelper.addDaysToDate(startDate, numberOfDays - 1)
    val endDate = if (sessionHelper.isDateAvailable(calculativeEndDate)) calculativeEndDate
    else sessionHelper.nextAvailableDate(calculativeEndDate)
    for {
      canCreate <- sessionHelper.checkDatesToCreateSession(startDate, sessionHelper.getNumberOfDaysBetweenDates(startDate, endDate))
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
  }

  def getAllSession: Future[List[DisplaySchedule]] = {
    appDatabase.schedule.getAll.map { sessionDetailsList =>
      sessionDetailsList.map { sessionDetails =>
        val startDate = sessionDetails.startDate
        val endDate = sessionHelper.addDaysToDate(startDate, sessionDetails.numberOfDays)
        DisplaySchedule(startDate, endDate, sessionDetails.trainee, sessionDetails.technologyName,
          sessionDetails.numberOfDays, sessionDetails.content, sessionDetails.assistantTrainer)
      }
    }
  }

  //Todo(ayush) verify it using the test case
  def updateSession(updateSessionDetails: UpdateSessionDetails): Future[DisplaySchedule] = {
    for {
      _ <- updateAllSession(updateSessionDetails.previousDate, sessionHelper.getNumberOfDaysBetweenDates(updateSessionDetails.previousDate, updateSessionDetails.updateDate))
      displaySchedule <- update(updateSessionDetails)
    } yield displaySchedule
  }

  def update(updateSessionDetails: UpdateSessionDetails): Future[DisplaySchedule] = {
    val updateDate = updateSessionDetails.updateDate
    val previousDate = updateSessionDetails.previousDate
    val numberOfDays = sessionHelper.getNumberOfDaysBetweenDates(previousDate, updateDate)
    for {
      maybeSessionInfo <- appDatabase.knolSession.getSessionByDate(previousDate)
      sessionInfo <- maybeSessionInfo.fold[Future[SessionInfo]](Future.failed(new Exception("Unable to get session")))(info => Future.successful(info))
      _ <- appDatabase.knolSession.deleteSession(previousDate)
      _ <- appDatabase.knolSession.createSession(sessionInfo.sessionId, updateDate, sessionInfo.numberOfDays)
      _ <- appDatabase.schedule.updateScheduleDate(sessionInfo.sessionId, updateDate)
      mayBeNewScheduleInfo <- appDatabase.schedule.getScheduleBySessionId(sessionInfo.sessionId)
      newScheduleInfo <- mayBeNewScheduleInfo.fold[Future[ScheduleInfo]](Future.failed(new Exception("Unable to get schedule")))(info => Future.successful(info))
      displaySchedule = DisplaySchedule(updateDate, sessionHelper.addDaysToDate(previousDate, sessionInfo.numberOfDays + numberOfDays), newScheduleInfo.trainee, newScheduleInfo.technologyName,
        newScheduleInfo.numberOfDays, newScheduleInfo.content, newScheduleInfo.assistantTrainer)
    } yield displaySchedule
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
