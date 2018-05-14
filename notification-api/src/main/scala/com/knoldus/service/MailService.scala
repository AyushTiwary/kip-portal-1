package com.knoldus.service

import com.knoldus.api.{MailApi, MailApiImpl}


trait MailService {

  val mailApi: MailApi

  def sendMail(recipients: List[String], subject: String, content: String): Boolean = {
    mailApi.sendMail(recipients, subject, content)
  }

}

class MailServiceImpl extends MailService {
  val mailApi: MailApi = MailApiImpl
}
