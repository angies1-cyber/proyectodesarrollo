import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthRequest, AuthResponse, ModuloAcceso } from '../models/auth.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.authApiBaseUrl}/api/v1/auth`;
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  private getUserFromStorage(): AuthResponse | null {
    const userStr = localStorage.getItem('currentUser');
    if (!userStr) return null;
    try {
      const user = JSON.parse(userStr) as AuthResponse;
      if (!Array.isArray(user.roles) || !Array.isArray(user.modulos)) {
        localStorage.removeItem('currentUser');
        localStorage.removeItem('token');
        return null;
      }
      return user;
    } catch {
      localStorage.removeItem('currentUser');
      localStorage.removeItem('token');
      return null;
    }
  }

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('currentUser', JSON.stringify(res));
        this.currentUserSubject.next(res);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = token.split('.')[1]
        .replace(/-/g, '+')
        .replace(/_/g, '/');
      const paddedPayload = payload.padEnd(Math.ceil(payload.length / 4) * 4, '=');
      const decoded = JSON.parse(atob(paddedPayload));
      if (typeof decoded.exp === 'number' && decoded.exp * 1000 > Date.now()) {
        return true;
      }
    } catch {
      // A malformed token is treated like an expired session.
    }

    this.logout();
    return false;
  }

  getUser(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  hasRole(role: string): boolean {
    const user = this.getUser();
    return user ? user.roles.includes(role) : false;
  }

  isSuperAdmin(): boolean {
    return this.hasRole('ROLE_SUPER_ADMIN');
  }

  hasModule(codigo: string): boolean {
    return this.isSuperAdmin() || this.getModules().some(modulo => modulo.codigo === codigo);
  }

  hasPermission(moduloCodigo: string, permiso: string): boolean {
    if (this.isSuperAdmin()) return true;
    const modulo = this.getModules().find(item => item.codigo === moduloCodigo);
    return !!modulo?.permisos.includes(permiso);
  }

  getModules(): ModuloAcceso[] {
    return this.getUser()?.modulos ?? [];
  }

  getMenuTree(): ModuloAcceso[] {
    const map = new Map<number, ModuloAcceso>();
    this.getModules().forEach(modulo => map.set(modulo.id, { ...modulo, hijos: [] }));
    const roots: ModuloAcceso[] = [];
    map.forEach(modulo => {
      if (modulo.moduloPadreId == null) roots.push(modulo);
      else map.get(modulo.moduloPadreId)?.hijos?.push(modulo);
    });
    const sort = (items: ModuloAcceso[]): ModuloAcceso[] => items
      .sort((a, b) => a.orden - b.orden || a.nombre.localeCompare(b.nombre))
      .map(item => ({ ...item, hijos: sort(item.hijos ?? []) }));
    return sort(roots);
  }

}
