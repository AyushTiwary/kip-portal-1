package com.knoldus.responses

import com.knoldus.carts.ResponseCart
import com.knoldus.util.JsonHelper

object ErrorResponses extends JsonHelper {

  lazy val INTERNAL_SERVER_ERROR = write(ResponseCart(message = Some("There was an internal server error.")))
  lazy val BAD_REQUEST_STD = write(ResponseCart(message = Some("Bad request, Error found in data")))
  lazy val BAD_REQUEST_FOR_POST = write(ResponseCart(message = Some("Bad request, Please try with post call")))
  lazy val BAD_REQUEST_FOR_PUT = write(ResponseCart(message = Some("Bad request, Please try with put call")))
  lazy val BAD_REQUEST_FOR_DATA = write(ResponseCart(message = Some("Bad request, Error found in data")))
  lazy val BAD_REQUEST_FILE_NOT_FOUND = write(ResponseCart(message = Some("File not found")))
  lazy val BAD_REQUEST_INVALID_JSON = write(ResponseCart(message = Some("Request JSON was invalid")))
  lazy val FORBIDDEN = write(ResponseCart(message = Some("Forbidden")))

  def getJsonDataTypeError(msg: String): String = write(ResponseCart(message = Some(msg)))

  def OK_PARSE(data: AnyRef): String = write(ResponseCart(data = Some(parse(write(data)))))

  def OK_MSG(msg: String): String = write(ResponseCart(message = Some(msg)))

  def BAD_REQUEST(msg: String): String = write(ResponseCart(message = Some(msg)))

  def BAD_REQUEST_WITH_ERROR_CODE(errorCode: Int, msg: String): String = write(ResponseCart(message = Some(msg)))
}
