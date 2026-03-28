import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from './auth.service';

export interface TaskResponse {
  id: number;
  name: string;
  description?: string;
  unitLabel: string;
  creatorUsername: string;
}

export interface TaskDetailResponse {
  id: number;
  name: string;
  description?: string;
  unitLabel: string;
  participantsCount: number;
  totalProgressEvents: number;
}

export interface ParticipantProgress {
  username: string;
  cumulativeTotal: number;
}

export interface CreateTaskRequest {
  name: string;
  description?: string;
  unitLabel: string;
}

export interface RecordProgressRequest {
  delta: number;
  note?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  constructor(private http: HttpClient) {}

  getTasks(): Observable<ApiResponse<TaskResponse[]>> {
    return this.http.get<ApiResponse<TaskResponse[]>>('/api/tasks');
  }

  createTask(payload: CreateTaskRequest): Observable<ApiResponse<TaskResponse>> {
    return this.http.post<ApiResponse<TaskResponse>>('/api/tasks', payload);
  }

  getTaskDetail(id: number): Observable<ApiResponse<TaskDetailResponse>> {
    return this.http.get<ApiResponse<TaskDetailResponse>>(`/api/tasks/${id}`);
  }

  joinTask(id: number): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`/api/tasks/${id}/participants`, {});
  }

  recordProgress(id: number, payload: RecordProgressRequest): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`/api/tasks/${id}/progress`, payload);
  }

  getLeaderboard(id: number): Observable<ApiResponse<ParticipantProgress[]>> {
    return this.http.get<ApiResponse<ParticipantProgress[]>>(`/api/tasks/${id}/leaderboard`);
  }
}
