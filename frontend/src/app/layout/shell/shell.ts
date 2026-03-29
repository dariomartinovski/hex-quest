import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-shell',
  templateUrl: './shell.html',
  styleUrl: './shell.scss',
  imports: [RouterOutlet, RouterModule, CommonModule]
})
export class ShellComponent implements OnInit {
  authService = inject(AuthService);
  currentUser = this.authService.currentUser;

  ngOnInit() {
    // Optionally fetch /me profile if it's missing but we have a token
  }

  logout() {
    this.authService.logout();
  }
}
