import { Component, OnInit } from '@angular/core';
import 'fullcalendar';
import 'fullcalendar-scheduler';
import * as $ from 'jquery';
import {CalendarEvent, CreateSession, Session, UpdateDateRequest} from '../session';
import {SessionService} from '../session.service';
import * as moment from 'moment';

@Component({
  selector: 'app-list-session',
  templateUrl: './list-session.component.html',
  styleUrls: ['../session.component.css', './list-session.component.css']
})
export class ListSessionComponent implements OnInit {

  listOfSessions: Session[] = [];
  listOfCalendarEvents: CalendarEvent[] = [];
  showModal:boolean = false;
  createSession: CreateSession = { startDate: '',
    trainee: '',
    technologyName: '',
    numberOfDays: 0,
    content: '',
    assistantTrainer : ''
  };

  constructor(private sessionService: SessionService) {
  }

  close() {
    this.showModal = false;
  }

  create() {
    const endDate = this.createSession.startDate.split('-');
     this.createSession.startDate = `${endDate[0]}/${endDate[1]}/${endDate[2]}`;
    console.log(this.createSession);
    this.sessionService.createSession(this.createSession).subscribe( res => {
      console.log(res);
    })
  }

  ngOnInit() {

    this.getAllSessions();
  }

  getCalenderEvents() {
    const find = '/';
    const regex = new RegExp(find, 'g');
    this.listOfCalendarEvents = [];
    this.listOfSessions.map(session => {
      // const endDate = moment(session.endDate, 'YYYY/MM/DD').add(1, 'days').format('YYYY-MM-DD');
      this.listOfCalendarEvents.push({
        title: session.technologyName,
        start: session.startDate.replace(regex, '-'),
        end: moment(session.endDate, 'YYYY/MM/DD').add(1, 'days').format('YYYY-MM-DD')
      });
    });
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

  updateSession(prevDate: string, updatedDate: string) {

    const updateDateRequest: UpdateDateRequest = {
      previousDate: prevDate,
      updateDate: updatedDate
    };

    this.sessionService.updateSession(updateDateRequest).subscribe(res => {
      console.log(res.message);
      this.sessionService.getAllSessions().subscribe(result => {
        this.listOfSessions = result.data;
        this.getCalenderEvents();
        this.updateCalender();
      }, (error: any) => {
        console.error(error);
      });
    }, err => {
      console.error(err);
      this.sessionService.getAllSessions().subscribe(result => {
        this.listOfSessions = result.data;
        this.getCalenderEvents();
        this.updateCalender();
      }, (error: any) => {
        console.error(error);
      });
    });
  }

  renderCalendar() {
    $('#calendar').fullCalendar({
      defaultView: 'month',
      editable: true,
      dayClick: (date, jsEvent, view, resourceObj) => {
        this.showModal = true;
      },
      eventDrop: (event : any, delta, revertFunc) => {
        this.updateSession(moment(event.start._i).format('YYYY/MM/DD'), moment(event.start._d).format('YYYY/MM/DD'));
      },
      eventSources: [
        {
          // events: this.listOfCalendarEvents,
          events: (start, end, timezone, callback) => {
            callback(this.listOfCalendarEvents);
          },
          color: '#3a87ad',
          textColor: 'yellow'
        }
      ]
    });
  }

  updateCalender() {
    $('#calendar').fullCalendar('removeEvents');
    $('#calendar').fullCalendar( 'addEventSource', {events: this.listOfCalendarEvents} );
  }

}
