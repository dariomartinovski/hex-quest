import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div class="p-6">
      <h2 class="text-2xl font-bold text-gray-800">Dashboard</h2>
      <p class="text-gray-600 mt-2">Welcome! Your tasks and Hex Quests will appear here.</p>
    </div>
  `
})
export class DashboardComponent {}
