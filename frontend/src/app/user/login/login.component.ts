import {Component, OnDestroy, OnInit} from '@angular/core';
import {User} from '../user';
import {UserService} from '../user.service';
import {Observer, Subscription} from 'rxjs/index';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  user: User = {emailId: '', password: ''};
  loginSubscription: Subscription;
  constructor(private userService: UserService) { }

  ngOnInit() {}

  login() {
    const obs: Observer = {
      next : (res) => {
        console.log(res);
      },
      error: (err) => {
        console.error(err);
      }
    };
    this.loginSubscription = this.userService.login(this.user).subscribe(obs);
  }

  ngOnDestroy() {
    this.loginSubscription.unsubscribe();
  }

}
