import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../../core/services/task.service';

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

  private taskService = inject(TaskService);

  delta = signal(1);
  note = signal('');
  isSubmitting = signal(false);
  showSuccess = signal(false);

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

    this.taskService.recordProgress(this.taskId, {
      delta: this.delta(),
      note: this.note() || undefined
    }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.showSuccess.set(true);
        setTimeout(() => {
          this.recorded.emit();
        }, 1200);
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
