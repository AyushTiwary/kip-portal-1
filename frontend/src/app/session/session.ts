export interface Session {
  startDate: string;
  endDate: string;
  trainee: string;
  technologyName: string;
  numberOfDays: number;
  content: string;
  assistantTrainer?: string;
}

export interface CalendarEvent {
  title: string;
  start: string;
  end: string;
}

export interface UpdateDateRequest {
  previousDate: string;
  updateDate: string;
}

export interface CreateSession {
  startDate: string;
  trainee: string;
  technologyName: string;
  numberOfDays: number;
  content: string;
  assistantTrainer?: string;
}
