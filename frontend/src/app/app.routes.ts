import { Routes } from '@angular/router';
import { ShellComponent } from './layout/shell/shell';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { DashboardComponent } from './features/dashboard/dashboard';
import { ProfileComponent } from './features/profile/profile';
import { TaskDetailComponent } from './features/task-detail/task-detail';
import { AddTaskComponent } from './features/add-task/add-task';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'profile', component: ProfileComponent },
      { path: 'tasks/new', component: AddTaskComponent },
      { path: 'tasks/:id', component: TaskDetailComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
  { path: '**', redirectTo: 'dashboard' }
];
