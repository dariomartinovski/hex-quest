import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TaskService } from '../../core/services/task.service';

@Component({
  selector: 'app-add-task',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './add-task.html'
})
export class AddTaskComponent {
  private fb = inject(FormBuilder);
  private taskService = inject(TaskService);
  private router = inject(Router);

  taskForm = this.fb.group({
    name: ['', Validators.required],
    description: [''],
    unitLabel: ['', Validators.required]
  });

  errorMsg: string | null = null;
  isSubmitting = false;

  onSubmit() {
    if (this.taskForm.invalid) return;

    this.isSubmitting = true;
    this.errorMsg = null;

    const val = this.taskForm.value;
    this.taskService.createTask({
      name: val.name!,
      description: val.description || undefined,
      unitLabel: val.unitLabel!
    }).subscribe({
      next: (res) => {
        if (res.data) {
          this.router.navigate(['/tasks', res.data.id]);
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMsg = err.error?.errors?.[0] || 'Failed to create task';
      }
    });
  }
}
