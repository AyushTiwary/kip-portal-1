export interface User {
  emailId: string;
  password: string;
}

export interface LoginResponse {
  emailId: string;
  userType: string;
}
