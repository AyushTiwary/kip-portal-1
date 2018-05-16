package com.knoldus.api

import org.scalatest.FunSuiteLike

class MailApiTest extends FunSuiteLike  {
  val mailApi = new MailApi
  val sendTo = List("anubhavtarar40@gmail.com")

  test("mail api should be able to send mail"){
    val actual = mailApi.sendMail(sendTo, "hi", "hi")
    assert(actual)
  }

}
