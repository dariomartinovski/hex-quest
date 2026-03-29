import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AchievementResponse } from '../../core/services/achievement.service';

@Component({
  selector: 'app-achievement-unlock-overlay',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './achievement-unlock-overlay.html',
  styleUrl: './achievement-unlock-overlay.scss'
})
export class AchievementUnlockOverlayComponent {
  @Input() achievement!: AchievementResponse;
  @Output() dismiss = new EventEmitter<void>();

  particles = Array.from({ length: 13 }, (_, i) => i);
  confettiColors = ['#fbbf24', '#a78bfa', '#34d399', '#f472b6', '#60a5fa', '#fb923c'];
}
