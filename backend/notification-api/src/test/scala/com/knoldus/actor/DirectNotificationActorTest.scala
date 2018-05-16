package com.knoldus.actor

import scala.concurrent.Await
import scala.concurrent.duration._

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.knoldus.domains.UserDetails
import com.knoldus.exceptions.InvalidNotificationTypeException
import com.knoldus.service.MailServiceImpl
import org.scalatest.FunSuiteLike

class DirectNotificationActorTest
  extends TestKit(ActorSystem("TestSystem")) with FunSuiteLike with ImplicitSender {
  val mailService = new MailServiceImpl

  val directNotificationSenderActor = system.actorOf(Props(classOf[DirectNotificationActor],
    mailService))
  val userResponse = new UserDetails("anubhav.tarar@knoldus.in", "xyz")

  test("Should send sent as status when mail is sent") {
    implicit val timeout = Timeout(5 seconds)
    val msg = directNotificationSenderActor ? userResponse
    assert(Await.result(msg, 3 seconds).toString.equals("Sent"))
  }
}