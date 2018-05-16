package com.knoldus.utils

import com.typesafe.config.ConfigFactory

object NotificationConfigReader {
  val conf = ConfigFactory.load()
  val email = conf.getString("user.email")
  val password = conf.getString("user.password")

}
