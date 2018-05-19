import { Component, OnInit } from '@angular/core';
import {StorageService} from "../storage.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-session',
  templateUrl: './session.component.html',
  styleUrls: ['./session.component.css']
})
export class SessionComponent implements OnInit {

  isAdmin: boolean = false;

  constructor(private storageService: StorageService,
              private router: Router) { }

  ngOnInit() {
    this.isAdmin = this.storageService.isAdmin();
  }

  logout(){
    this.storageService.removeData('user');
    this.router.navigate(['/user/login']);
  }

  goTo(route: string) {
    this.router.navigate([route]);
  }
}
