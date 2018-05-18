import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from '../user';
import {UserService} from '../user.service';
import {Observer, Subscription} from 'rxjs/index';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  user: User = {emailId: '', password: ''};
  loginSubscription: Subscription;
  constructor(private userService: UserService) { }

  ngOnInit() {}

  login() {
    this.loginSubscription = this.userService.login(this.user).subscribe((data) => {
      console.log(data);
    }, err => {
      console.error(err);
    });
  }

  // ngOnDestroy() {
  //   this.loginSubscription.unsubscribe();
  // }

}
