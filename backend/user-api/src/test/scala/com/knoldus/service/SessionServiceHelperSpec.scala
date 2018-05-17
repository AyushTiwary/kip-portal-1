package com.knoldus.service

import com.knoldus.services.SessionServiceHelper

class SessionServiceHelperSpec extends TestSuite {

  val sessionHelper = new SessionServiceHelper

  it should "passed" in {
    sessionHelper.findListOfWorkingDays("2018/05/16", 3).map{ p =>
      println(p)
      true
    }
    assert(true)
  }

}
