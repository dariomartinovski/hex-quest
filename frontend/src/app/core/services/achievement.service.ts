import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from './auth.service';

export interface AchievementResponse {
  id: number;
  taskId: number;
  name: string;
  description?: string;
  badgeImageUrl?: string;
  sex?: string;
  categoryId?: number;
  typeName: string;
  thresholdValue?: number;
  minThreshold?: number;
}

export interface UserAchievementResponse {
  id: number;
  achievement: AchievementResponse;
  awardedAt?: string;
  isActive: boolean;
}

export interface CategoryResponse {
  id: number;
  name: string;
  icon?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AchievementService {
  constructor(private http: HttpClient) {}

  getAchievements(): Observable<ApiResponse<AchievementResponse[]>> {
    return this.http.get<ApiResponse<AchievementResponse[]>>('/api/achievements');
  }

  getCategories(): Observable<ApiResponse<CategoryResponse[]>> {
    return this.http.get<ApiResponse<CategoryResponse[]>>('/api/achievements/categories');
  }

  getUserAchievements(userId: number): Observable<ApiResponse<UserAchievementResponse[]>> {
    return this.http.get<ApiResponse<UserAchievementResponse[]>>(`/api/users/${userId}/achievements`);
  }
}
