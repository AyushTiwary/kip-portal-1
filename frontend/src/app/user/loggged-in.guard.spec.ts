import { TestBed, async, inject } from '@angular/core/testing';

import { LogggedInGuard } from './loggged-in.guard';

describe('LogggedInGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LogggedInGuard]
    });
  });

  it('should ...', inject([LogggedInGuard], (guard: LogggedInGuard) => {
    expect(guard).toBeTruthy();
  }));
});
