import { Component, OnInit } from '@angular/core';
import 'fullcalendar';
import 'fullcalendar-scheduler';
import * as $ from 'jquery';
import {CalendarEvent, Session} from '../session';
import {SessionService} from "../session.service";

@Component({
  selector: 'app-list-session',
  templateUrl: './list-session.component.html',
  styleUrls: ['../session.component.css', './list-session.component.css']
})
export class ListSessionComponent implements OnInit {

  listOfSessions: Session[] = [];
  listOfCalendarEvents: CalendarEvent[] = [];

  constructor(private sessionService: SessionService) {
  }

  ngOnInit() {

    this.getAllSessions();

    $('#calendar').fullCalendar({
      defaultView: 'month',
      dayClick: function () {
        alert('a day has been clicked!');
      },
      eventSources: [
        {
          events: this.listOfCalendarEvents,
          color: 'black',
          textColor: 'yellow'
        }
      ]

    });
  }

  getCalenderEvents() {
    this.listOfSessions.map(session => {
      this.listOfCalendarEvents.push({
        title: session.technologyName,
        start: session.startDate.replace(/^()/, '-'),
        end: session.endDate.replace('/', '-'),
      });
    });

    console.log(this.listOfCalendarEvents);
  }

  getAllSessions() {
    this.sessionService.getAllSessions().subscribe(res => {
      this.listOfSessions = res.data;
      this.getCalenderEvents();
    }, err => {
      console.error(err);
    });
  }
}
