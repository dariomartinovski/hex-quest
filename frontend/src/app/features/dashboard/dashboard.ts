import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService, UserProfile } from '../../core/services/auth.service';
import { TaskService, TaskResponse } from '../../core/services/task.service';
import { AchievementService, AchievementResponse, UserAchievementResponse } from '../../core/services/achievement.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.html'
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private taskService = inject(TaskService);
  private achievementService = inject(AchievementService);

  currentUser = this.authService.currentUser;
  tasks = signal<TaskResponse[]>([]);
  achievements = signal<AchievementResponse[]>([]);
  userAchievementIds = signal<Set<number>>(new Set());
  isLoading = signal(true);

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);

    this.taskService.getTasks().subscribe({
      next: (res) => {
        this.tasks.set(res.data ?? []);
      }
    });

    this.achievementService.getAchievements().subscribe({
      next: (res) => {
        this.achievements.set(res.data ?? []);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });

    const user = this.currentUser();
    if (user) {
      this.achievementService.getUserAchievements(user.id).subscribe({
        next: (res) => {
          const ids = new Set((res.data ?? []).filter(ua => ua.isActive).map(ua => ua.achievement.id));
          this.userAchievementIds.set(ids);
        }
      });
    }
  }

  isUnlocked(achievementId: number): boolean {
    return this.userAchievementIds().has(achievementId);
  }

  getTaskAchievements(taskId: number): AchievementResponse[] {
    return this.achievements().filter(a => a.taskId === taskId);
  }
}
