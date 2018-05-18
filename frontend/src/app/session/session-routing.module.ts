import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {CreateSessionComponent} from './create-session/create-session.component';
import {UpdateSessionComponent} from './update-session/update-session.component';
import {LogggedInGuard} from '../user/loggged-in.guard';
import {ListSessionComponent} from './list-session/list-session.component';
import {SessionComponent} from "./session.component";
import {UpdatePermissionComponent} from "./update-permission/update-permission.component";

const routes: Routes = [
  {
    path: 'session',
    component: SessionComponent,
    canActivate: [LogggedInGuard],
    children: [
      {
        path: 'create',
        component: CreateSessionComponent
      }, {
        path: 'update',
        component: UpdateSessionComponent
      }, {
        path: 'list',
        component: ListSessionComponent
      },
      {
        path: 'update/permission',
        component: UpdatePermissionComponent
      }]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SessionRoutingModule { }
