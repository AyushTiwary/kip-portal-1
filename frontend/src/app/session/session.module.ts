import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {SessionRoutingModule} from "./session-routing.module";
import { ListSessionComponent } from './list-session/list-session.component';
import {SessionComponent} from "./session.component";
import { UpdatePermissionComponent } from './update-permission/update-permission.component';

@NgModule({
  imports: [
    CommonModule,
    SessionRoutingModule
  ],
  declarations: [ListSessionComponent, SessionComponent, UpdatePermissionComponent]
})
export class SessionModule { }
