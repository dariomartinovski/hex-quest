import { Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  displayName?: string;
  avatarUrl?: string;
  sex?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: UserProfile;
}

export interface ApiResponse<T> {
  data?: T;
  meta?: any;
  errors?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private accessToken: string | null = null;
  currentUser = signal<UserProfile | null>(null);

  constructor(private router: Router, private http: HttpClient) {
    this.checkInitialAuth();
  }

  register(payload: any): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>('/api/auth/register', payload).pipe(
      tap(res => {
        if (res.data) this.handleAuthSuccess(res.data);
      })
    );
  }

  login(payload: any): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>('/api/auth/login', payload).pipe(
      tap(res => {
        if (res.data) this.handleAuthSuccess(res.data);
      })
    );
  }

  fetchMe(): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>('/api/auth/me').pipe(
      tap(res => {
        if (res.data) this.currentUser.set(res.data);
      })
    );
  }

  private handleAuthSuccess(data: AuthResponse) {
    this.accessToken = data.accessToken;
    localStorage.setItem('refresh_token', data.refreshToken);
    this.currentUser.set(data.user);
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refresh_token');
  }

  logout() {
    this.accessToken = null;
    this.currentUser.set(null);
    localStorage.removeItem('refresh_token');
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!this.accessToken || !!this.getRefreshToken();
  }

  private checkInitialAuth() {
    if (this.getRefreshToken()) {
      // In a full implementation, we might call /api/auth/refresh here.
      // For now, if we have a refresh token, we allow navigation and fetch /me if needed.
    }
  }
}
