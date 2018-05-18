package com.knoldus.services

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.datastax.driver.core.ResultSet
import com.typesafe.config.ConfigFactory
import model.PortalDataBase

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionServiceHelper {
  val config = ConfigFactory.load()
  val appDatabase = new PortalDataBase(config).getClusterDB

  //Todo(ayush) make nextAvailableDate tail recursive
  def nextAvailableDate(currentDateString: String): String = {
    val nextDate = addDayToDate(currentDateString)
    if (isDateAvailable(nextDate)) nextDate
    else nextAvailableDate(nextDate)
  }

  def checkDatesToCreateSession(startDate: String, numberOfDays: Int): Future[Boolean] = {
    val futureResponses = Future.sequence {
      createListForDate(startDate, numberOfDays).map { dateStr =>
        appDatabase.sessionDate.getOne(dateStr).map {
          case Some(_: String) => false
          case None => true
        }
      }
    }
    futureResponses.map(_.forall(res => res))
  }

  def createListForDate(startDate: String, numberOfDays: Int): List[String] = {
    startDate :: (1 until numberOfDays).toList.map { day =>
      val dateStr = addDaysToDate(startDate, day)
      dateStr
    }
  }

  def isDateAfter(preDate: String, dateToCheck: String): Boolean = {
    parseDateStringToDate(dateToCheck).after(parseDateStringToDate(preDate))
  }

  def isDateAvailable(date: String): Boolean = {
    if (isHoliday(date) || isWeekend(date)) false
    else true
  }

  def isWeekend(dateString: String): Boolean = {
    val date = parseDateStringToDate(dateString)
    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    Calendar.SUNDAY == dayOfWeek || Calendar.SATURDAY == dayOfWeek
  }

  def isHoliday(dateString: String): Boolean = {
    getHoliday.contains(dateString)
  }

  def getHoliday: List[String] = {
    val holidays = List.empty[String]
    waitToComplete[List[String]](appDatabase.holiday.getAllDates.map(holidayList => holidays ::: holidayList))
    holidays
  }

  @tailrec
  private def waitToComplete[T](res: Future[T]): Boolean = {
    if (res.isCompleted) true
    else waitToComplete(res)
  }

  def parseDateToDateString(date: Date): String = {
    val formatter = new SimpleDateFormat("yyyy/MM/dd")
    formatter.format(date)
  }

  def parseDateStringToDate(dateString: String): Date = {
    val formatter = new SimpleDateFormat("yyyy/MM/dd")
    formatter.parse(dateString)
  }

  def getNumberOfDaysBetweenDates(startDate: String, endDate: String): Int = {
    (parseDateStringToDate(endDate).getTime - parseDateStringToDate(startDate).getTime) / (1000 * 60 * 60 * 24)
  }.toInt

  def addDaysToDate(dateString: String, numberOfDays: Int): String = {
    val calendar = Calendar.getInstance()
    calendar.setTime(parseDateStringToDate(dateString))
    calendar.add(Calendar.DATE, numberOfDays)
    parseDateToDateString(calendar.getTime)
  }

  def addDayToDate(dateString: String): String = {
    addDaysToDate(dateString, 1)
  }
}
