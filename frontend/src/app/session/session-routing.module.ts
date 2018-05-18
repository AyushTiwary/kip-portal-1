import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {CreateSessionComponent} from "./create-session/create-session.component";
import {LogggedInGuard} from "../user/loggged-in.guard";

const routes: Routes = [
  {
    path: 'session/create',
    component: CreateSessionComponent,
    canActivate: [LogggedInGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SessionRoutingModule { }
