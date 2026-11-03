import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();
  const isPublicAuth = req.url.includes('/api/v1/auth/login') || req.url.includes('/api/v1/auth/register');
  const apiBaseUrls = [environment.authApiBaseUrl, environment.clinicApiBaseUrl];
  const isProtectedApi = !isPublicAuth && apiBaseUrls.some(baseUrl => req.url.startsWith(`${baseUrl}/`));

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
