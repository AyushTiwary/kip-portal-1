import { Component, OnInit } from '@angular/core';
import 'fullcalendar';
import 'fullcalendar-scheduler';
import * as $ from 'jquery';
import {CalendarEvent, Session, UpdateDateRequest} from '../session';
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
  }

  getCalenderEvents() {
    const find = '/';
    const regex = new RegExp(find, 'g');

    this.listOfSessions.map(session => {

      const endDate = session.endDate.split('/');
      const finalEndDate = `${endDate[0]}-${endDate[1]}-${(+endDate[2] + 1)}`;
      this.listOfCalendarEvents.push({
        title: session.technologyName,
        start: session.startDate.replace(regex, '-'),
        end: finalEndDate
      });
    });
    console.log(this.listOfCalendarEvents);
  }

  getAllSessions() {
    this.sessionService.getAllSessions().subscribe(res => {
      this.listOfSessions = res.data;
      this.getCalenderEvents();
      this.renderCalendar();
    }, err => {
      console.error(err);
    });
  }

  updateSession(event: CalendarEvent) {
    // const request: UpdateDateRequest = {
    //   previousDate: event.,
    //   updateDate: ''
    // };

    // this.sessionService.updateSession().subscribe(res => {
    //   this.listOfSessions = res.data;
    //   this.getCalenderEvents();
    //   this.renderCalendar();
    // }, err => {
    //   console.error(err);
    // });
  }

  renderCalendar() {
    $('#calendar').fullCalendar({
      defaultView: 'month',
      editable: true,
      dayClick: function () {
        alert('a day has been clicked!');
      },
      eventDrop: (event, delta, revertFunc) => {
        console.log(event)
        this.updateSession(event);
      },
      eventDragStart: (event, delta, revertFunc) => {
        console.log(event);
        this.updateSession(event);
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



}
