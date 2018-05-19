import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError} from 'rxjs/internal/operators';
import {Observable} from 'rxjs/index';
import {CreateSession, UpdateDateRequest} from "./session";
import {LoginResponse} from "../user/user";

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  getAllSessionsURL: string;
  getUsersURL: string;
  updateSessionURL: string;
  changetypeURL: string;
  createSessionURL: string;

  constructor(private http: HttpClient) {
    this.getAllSessionsURL = environment.apiEndPoint + 'kip/getallsessions';
    this.updateSessionURL = environment.apiEndPoint + 'kip/updateSession';
    this.createSessionURL = environment.apiEndPoint + 'kip/createsession';
    this.getUsersURL = environment.apiEndPoint + 'kip/user';
    this.changetypeURL = environment.apiEndPoint + 'kip/changeusertype';
  }

  createSession(createSessionData: CreateSession) {
    return this.http.post(this.createSessionURL, createSessionData)
      .pipe(catchError(err => this.handleError(err)));
  }

  getAllSessions() {
    return this.http.get(this.getAllSessionsURL)
      .pipe(catchError(err => this.handleError(err)));
  }

  getUsers(emailId: string) {
    return this.http.post(this.getUsersURL, {emailId})
      .pipe(catchError(err => this.handleError(err)));
  }

  updateSession(requestData: UpdateDateRequest) {
    return this.http.post(this.updateSessionURL, requestData)
      .pipe(catchError(err => this.handleError(err)));
  }

  updateType(requestData: LoginResponse) {
    return this.http.post(this.changetypeURL, requestData)
      .pipe(catchError(err => this.handleError(err)));
  }

  private handleError(error: HttpErrorResponse): Observable<any> {
    console.log(error);
    let errMsg: string;
    if (error.error instanceof ErrorEvent) {
      errMsg = 'An error occurred:' + error.message;
    } else {
      errMsg = `Backend returned code ${error.status}, body was: ${error.error}`;
    }
    console.error(errMsg);
    return Observable.throw(errMsg);
  }
}
