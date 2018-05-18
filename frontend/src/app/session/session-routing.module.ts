import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {CreateSessionComponent} from "./create-session/create-session.component";
import {ListsessionComponent} from "./listsession/listsession.component";
import {UpdateSessionComponent} from "./update-session/update-session.component";

const routes: Routes = [
  {
    path: 'session',
    component: ListsessionComponent
  },
  {
    path: 'session/create',
    component: CreateSessionComponent
  },
  {
    path: 'session/update',
    component: UpdateSessionComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SessionRoutingModule { }
