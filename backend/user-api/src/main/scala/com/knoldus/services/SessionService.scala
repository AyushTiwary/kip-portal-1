package com.knoldus.services

import com.knoldus.domains.{DisplaySchedule, ScheduleInfo, SessionDetails}
import com.typesafe.config.ConfigFactory
import model.PortalDataBase

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionService {
  val config = ConfigFactory.load()
  val appDatabase = new PortalDataBase(config).getClusterDB
  val sessionHelper = new SessionServiceHelper

  def createSession(sessionDetails: SessionDetails): Future[DisplaySchedule] = {
    val sessionId = sessionDetails.technologyName + "-" + sessionHelper.parseDateToDateString(sessionDetails.startDate)
    val numberOfDays = sessionDetails.numberOfDays
    val startDate = sessionHelper.parseDateToDateString(sessionDetails.startDate)
    val endDate = sessionHelper.addDaysToDate(startDate, numberOfDays)
    for {
      canCreate <- sessionHelper.checkDatesToCreateSession(startDate, numberOfDays)
      displaySchedule <- if (canCreate) {
        for {
          _ <- appDatabase.knolSession.createSession(sessionId, startDate)
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

  //Todo(ayush) add logic for updating the session
  def updateSession(previousDate: String, updateDate: String): Future[DisplaySchedule] = {
    Future.successful(DisplaySchedule(previousDate, updateDate, "trainee", "technologyName", 4, "content", None))
  }
  /*{
    for {
      sessionInfoLIst <- appDatabase.knolSession.getAll
      _ <- Future.sequence(sessionInfoLIst.map { sessionInfo =>
        val preSessionDate = sessionInfo.startDate
        if (sessionHelper.isDateAfter(preSessionDate, previousDate)) {
          for {
            dateToUpdate <- sessionHelper.nextAvailableDate(preSessionDate)
            _ <- updateStoreForSession(preSessionDate, dateToUpdate)
          } yield dateToUpdate
        }
        else {
          Future.failed(new Exception("Unable to update the session"))
        }
      })
      updateScheduleInfoList <- appDatabase.schedule.getAll
    } yield updateScheduleInfoList
  }*/

  private def updateStoreForSession(previousDate: String, updateDate: String): Future[String] = for {
    mayBeSessionInfo <- appDatabase.knolSession.getSessionByDate(previousDate)
    sessionInfoForUpdate <- mayBeSessionInfo match {
      case Some(session) => Future.successful(session)
      case None => Future.failed(new Exception(s"Session does not exist for date : $previousDate"))
    }
    _ <- appDatabase.schedule.updateScheduleDate(sessionInfoForUpdate.sessionId, updateDate)
    _ <- appDatabase.knolSession.deleteSession(sessionInfoForUpdate.startDate)
    _ <- appDatabase.knolSession.createSession(sessionInfoForUpdate.sessionId, updateDate)
  } yield sessionInfoForUpdate.startDate

}
