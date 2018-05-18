import { Component, OnInit } from '@angular/core';
import {StorageService} from "../storage.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-session',
  templateUrl: './session.component.html',
  styleUrls: ['./session.component.css']
})
export class SessionComponent implements OnInit {

  constructor(private storageService: StorageService,
              private router: Router) { }

  ngOnInit() {
  }

  logout(){
    this.storageService.removeData('user');
    this.router.navigate(['/user/login']);
  }

}
