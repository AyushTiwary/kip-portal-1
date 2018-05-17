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
    val endDate = sessionHelper.addDaysToDate(startDate, numberOfDays - 1)
    for {
      canCreate <- sessionHelper.checkDatesToCreateSession(startDate, numberOfDays)
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
    val updateDate = updateSessionDetails.updateDate
    val previousDate = updateSessionDetails.previousDate
    val numberOfDays = sessionHelper.getNumberOfDays(previousDate, updateDate)
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

  private def updateStoreForSession(previousDate: String, updateDate: String): Future[String] = for {
    mayBeSessionInfo <- appDatabase.knolSession.getSessionByDate(previousDate)
    sessionInfoForUpdate <- mayBeSessionInfo match {
      case Some(session) => Future.successful(session)
      case None => Future.failed(new Exception(s"Session does not exist for date : $previousDate"))
    }
    //_ <- appDatabase.schedule.updateScheduleDate(sessionInfoForUpdate.sessionId, updateDate)
    _ <- appDatabase.knolSession.deleteSession(sessionInfoForUpdate.startDate)
    // _ <- appDatabase.knolSession.createSession(sessionInfoForUpdate.sessionId, updateDate)
  } yield sessionInfoForUpdate.startDate

}
