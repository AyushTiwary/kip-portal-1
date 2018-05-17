package com.knoldus.service

import java.text.SimpleDateFormat

import com.knoldus.domains.SessionDetails
import com.knoldus.services.SessionService

import scala.concurrent.ExecutionContext.Implicits.global

class SessionServiceSpec extends TestSuite {
  val sessionService = new SessionService
  val formatter = new SimpleDateFormat("yyyy/MM/dd")

  it should "create the session" in {
    val date = formatter.parse("2018/12/4")
    val scheduleDetails = SessionDetails("2018/12/4", "trainee", "technologyName", 1, "content", None)
    val res = sessionService.createSession(scheduleDetails)
    Thread.sleep(1000000)
    res.map(r => assert(r === scheduleDetails))
  }

}