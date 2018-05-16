package com.knoldus.util

import org.slf4j.{Logger, LoggerFactory}

trait LoggerHelper {
  def getLogger(clazz: Class[_]): Logger = {
    LoggerFactory.getLogger(clazz)
  }
}