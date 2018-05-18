package com.knoldus.service

import java.text.SimpleDateFormat

import com.knoldus.domains.{DisplaySchedule, SessionDetails, UpdateSessionDetails}
import com.knoldus.services.SessionService
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class SessionServiceSpec extends TestSuite {
  val sessionService = new SessionService
  val formatter = new SimpleDateFormat("yyyy/MM/dd")

  it should " not update the session" in {
    database.truncate(100.seconds)
    val scheduleDetails = SessionDetails("2018/05/18", "trainee", "technologyName", 1, "content", None)
    val futureRes =for{
    res <- sessionService.createSession(scheduleDetails)
      _ <- sessionService.updateSession(UpdateSessionDetails("2018/05/18", "2018/05/19"))
    } yield res
    futureRes.map(_ => assert(false)).recoverWith{
      case ex => assert(ex.getMessage === "Unable to update session")
    }
  }

  it should "create the session" in {
    database.truncate(100.seconds)
    val scheduleDetails = SessionDetails("2018/05/16", "trainee", "technologyName", 1, "content", None)
    val futureRes =for{
      res <- sessionService.createSession(scheduleDetails)
    } yield res
    futureRes.map(displaySchedule => assert(displaySchedule === DisplaySchedule("2018/05/16","2018/05/16","trainee","technologyName",1,"content",None)))
  }

  it should "create & update the session" in {
    database.truncate(100.seconds)
    val scheduleDetails = SessionDetails("2018/05/16", "trainee", "technologyName", 1, "content", None)
    val futureRes =for{
    _ <- sessionService.createSession(scheduleDetails)
     res <- sessionService.updateSession(UpdateSessionDetails("2018/05/16", "2018/05/17"))
    } yield res
    futureRes.map(displaySchedule => assert(displaySchedule === DisplaySchedule("2018/05/17","2018/05/17","trainee","technologyName",1,"content",None)))
  }

  it should "skip the weekdays while updating the session" in {
    database.truncate(100.seconds)
    val scheduleDetails = SessionDetails("2018/05/16", "trainee", "technologyName", 5, "content", None)
    val futureRes =for{
      _ <- sessionService.createSession(scheduleDetails)
      res <- sessionService.updateSession(UpdateSessionDetails("2018/05/16", "2018/05/18"))
    } yield res
    futureRes.map(displaySchedule => assert(displaySchedule === DisplaySchedule("2018/05/18","2018/05/22","trainee","technologyName",5,"content",None)))
  }
}
