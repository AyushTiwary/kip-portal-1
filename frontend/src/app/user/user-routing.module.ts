import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {SignupComponent} from "./signup/signup.component";
import {AppComponent} from "../app.component";
import {UserComponent} from "./user.component";
import {LoginGuard} from "./login.guard";

const routes: Routes = [
  {path: '', redirectTo: 'user', pathMatch: 'full'},
  {
    path: 'user',
    component: UserComponent,
    canActivate: [LoginGuard],
    children: [
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full',
      },
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'signup',
        component: SignupComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
