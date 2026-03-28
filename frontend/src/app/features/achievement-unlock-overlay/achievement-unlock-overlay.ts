import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AchievementResponse } from '../../core/services/achievement.service';

@Component({
  selector: 'app-achievement-unlock-overlay',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed inset-0 z-[100] flex items-center justify-center" (click)="dismiss.emit()">
      <!-- Backdrop with radial glow -->
      <div class="absolute inset-0 bg-black/70 backdrop-blur-md"></div>

      <!-- Confetti particles -->
      <div class="absolute inset-0 overflow-hidden pointer-events-none">
        @for (i of particles; track i) {
          <div
            class="absolute w-2 h-2 rounded-full animate-confetti"
            [style.left.%]="i * 7.7"
            [style.animation-delay.ms]="i * 120"
            [style.background-color]="confettiColors[i % confettiColors.length]"
          ></div>
        }
      </div>

      <!-- Achievement Card -->
      <div class="relative z-10 text-center animate-achievement-reveal max-w-sm mx-6">
        <!-- Glow ring -->
        <div class="absolute inset-0 -m-8 bg-gradient-to-r from-amber-400 via-yellow-300 to-amber-400 rounded-full blur-3xl opacity-30 animate-pulse"></div>

        <!-- Badge -->
        <div class="relative">
          <div class="w-32 h-32 mx-auto rounded-full bg-gradient-to-br from-amber-400 to-yellow-500 shadow-2xl shadow-amber-500/40 flex items-center justify-center mb-6 animate-bounce-in">
            @if (achievement.typeName === 'SUPREMACY') {
              <svg xmlns="http://www.w3.org/2000/svg" class="w-16 h-16 text-white drop-shadow-lg" viewBox="0 0 24 24" fill="currentColor">
                <path fill-rule="evenodd" d="M5.166 2.621v.858c-1.035.148-2.059.33-3.071.543a.75.75 0 00-.584.859 6.753 6.753 0 006.138 5.6 6.73 6.73 0 002.743 1.346A6.707 6.707 0 019.279 15H8.54c-1.036 0-1.875.84-1.875 1.875V19.5h-.75a.75.75 0 000 1.5h12.75a.75.75 0 000-1.5h-.75v-2.625c0-1.036-.84-1.875-1.875-1.875h-.739a6.707 6.707 0 01-1.112-3.173 6.73 6.73 0 002.743-1.347 6.753 6.753 0 006.139-5.6.75.75 0 00-.585-.858 47.077 47.077 0 00-3.07-.543V2.62a.75.75 0 00-.658-.744 49.22 49.22 0 00-6.093-.377c-2.063 0-4.096.128-6.093.377a.75.75 0 00-.657.744z" clip-rule="evenodd" />
              </svg>
            } @else {
              <svg xmlns="http://www.w3.org/2000/svg" class="w-16 h-16 text-white drop-shadow-lg" viewBox="0 0 24 24" fill="currentColor">
                <path fill-rule="evenodd" d="M8.603 3.799A4.49 4.49 0 0112 2.25c1.357 0 2.573.6 3.397 1.549a4.49 4.49 0 013.498 1.307 4.491 4.491 0 011.307 3.497A4.49 4.49 0 0121.75 12a4.49 4.49 0 01-1.549 3.397 4.491 4.491 0 01-1.307 3.497 4.491 4.491 0 01-3.497 1.307A4.49 4.49 0 0112 21.75a4.49 4.49 0 01-3.397-1.549 4.49 4.49 0 01-3.498-1.306 4.491 4.491 0 01-1.307-3.498A4.49 4.49 0 012.25 12c0-1.357.6-2.573 1.549-3.397a4.49 4.49 0 011.307-3.497 4.49 4.49 0 013.497-1.307zm7.007 6.387a.75.75 0 10-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 00-1.06 1.06l2.25 2.25a.75.75 0 001.14-.094l3.75-5.25z" clip-rule="evenodd" />
              </svg>
            }
          </div>
        </div>

        <!-- Text -->
        <p class="text-amber-300 text-sm font-bold uppercase tracking-[0.3em] mb-2 animate-fade-in-delay">
          {{ achievement.typeName === 'SUPREMACY' ? 'Supremacy Claimed!' : 'Achievement Unlocked!' }}
        </p>
        <h2 class="text-3xl font-extrabold text-white mb-2 animate-fade-in-delay">{{ achievement.name }}</h2>
        @if (achievement.description) {
          <p class="text-white/70 text-sm animate-fade-in-delay">{{ achievement.description }}</p>
        }

        <p class="text-white/40 text-xs mt-8 animate-fade-in-delay">Tap anywhere to continue</p>
      </div>
    </div>
  `,
  styles: [`
    @keyframes confetti {
      0% { top: -5%; opacity: 1; transform: rotate(0deg) scale(1); }
      100% { top: 105%; opacity: 0; transform: rotate(720deg) scale(0.5); }
    }
    @keyframes achievement-reveal {
      0% { transform: scale(0.3); opacity: 0; }
      50% { transform: scale(1.05); }
      100% { transform: scale(1); opacity: 1; }
    }
    @keyframes fade-in-delay {
      0%, 40% { opacity: 0; transform: translateY(10px); }
      100% { opacity: 1; transform: translateY(0); }
    }
    :host { display: contents; }
    .animate-confetti { animation: confetti 3s ease-in forwards; }
    .animate-achievement-reveal { animation: achievement-reveal 0.6s ease-out; }
    .animate-fade-in-delay { animation: fade-in-delay 0.8s ease-out 0.3s both; }
  `]
})
export class AchievementUnlockOverlayComponent {
  @Input() achievement!: AchievementResponse;
  @Output() dismiss = new EventEmitter<void>();

  particles = Array.from({ length: 13 }, (_, i) => i);
  confettiColors = ['#fbbf24', '#a78bfa', '#34d399', '#f472b6', '#60a5fa', '#fb923c'];
}
