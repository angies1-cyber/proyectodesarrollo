import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

import Swal from 'sweetalert2';

export const roleGuard = (requiredRole: string): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isLoggedIn() && authService.hasRole(requiredRole)) {
      return true;
    }

    Swal.fire({
      icon: 'error',
      title: 'Acceso Denegado',
      text: 'No posees permisos para acceder a esta sección.',
      confirmButtonColor: '#dc2626'
    });
    router.navigate(['/dashboard']);
    return false;
  };
};

export const permissionGuard = (moduleCode: string, permission = 'READ'): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    if (authService.isLoggedIn() && authService.hasPermission(moduleCode, permission)) return true;
    return router.createUrlTree(['/forbidden']);
  };
};
