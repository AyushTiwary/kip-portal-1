import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from '../user';
import {UserService} from '../user.service';
import {Observer, Subscription} from 'rxjs/index';
import {StorageService} from "../../storage.service";
import {Router} from "@angular/router";

declare var toastr:any;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  user: User = {emailId: 'anubhav.tarar@knoldus.in', password: 'ddrshrdfe385snf3cqi5p5ho'};
  loginSubscription: Subscription;
  constructor(
    private userService: UserService,
    private myStorage: StorageService,
    private router: Router) {
  }

  ngOnInit() {}

  login() {
    this.userService.login(this.user).subscribe((user) => {
      this.myStorage.setData('user', user.data);
      this.router.navigate(['session/create']);
    }, err => {
      toastr.error('Internal Server Error', 'Sorry!');
    });
  }

}
