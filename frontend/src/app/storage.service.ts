import { Injectable } from '@angular/core';
import {LoginResponse} from './user/user';

declare var toastr:any;

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  constructor() {
    toastr.options = {
      "closeButton": true,
      "debug": false,
      "newestOnTop": false,
      "progressBar": false,
      "positionClass": "toast-top-right",
      "preventDuplicates": true,
      "onclick": null,
      "showDuration": "300",
      "hideDuration": "1000",
      "timeOut": "2500",
      "extendedTimeOut": "1000",
      "showEasing": "swing",
      "hideEasing": "linear",
      "showMethod": "fadeIn",
      "hideMethod": "fadeOut"
    }
  }

  setData(key: string, value: any): void {
    localStorage.setItem(key, JSON.stringify(value));
  }

  removeData(key: string): void {
    localStorage.removeItem(key);
  }

  getData(): LoginResponse {
    return JSON.parse(localStorage.getItem('user'));
  }

  isLoggedIn() {
    return localStorage.getItem('user') ? true : false;
  }
}
