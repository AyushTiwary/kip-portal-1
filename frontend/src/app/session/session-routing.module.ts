import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {CreateSessionComponent} from "./create-session/create-session.component";
import {ListsessionComponent} from "./listsession/listsession.component";
import {UpdateSessionComponent} from "./update-session/update-session.component";
import {LogggedInGuard} from "../user/loggged-in.guard";

const routes: Routes = [
  {
    path: 'session',
    component: ListsessionComponent,
    canActivate: [LogggedInGuard]
  },
  {
    path: 'session/create',
    component: CreateSessionComponent,
    canActivate: [LogggedInGuard]
  },
  {
    path: 'session/update',
    component: UpdateSessionComponent,
    canActivate: [LogggedInGuard]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SessionRoutingModule { }
