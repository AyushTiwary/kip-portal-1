package com.knoldus.services

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.typesafe.config.ConfigFactory
import model.{HolidayDetail, PortalDataBase}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionServiceHelper {
  val config = ConfigFactory.load()
  val appDatabase = new PortalDataBase(config).getClusterDB

  //Todo(ayush) make nextAvailableDate tail recursive
  def nextAvailableDate(currentDateString: String): Future[String] = {
    for {
      isHoliday <- isHoliday(currentDateString)
      nextAvailableDate <- if (isHoliday || isWeekend(currentDateString)) {
        nextAvailableDate(addDayToDate(currentDateString))
      }
      else {
        Future.failed(new Exception("date is not available"))
      }
    } yield nextAvailableDate
  }

  def checkDatesToCreateSession(startDate: String, numberOfDays: Int): Future[Boolean] = {
    val futureResponses = Future.sequence{
      createListForDate(startDate, numberOfDays).map{ dateStr =>
        appDatabase.sessionDate.getOne(dateStr).map{
          case Some(_: String) => false
          case None => true
        }}}
    futureResponses.map(_.forall(res => res))
  }

  def createListForDate(startDate: String, numberOfDays: Int): List[String] = {
    startDate :: (1 until numberOfDays).toList.map { day =>
      addDaysToDate(startDate, day)
    }
  }

  def isDateAfter(preDate: String, dateToCheck: String): Boolean = {
    parseDateStringToDate(dateToCheck).after(parseDateStringToDate(preDate))
  }

  def isDateAvailable(date: String): Future[Boolean] =
    for {
      isHoliday <- isHoliday(date)
      isAvailable = if (isHoliday || isWeekend(date)) true
      else false
    } yield isAvailable

  def isWeekend(dateString: String): Boolean = {
    val date = parseDateStringToDate(dateString)
    val calendar = Calendar.getInstance()
    calendar.setTime(date)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    Calendar.SUNDAY == dayOfWeek || Calendar.SATURDAY == dayOfWeek
  }

  def isHoliday(dateString: String): Future[Boolean] = {
    appDatabase.holiday.getHoliday(dateString).map {
      case Some(_: HolidayDetail) => true
      case None => false
    }
  }

  def parseDateToDateString(date: Date): String = {
    val formatter = new SimpleDateFormat("yyyy/MM/dd")
    formatter.format(date)
  }

  def parseDateStringToDate(dateString: String): Date = {
    val formatter = new SimpleDateFormat("yyyy/MM/dd")
    formatter.parse(dateString)
  }

  def getNumberOfDays(startDate: String, endDate: String): Int ={
    (parseDateStringToDate(endDate).getTime - parseDateStringToDate(startDate).getTime) / (1000 * 60 * 60 * 24)
  }.toInt

/*  def getEndDate(startDate: String, dayCount: Int) ={

  }*/

/*  def findListOfWorkingDays(startDate: String, numberOfDays: Int)={
    var days = 1
    var datesStr = ListBuffer(startDate)
    while(days != numberOfDays){
      nextAvailableDate(datesStr.last).map{ date =>
        datesStr.+=:(date)
      }
      days = days + 1
    }
    datesStr.toList
  }*/

/*  def addDaysToDate1(str: String, i: Int) ={
    (0 until(i)).toList.map{ _ =>
      nextAvailableDate(str)
    }
  }*/

  def addDaysToDate(dateString: String, numberOfDays: Int): String = {
    val calendar = Calendar.getInstance()
    calendar.setTime(parseDateStringToDate(dateString))
    calendar.add(Calendar.DATE, numberOfDays)
    parseDateToDateString(calendar.getTime)
  }

  def addDayToDate(dateString: String): String = {
    addDaysToDate(dateString, 1)
  }

/*  def addBookDates(startDate: String, numberOfDays: Int): Future[ResultSet] = {
    if (numberOfDays > 1) {
      for {
        _ <- appDatabase.sessionDate.book(startDate)
        nextAvailableDate <- nextAvailableDate(startDate)
        res <- addBookDates(nextAvailableDate, numberOfDays - 1)
      } yield res
    }
    else {
      appDatabase.sessionDate.book(startDate)
    }
  }*/
}
