import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="login-page">
      <section class="login-card" aria-labelledby="login-title">
        <div class="login-mark" aria-hidden="true">
          <svg viewBox="0 0 24 24"><path d="M9 3h6v6h6v6h-6v6H9v-6H3V9h6V3Z"/></svg>
        </div>
        <p class="login-eyebrow">Clínica médica</p>
        <h1 id="login-title">Bienvenido de nuevo</h1>
        <p class="login-subtitle">Ingresa tus credenciales para continuar</p>

        <form (ngSubmit)="onLogin()">
          <div class="login-field">
            <label for="username">Nombre de usuario</label>
            <input id="username" type="text" [(ngModel)]="username" name="username" placeholder="Ej. admin o usuario" autocomplete="username" required>
          </div>
          <div class="login-field">
            <label for="password">Contraseña</label>
            <input id="password" type="password" [(ngModel)]="password" name="password" placeholder="Tu contraseña" autocomplete="current-password" required>
          </div>
          <button type="submit" [disabled]="loading" class="login-submit">
            {{ loading ? 'Ingresando...' : 'Iniciar sesión' }}
          </button>
        </form>

        <div class="login-demo">
          <strong>Credenciales de prueba</strong>
          <span>Admin: admin / admin123</span>
          <span>Operador: usuario / user123</span>
        </div>
      </section>
    </main>
  `
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onLogin(): void {
    if (!this.username || !this.password) {
      Swal.fire({
        icon: 'warning',
        title: 'Campos requeridos',
        text: 'Por favor ingresa usuario y contraseña.',
        confirmButtonColor: '#506b85'
      });
      return;
    }

    this.loading = true;
    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.loading = false;
        Swal.fire({
          icon: 'success',
          title: '¡Bienvenido!',
          text: 'Inicio de sesión exitoso',
          timer: 1500,
          showConfirmButton: false
        });
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        console.error('Error detallado de login:', err);
        const mensaje = err.error?.message || err.message || 'Credenciales inválidas o servidor no disponible.';
        Swal.fire({
          icon: 'error',
          title: 'Error de Autenticación',
          text: mensaje,
          confirmButtonColor: '#b45f72'
        });
      }
    });
  }
}
