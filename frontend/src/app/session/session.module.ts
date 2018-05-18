import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {CreateSessionComponent} from "./create-session/create-session.component";
import {SessionRoutingModule} from "./session-routing.module";
import { UpdateSessionComponent } from './update-session/update-session.component';
import { ListsessionComponent } from './listsession/listsession.component';

@NgModule({
  imports: [
    CommonModule,
    SessionRoutingModule
  ],
  declarations: [CreateSessionComponent, UpdateSessionComponent, ListsessionComponent]
})
export class SessionModule { }
