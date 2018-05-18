import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from "../user.service";
import {Observer, Subscription} from "rxjs/index";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit, OnDestroy {
  emailId: string;
  signupSubscription: Subscription;
  constructor(private userService: UserService) { }

  ngOnInit() {}

  signup() {
    const obs: Observer = {
      next : (res) => {
        console.log(res);
      },
      error: (err) => {
        console.error(err);
      }
    };
    this.signupSubscription = this.userService.signup(this.emailId).subscribe(obs);
  }

  ngOnDestroy() {
    this.signupSubscription.unsubscribe();
  }

}
