import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from "../user.service";
import {Observer, Subscription} from "rxjs/index";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  emailId: string;
  signupSubscription: Subscription;
  constructor(private userService: UserService) { }

  ngOnInit() {}

  signup() {
    this.signupSubscription = this.userService.signup(this.emailId).subscribe((data) => {
      console.log(data);
    }, err => {
      console.error(err);
    });
  }
  //
  // ngOnDestroy() {
  //   this.signupSubscription.unsubscribe();
  // }

}
