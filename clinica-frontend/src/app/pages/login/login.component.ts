import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, ElementRef, NgZone, OnDestroy, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { AuthService } from '../../core/services/auth.service';
import { TurnstileApi, TurnstileService } from '../../core/services/turnstile.service';
import { environment } from '../../../environments/environment';
import { finalize } from 'rxjs';

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
          <div class="turnstile-field" aria-labelledby="turnstile-label">
            <span id="turnstile-label" class="turnstile-label">Verificación de seguridad</span>
            <div class="turnstile-shell">
              <div #turnstileContainer></div>
            </div>
            <p class="turnstile-status" [class.turnstile-error]="turnstileError" role="status" aria-live="polite">
              {{ turnstileStatus }}
            </p>
          </div>
          <button type="submit" [disabled]="loading || !turnstileToken" class="login-submit">
            {{ loading ? 'Ingresando...' : 'Iniciar sesión' }}
          </button>
        </form>

      </section>
    </main>
  `
})
export class LoginComponent implements AfterViewInit, OnDestroy {
  @ViewChild('turnstileContainer') private turnstileContainer!: ElementRef<HTMLElement>;
  username = '';
  password = '';
  loading = false;
  turnstileToken = '';
  turnstileStatus = 'Completa la verificación para habilitar el inicio de sesión.';
  turnstileError = false;
  private turnstileApi?: TurnstileApi;
  private widgetId?: string;
  private destroyed = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private turnstileService: TurnstileService,
    private zone: NgZone
  ) {}

  ngAfterViewInit(): void {
    this.turnstileService.load().then(api => {
      if (this.destroyed) return;
      this.turnstileApi = api;
      this.widgetId = api.render(this.turnstileContainer.nativeElement, {
        sitekey: environment.turnstileSiteKey,
        action: 'login',
        appearance: 'always',
        theme: 'auto',
        callback: token => this.zone.run(() => {
          this.turnstileToken = token;
          this.turnstileError = false;
          this.turnstileStatus = 'Verificación completada.';
        }),
        'expired-callback': () => this.zone.run(() => this.resetTurnstile(
          'La verificación expiró. Complétala nuevamente.'
        )),
        'error-callback': () => this.zone.run(() => this.resetTurnstile(
          'No se pudo completar la verificación. Inténtalo nuevamente.', true
        ))
      });
    }).catch(() => this.zone.run(() => {
      if (this.destroyed) return;
      this.turnstileError = true;
      this.turnstileStatus = 'No se pudo cargar la verificación de seguridad. Recarga la página.';
    }));
  }

  ngOnDestroy(): void {
    this.destroyed = true;
    if (this.turnstileApi && this.widgetId) {
      this.turnstileApi.remove(this.widgetId);
    }
  }

  onLogin(): void {
    if (!this.username || !this.password) {
      this.resetTurnstile('Completa nuevamente la verificación de seguridad.');
      Swal.fire({
        icon: 'warning',
        title: 'Campos requeridos',
        text: 'Por favor ingresa usuario y contraseña.',
        confirmButtonColor: '#506b85'
      });
      return;
    }

    const turnstileToken = this.turnstileToken;
    this.loading = true;
    this.authService.login({ username: this.username, password: this.password, turnstileToken }).pipe(
      finalize(() => {
        this.loading = false;
        this.resetTurnstile('Completa nuevamente la verificación para otro intento.');
      })
    ).subscribe({
      next: () => {
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

  private resetTurnstile(message: string, isError = false): void {
    this.turnstileToken = '';
    this.turnstileError = isError;
    this.turnstileStatus = message;
    if (this.turnstileApi && this.widgetId) {
      this.turnstileApi.reset(this.widgetId);
    }
  }
}
