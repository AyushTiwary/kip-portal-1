import { Component, OnInit } from '@angular/core';
import {SessionService} from '../session.service';
import {LoginResponse} from '../../user/user';

declare var toastr: any;

@Component({
  selector: 'app-update-permission',
  templateUrl: './update-permission.component.html',
  styleUrls: ['../create-session.component.css', './update-permission.component.css']
})
export class UpdatePermissionComponent implements OnInit {

  email: string = '';
  users: LoginResponse[] = [];
  userTypes = ['admin', 'Trainee', 'Trainer'];
  selectedType: string;
  showSelect: boolean = false;

  constructor(private sessionService: SessionService) { }

  ngOnInit() {}

  getusers(email: string) {
    this.sessionService.getUsers(email).subscribe((res) => {
      this.users = res.data;
    }, err => {
      console.error(err);
    });
  }

  setValue(user: any) {
    this.showSelect = true;
    this.selectedType = user.userType;
    this.email = user.emailId;
    this.users = [];
  }

  changeUserType() {
    this.sessionService.updateType({emailId: this.email, userType: this.selectedType}).subscribe((res) => {
      toastr.success(res.message);
      this.email = '';
      this.selectedType = '';
      this.showSelect = false;
    }, err => {
      console.error(err);
    });
  }
}
