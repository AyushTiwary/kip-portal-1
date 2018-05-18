import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {CreateSessionComponent} from "./create-session/create-session.component";
import {SessionRoutingModule} from "./session-routing.module";

@NgModule({
  imports: [
    CommonModule,
    SessionRoutingModule
  ],
  declarations: [CreateSessionComponent]
})
export class SessionModule { }
