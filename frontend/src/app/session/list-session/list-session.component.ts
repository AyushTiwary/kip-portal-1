import { Component, OnInit } from '@angular/core';
import 'fullcalendar';
import 'fullcalendar-scheduler';
import * as $ from 'jquery';
import {CalendarEvent, Session} from '../session';

@Component({
  selector: 'app-list-session',
  templateUrl: './list-session.component.html',
  styleUrls: ['../session.component.css', './list-session.component.css']
})
export class ListSessionComponent implements OnInit {

  listOfSessions: Session[] = [];
  listOfCalendarEvents: CalendarEvent[] = [];

  constructor() {
  }

  ngOnInit() {
    $('#calendar').fullCalendar({
      defaultView: 'month',
      dayClick: function () {
        alert('a day has been clicked!');
      },
      eventSources: [
        {
          events: [
            {
              title: 'event1',
              start: '2018-06-01'
            },
            {
              title: 'event2',
              start: '2018-06-05',
              end: '2018-06-09'
            }
          ],
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
        start: session.startDate.replace('/', '-'),
        end: session.endDate.replace('/', '-'),
      });
    });

  }
}
