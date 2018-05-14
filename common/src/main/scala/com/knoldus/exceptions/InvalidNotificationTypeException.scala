package com.knoldus.exceptions

import com.knoldus.exception.Kip_Portal_Exception

case class InvalidNotificationTypeException(msg:String) extends Kip_Portal_Exception(msg)
