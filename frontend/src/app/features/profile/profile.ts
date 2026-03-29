import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { AchievementService, UserAchievementResponse, CategoryResponse } from '../../core/services/achievement.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
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
      next: (res) => this.earnedAchievements.set((res.data ?? []).filter(ua => ua.isActive))
    });
  }

  loadCategories() {
    this.achievementService.getCategories().subscribe({
      next: (res) => this.categories.set(res.data ?? [])
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
