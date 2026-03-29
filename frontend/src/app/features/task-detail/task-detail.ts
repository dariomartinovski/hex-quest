import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TaskService, TaskDetailResponse, ParticipantProgress } from '../../core/services/task.service';
import { AchievementService, AchievementResponse, UserAchievementResponse } from '../../core/services/achievement.service';
import { AuthService } from '../../core/services/auth.service';
import { LogProgressModalComponent } from './log-progress-modal/log-progress-modal';
import { AchievementUnlockOverlayComponent } from '../achievement-unlock-overlay/achievement-unlock-overlay';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  templateUrl: './task-detail.html',
  styleUrl: './task-detail.scss',
  imports: [CommonModule, RouterModule, LogProgressModalComponent, AchievementUnlockOverlayComponent]
})
export class TaskDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private taskService = inject(TaskService);
  private achievementService = inject(AchievementService);
  private authService = inject(AuthService);

  currentUser = this.authService.currentUser;
  taskId = 0;
  task = signal<TaskDetailResponse | null>(null);
  leaderboard = signal<ParticipantProgress[]>([]);
  achievements = signal<AchievementResponse[]>([]);
  userAchievementIds = signal<Set<number>>(new Set());
  activeTab = signal<'leaderboard' | 'achievements' | 'activity'>('leaderboard');
  showProgressModal = signal(false);
  isLoading = signal(true);

  // Unlock overlay queue
  unlockQueue = signal<AchievementResponse[]>([]);
  currentUnlock = signal<AchievementResponse | null>(null);

  ngOnInit() {
    this.taskId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);

    this.taskService.getTaskDetail(this.taskId).subscribe({
      next: (res) => { this.task.set(res.data ?? null); this.isLoading.set(false); },
      error: () => this.isLoading.set(false)
    });

    this.taskService.getLeaderboard(this.taskId).subscribe({
      next: (res) => this.leaderboard.set(res.data ?? [])
    });

    this.achievementService.getAchievements().subscribe({
      next: (res) => {
        const taskAchievements = (res.data ?? []).filter(a => a.taskId === this.taskId);
        this.achievements.set(taskAchievements);
      }
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

  joinTask() {
    this.taskService.joinTask(this.taskId).subscribe({
      next: () => this.loadData()
    });
  }

  openProgressModal() {
    this.showProgressModal.set(true);
  }

  onProgressRecorded() {
    this.showProgressModal.set(false);
    this.loadData();
  }

  onAchievementsUnlocked(achievements: AchievementResponse[]) {
    this.unlockQueue.set([...achievements]);
    this.showNextUnlock();
  }

  showNextUnlock() {
    const queue = this.unlockQueue();
    if (queue.length > 0) {
      this.currentUnlock.set(queue[0]);
      this.unlockQueue.set(queue.slice(1));
    } else {
      this.currentUnlock.set(null);
    }
  }

  dismissUnlock() {
    this.showNextUnlock();
  }

  getRankEmoji(index: number): string {
    if (index === 0) return '🥇';
    if (index === 1) return '🥈';
    if (index === 2) return '🥉';
    return `#${index + 1}`;
  }
}
