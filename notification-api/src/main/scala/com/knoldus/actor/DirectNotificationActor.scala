package com.knoldus.actor

import akka.actor.{Actor, ActorRef}
import com.knoldus.actor.Messages.{Failed, Sent}
import com.knoldus.domains.UserResponse
import com.knoldus.service.MailService
import com.knoldus.util.LoggerHelper
import akka.pattern.pipe
import com.knoldus.exceptions.InvalidNotificationTypeException


class DirectNotificationActor(mailService: MailService) extends Actor with LoggerHelper{
  val logger = getLogger(this.getClass)

  def receive: Receive = {
    case user:UserResponse=>

      logger.info("Notifier has Recieved the message ")
    if(mailService.sendMail(user.emailId.split(",").toList,"kip-portal",s"****Your temporary password for Kip is ***** ${user.passWord}")) {
      sender() ! Sent
    }
      else {
      sender() ! Failed
      }
     case _ =>
       logger.error("Invalid Notification type")
       throw new InvalidNotificationTypeException("Invalid Notification type")
  }
}

case object Messages{
  case object Sent
  case object Failed
}