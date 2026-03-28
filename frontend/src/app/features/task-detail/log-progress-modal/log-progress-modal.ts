import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../../core/services/task.service';
import { AchievementService, AchievementResponse } from '../../../core/services/achievement.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-log-progress-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './log-progress-modal.html'
})
export class LogProgressModalComponent {
  @Input() taskId!: number;
  @Input() unitLabel: string = '';
  @Output() close = new EventEmitter<void>();
  @Output() recorded = new EventEmitter<void>();
  @Output() achievementsUnlocked = new EventEmitter<AchievementResponse[]>();

  private taskService = inject(TaskService);
  private achievementService = inject(AchievementService);
  private authService = inject(AuthService);

  delta = signal(1);
  note = signal('');
  isSubmitting = signal(false);
  showSuccess = signal(false);

  private previousAchievementIds = new Set<number>();

  quickAdd(amount: number) {
    this.delta.update(v => v + amount);
  }

  setDelta(value: number) {
    if (value > 0) {
      this.delta.set(value);
    }
  }

  submit() {
    if (this.delta() <= 0) return;

    this.isSubmitting.set(true);

    // Snapshot current achievements BEFORE recording progress
    const user = this.authService.currentUser();
    if (user) {
      this.achievementService.getUserAchievements(user.id).subscribe({
        next: (res) => {
          this.previousAchievementIds = new Set(
            (res.data ?? []).filter(ua => ua.isActive).map(ua => ua.achievement.id)
          );
          this.doRecordProgress();
        },
        error: () => this.doRecordProgress()
      });
    } else {
      this.doRecordProgress();
    }
  }

  private doRecordProgress() {
    this.taskService.recordProgress(this.taskId, {
      delta: this.delta(),
      note: this.note() || undefined
    }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.showSuccess.set(true);

        // Check for newly unlocked achievements
        const user = this.authService.currentUser();
        if (user) {
          this.achievementService.getUserAchievements(user.id).subscribe({
            next: (res) => {
              const currentIds = (res.data ?? []).filter(ua => ua.isActive).map(ua => ua.achievement);
              const newlyUnlocked = currentIds.filter(a => !this.previousAchievementIds.has(a.id));

              setTimeout(() => {
                if (newlyUnlocked.length > 0) {
                  this.achievementsUnlocked.emit(newlyUnlocked);
                }
                this.recorded.emit();
              }, 1200);
            },
            error: () => {
              setTimeout(() => this.recorded.emit(), 1200);
            }
          });
        } else {
          setTimeout(() => this.recorded.emit(), 1200);
        }
      },
      error: () => {
        this.isSubmitting.set(false);
      }
    });
  }

  onBackdropClick(event: MouseEvent) {
    if (event.target === event.currentTarget) {
      this.close.emit();
    }
  }
}
