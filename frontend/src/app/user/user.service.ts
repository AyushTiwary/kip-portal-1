import { Injectable } from '@angular/core';
import {UserModule} from './user.module';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {catchError} from 'rxjs/internal/operators';
import {Observable} from 'rxjs/index';
import {User} from './user';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  loginURL: string;
  signupURL: string;

  constructor(private http: HttpClient) {
    this.loginURL = environment.apiEndPoint + 'kip/login';
    this.signupURL = environment.apiEndPoint + 'kip/createuser';
  }

  login(user: User) {
    return this.http.post(this.loginURL, user)
      .pipe(catchError(err => this.handleError(err)));
  }

  signup(emailId: string) {
    return this.http.post(this.signupURL, {emailId})
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
    return Observable.throw(
      errMsg);
  }

}
