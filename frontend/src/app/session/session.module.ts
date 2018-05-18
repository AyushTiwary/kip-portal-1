import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {CreateSessionComponent} from "./create-session/create-session.component";
import {SessionRoutingModule} from "./session-routing.module";
import { UpdateSessionComponent } from './update-session/update-session.component';
import { ListSessionComponent } from './list-session/list-session.component';
import {SessionComponent} from "./session.component";
import { UpdatePermissionComponent } from './update-permission/update-permission.component';

@NgModule({
  imports: [
    CommonModule,
    SessionRoutingModule
  ],
  declarations: [CreateSessionComponent, UpdateSessionComponent,  ListSessionComponent, SessionComponent, UpdatePermissionComponent]
})
export class SessionModule { }
