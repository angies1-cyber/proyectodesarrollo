import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="main-shell" [class.sidebar-collapsed]="sidebarCollapsed" [class.sidebar-open]="mobileMenuOpen">
      <button *ngIf="mobileMenuOpen" class="drawer-backdrop" type="button" (click)="closeMobileMenu()" aria-label="Cerrar menú"></button>
      <aside class="app-sidebar" id="main-navigation" aria-label="Navegación principal">
        <div class="sidebar-brand">
          <svg class="brand-icon" viewBox="0 0 24 24" aria-hidden="true"><path d="M9 3h6v6h6v6h-6v6H9v-6H3V9h6V3Z"/></svg>
          <strong class="sidebar-label">Clínica Médica</strong>
        </div>

        <div class="sidebar-user" *ngIf="user">
          <span class="user-name sidebar-label">{{ user.username }}</span>
          <span class="role-badge sidebar-label" [class.role-admin]="isAdmin()">
            {{ roleLabel() }}
          </span>
          <span class="user-initial" aria-hidden="true">{{ user.username.charAt(0).toUpperCase() }}</span>
        </div>

        <nav class="sidebar-nav">
          <ng-container *ngFor="let modulo of menu">
            <a *ngIf="modulo.ruta" [routerLink]="modulo.ruta" routerLinkActive="active" (click)="closeMobileMenu()" [title]="modulo.nombre">
              <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 4h7v7H4V4Zm9 0h7v7h-7V4ZM4 13h7v7H4v-7Zm9 0h7v7h-7v-7Z"/></svg>
              <span class="sidebar-label">{{ modulo.nombre }}</span>
            </a>
            <section *ngIf="!modulo.ruta && modulo.hijos?.length" class="menu-group">
              <strong class="menu-group-title sidebar-label">{{ modulo.nombre }}</strong>
              <a *ngFor="let hijo of modulo.hijos" [routerLink]="hijo.ruta" routerLinkActive="active" (click)="closeMobileMenu()" [title]="hijo.nombre">
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 5h10v2H7V5Zm0 6h10v2H7v-2Zm0 6h10v2H7v-2Z"/></svg>
                <span class="sidebar-label">{{ hijo.nombre }}</span>
              </a>
            </section>
          </ng-container>
        </nav>
      </aside>

      <main class="app-main">
        <header class="app-topbar">
          <button class="menu-toggle" type="button" (click)="toggleSidebar()" [attr.aria-expanded]="mobileMenuOpen || !sidebarCollapsed" aria-controls="main-navigation" [attr.aria-label]="toggleLabel">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 6h16v2H4V6Zm0 5h16v2H4v-2Zm0 5h16v2H4v-2Z"/></svg>
          </button>
          <h1>Clínica</h1>
          <button (click)="logout()" class="topbar-logout" type="button">
            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M10 17v-2h4V9h-4V7l-5 5 5 5Zm9-14h-8v2h8v14h-8v2h8a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2Z"/></svg>
            <span>Cerrar sesión</span>
          </button>
        </header>
        <div class="app-page"><router-outlet></router-outlet></div>
      </main>
    </div>
  `
  
})
export class MainLayoutComponent {
  user = this.authService.getUser();
  menu = this.authService.getMenuTree();
  sidebarCollapsed = false;
  mobileMenuOpen = false;

  constructor(private authService: AuthService, private router: Router) {}

  get toggleLabel(): string {
    return this.mobileMenuOpen || !this.sidebarCollapsed ? 'Contraer menú de navegación' : 'Abrir menú de navegación';
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ROLE_ADMIN') || this.authService.isSuperAdmin();
  }

  roleLabel(): string {
    if (this.authService.isSuperAdmin()) return 'Superadministrador';
    return this.user?.roles.join(', ') || 'Sin rol';
  }

  toggleSidebar(): void {
    if (window.matchMedia('(max-width: 768px)').matches) {
      this.mobileMenuOpen = !this.mobileMenuOpen;
      return;
    }
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  closeMobileMenu(): void {
    this.mobileMenuOpen = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
