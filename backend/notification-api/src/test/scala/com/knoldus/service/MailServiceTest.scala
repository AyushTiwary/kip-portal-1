package com.knoldus.service

import org.scalatest.FunSuiteLike

class MailServiceTest extends FunSuiteLike {
val mailService = new MailServiceImpl
  val sendTo = List("anubhavtarar40@gmail.com")

  test("Mail service should be able to send mail"){
    assert(mailService.sendMail(sendTo,"hi","hi"))
  }
}
