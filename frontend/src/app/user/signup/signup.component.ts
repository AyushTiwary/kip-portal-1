import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from '../user.service';
import {Observer, Subscription} from 'rxjs/index';
import {StorageService} from '../../storage.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  emailId: string;
  signupSubscription: Subscription;
  constructor(private userService: UserService,
              private myStorage: StorageService,
              private router: Router) { }

  ngOnInit() {}

  signup() {
    this.signupSubscription = this.userService.signup(this.emailId).subscribe((value) => {
      this.myStorage.setData('user', value.data);
      alert(`An email has been sent on ${value.data.emailId} with your password. Kindly Login using that.`);
      this.router.navigate(['user/login']);
    }, err => {
      console.error(err);
    });
  }

}
