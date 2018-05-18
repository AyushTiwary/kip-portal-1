import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListsessionComponent } from './listsession.component';

describe('ListsessionComponent', () => {
  let component: ListsessionComponent;
  let fixture: ComponentFixture<ListsessionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListsessionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListsessionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
