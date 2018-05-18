import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError} from 'rxjs/internal/operators';
import {Observable} from 'rxjs/index';

@Injectable({
  providedIn: 'root'
})
export class SessionService {

  getAllSessionsURL: string;

  constructor(private http: HttpClient) {
    this.getAllSessionsURL = environment.apiEndPoint + 'kip/getallsessions';
  }

  getAllSessions() {
    return this.http.get(this.getAllSessionsURL)
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
