import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import { Observable } from 'rxjs';
import {StorageService} from "../storage.service";

@Injectable({
  providedIn: 'root'
})
export class LogggedInGuard implements CanActivate {

  constructor(private myStorage: StorageService, private router: Router) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    if (this.myStorage.isLoggedIn()) {
      return true;
    } else {
      this.router.navigate(['session/create']);
      return false;
    }
  }
}
