import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-6">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">Your Profile</h2>
      @if (currentUser()) {
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 flex flex-col items-center">
          
          @if (currentUser()?.avatarUrl) {
            <img [src]="currentUser()?.avatarUrl" class="w-24 h-24 rounded-full mb-4 shadow-md object-cover">
          } @else {
            <div class="w-24 h-24 rounded-full mb-4 bg-indigo-100 text-indigo-600 flex items-center justify-center text-3xl font-bold shadow-sm">
              {{ currentUser()?.username?.charAt(0) | uppercase }}
            </div>
          }

          <h3 class="text-xl font-bold text-gray-900">{{ currentUser()?.displayName || currentUser()?.username }}</h3>
          <p class="text-sm text-gray-500 mb-6">{{ currentUser()?.email }}</p>
          
          <div class="w-full pt-6 border-t border-gray-100">
            <h4 class="text-sm font-semibold text-gray-400 uppercase tracking-wider mb-4 text-left">Details</h4>
            <div class="space-y-3">
              <div class="flex justify-between">
                <span class="text-gray-500 text-sm">Username</span>
                <span class="font-medium text-gray-900">{{ currentUser()?.username }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500 text-sm">Sex</span>
                <span class="font-medium text-gray-900">{{ currentUser()?.sex || 'Not specified' }}</span>
              </div>
            </div>
          </div>

          <button (click)="logout()" class="mt-8 w-full py-2.5 px-4 bg-red-50 text-red-600 hover:bg-red-100 text-sm font-semibold rounded-xl transition-colors">
            Sign Out
          </button>
        </div>
      }
    </div>
  `
})
export class ProfileComponent {
  authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  logout() {
    this.authService.logout();
  }
}
