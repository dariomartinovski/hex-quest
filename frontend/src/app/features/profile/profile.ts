import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { AchievementService, UserAchievementResponse, CategoryResponse } from '../../core/services/achievement.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-6 max-w-2xl mx-auto">
      <h2 class="text-2xl font-bold text-gray-800 mb-6">Your Profile</h2>
      
      @if (currentUser()) {
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 flex flex-col items-center mb-8">
          
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

        <!-- Achievement Wall -->
        <div class="mb-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-xl font-bold text-gray-900">Achievement Wall</h3>
            <span class="bg-indigo-100 text-indigo-700 text-xs font-bold px-2.5 py-1 rounded-full">
              {{ earnedAchievements().length }} Earned
            </span>
          </div>

          <!-- Category Filter -->
          <div class="flex gap-2 mb-6 overflow-x-auto pb-2 scrollbar-hide">
            <button 
              (click)="selectedCategory.set(null)"
              class="shrink-0 px-4 py-1.5 rounded-full text-sm font-medium transition-all"
              [class]="selectedCategory() === null ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
            >
              All
            </button>
            @for (cat of categories(); track cat.id) {
              <button 
                (click)="selectedCategory.set(cat.id)"
                class="shrink-0 px-4 py-1.5 rounded-full text-sm font-medium transition-all"
                [class]="selectedCategory() === cat.id ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
              >
                {{ cat.name }}
              </button>
            }
          </div>

          @if (filteredAchievements().length === 0) {
            <div class="bg-gray-50 rounded-2xl py-12 text-center border-2 border-dashed border-gray-200">
              <p class="text-gray-400 text-sm">No achievements earned yet in this category.</p>
            </div>
          } @else {
            <div class="grid grid-cols-2 sm:grid-cols-3 gap-4">
              @for (ua of filteredAchievements(); track ua.id) {
                <div class="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 flex flex-col items-center group hover:shadow-md transition-all">
                  <div class="w-16 h-16 rounded-full mb-3 flex items-center justify-center transition-transform group-hover:scale-110"
                       [class]="ua.achievement.typeName === 'SUPREMACY' ? 'bg-amber-100 text-amber-600 shadow-inner shadow-amber-200' : 'bg-indigo-50 text-indigo-600 shadow-inner shadow-indigo-100'">
                    @if (ua.achievement.badgeImageUrl) {
                      <img [src]="ua.achievement.badgeImageUrl" class="w-10 h-10 object-contain">
                    } @else if (ua.achievement.typeName === 'SUPREMACY') {
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M5 2a1 1 0 011 1v1h1a1 1 0 010 2H6v1a1 1 0 01-2 0V6H3a1 1 0 010-2h1V3a1 1 0 011-1zm0 10a1 1 0 011 1v1h1a1 1 0 110 2H6v1a1 1 0 11-2 0v-1H3a1 1 0 110-2h1v-1a1 1 0 011-1zM12 2a1 1 0 01.967.744L14.146 7.2 17.5 9.134a1 1 0 010 1.732l-3.354 1.935-1.18 4.455a1 1 0 01-1.933 0L9.854 12.8 6.5 10.866a1 1 0 010-1.732l3.354-1.935 1.18-4.455A1 1 0 0112 2z" clip-rule="evenodd" />
                      </svg>
                    } @else {
                      <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M6.267 3.455a3.066 3.066 0 001.745-.723 3.066 3.066 0 013.976 0 3.066 3.066 0 001.745.723 3.066 3.066 0 012.812 2.812c.051.643.304 1.254.723 1.745a3.066 3.066 0 010 3.976 3.066 3.066 0 00-.723 1.745 3.066 3.066 0 01-2.812 2.812 3.066 3.066 0 00-1.745.723 3.066 3.066 0 01-3.976 0 3.066 3.066 0 00-1.745-.723 3.066 3.066 0 01-2.812-2.812 3.066 3.066 0 00-.723-1.745 3.066 3.066 0 010-3.976 3.066 3.066 0 00.723-1.745 3.066 3.066 0 012.812-2.812zm7.44 5.252a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                      </svg>
                    }
                  </div>
                  <p class="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-1">{{ ua.achievement.typeName }}</p>
                  <h4 class="text-xs font-bold text-gray-900 text-center leading-tight mb-1">{{ ua.achievement.name }}</h4>
                  @if (ua.awardedAt) {
                    <p class="text-[9px] text-gray-400">{{ ua.awardedAt | date:'mediumDate' }}</p>
                  }
                </div>
              }
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .scrollbar-hide::-webkit-scrollbar { display: none; }
    .scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }
  `]
})
export class ProfileComponent implements OnInit {
  authService = inject(AuthService);
  achievementService = inject(AchievementService);
  
  currentUser = this.authService.currentUser;
  earnedAchievements = signal<UserAchievementResponse[]>([]);
  categories = signal<CategoryResponse[]>([]);
  selectedCategory = signal<number | null>(null);

  ngOnInit() {
    const user = this.currentUser();
    if (user) {
      this.loadAchievements(user.id);
      this.loadCategories();
    }
  }

  loadAchievements(userId: number) {
    this.achievementService.getUserAchievements(userId).subscribe({
      next: (res) => {
        this.earnedAchievements.set((res.data ?? []).filter(ua => ua.isActive));
      }
    });
  }

  loadCategories() {
    this.achievementService.getCategories().subscribe({
      next: (res) => {
        this.categories.set(res.data ?? []);
      }
    });
  }

  filteredAchievements() {
    const catId = this.selectedCategory();
    if (catId === null) return this.earnedAchievements();
    return this.earnedAchievements().filter(ua => ua.achievement.categoryId === catId);
  }

  logout() {
    this.authService.logout();
  }
}
