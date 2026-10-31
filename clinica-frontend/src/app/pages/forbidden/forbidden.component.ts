import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-forbidden',
  standalone: true,
  imports: [RouterLink],
  template: `<section class="forbidden-page"><p class="section-kicker">Acceso restringido</p><h2>No tienes permiso para abrir esta sección</h2><p>Solicita el acceso correspondiente a un administrador.</p><a routerLink="/dashboard">Volver al inicio</a></section>`,
  styles: [`.forbidden-page{max-width:680px;margin:4rem auto;padding:3rem;background:#fff;border-radius:18px;text-align:center;box-shadow:0 18px 45px #29344918}.forbidden-page a{display:inline-block;margin-top:1rem;padding:.75rem 1.2rem;border-radius:8px;background:#8f4056;color:#fff;text-decoration:none}`]
})
export class ForbiddenComponent {}
