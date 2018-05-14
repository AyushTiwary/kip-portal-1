package com.knoldus.carts

import org.json4s._

case class ResponseCart(data: Option[JValue] = None, message: Option[String] = None)
