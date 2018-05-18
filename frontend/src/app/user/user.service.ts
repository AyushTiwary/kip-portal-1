import { Injectable } from '@angular/core';
import {UserModule} from "./user.module";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: UserModule
})
export class UserService {

  constructor(private http: HttpClient) { }
}
