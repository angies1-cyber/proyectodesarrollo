import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();
  const isPublicAuth = req.url.includes('/api/v1/auth/login') || req.url.includes('/api/v1/auth/register');
  const isProtectedApi = !isPublicAuth && (req.url.startsWith('http://localhost:8082/') || req.url.startsWith('http://localhost:8081/'));

  const request = token && isProtectedApi
    ? req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    })
    : req;

  return next(request).pipe(
    catchError(error => {
      if (error.status === 401 && isProtectedApi) {
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
