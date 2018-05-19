import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {User} from '../user';
import {UserService} from '../user.service';
import {Observer, Subscription} from 'rxjs/index';
import {StorageService} from "../../storage.service";
import {Router} from "@angular/router";
import * as $ from "jquery";

declare var toastr:any;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, AfterViewInit {

  user: User = {emailId: '', password: ''};
  loginSubscription: Subscription;
  constructor(
    private userService: UserService,
    private myStorage: StorageService,
    private router: Router) {
  }

  ngOnInit() {}

  ngAfterViewInit() {
  $(".input100").each(function(){
    $(this).on('blur', function(){
      let value: any = $(this).val();
      if(value.trim() != "") {
        $(this).addClass('has-val');
      }
      else {
        $(this).removeClass('has-val');
      }
    })
  });
}

  login() {
    this.userService.login(this.user).subscribe((user) => {
      this.myStorage.setData('user', user.data);
      this.router.navigate(['session/list']);
    }, err => {
      toastr.error('Internal Server Error', 'Sorry!');
    });
  }

}
