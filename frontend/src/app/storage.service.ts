import { Injectable } from '@angular/core';
import {LoginResponse} from './user/user';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() { }

  setData(key: string, value: any): void {
    localStorage.setItem(key, JSON.stringify(value));
  }

  getData(): LoginResponse {
    return JSON.parse(localStorage.getItem('user'));
  }

  isLoggedIn() {
    return localStorage.getItem('user') ? true : false;
  }
}
