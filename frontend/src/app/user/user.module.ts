import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserRoutingModule } from './user-routing.module';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import { UserComponent } from './user.component';
import { UpdatePermissionComponent } from './update-permission/update-permission.component';

@NgModule({
  imports: [
    CommonModule,
    UserRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  declarations: [LoginComponent, SignupComponent, UserComponent, UpdatePermissionComponent],
})
export class UserModule { }
