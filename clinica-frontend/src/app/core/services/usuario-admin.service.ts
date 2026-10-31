import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CrearUsuarioRequest, UsuarioAdmin } from '../models/usuario-admin.model';

@Injectable({ providedIn: 'root' })
export class UsuarioAdminService {
  private readonly apiUrl = 'http://localhost:8081/api/v1/admin/usuarios';

  constructor(private http: HttpClient) {}

  obtenerTodos(): Observable<UsuarioAdmin[]> {
    return this.http.get<UsuarioAdmin[]>(this.apiUrl);
  }

  crear(usuario: CrearUsuarioRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, usuario);
  }

  cambiarRoles(id: number, roleIds: number[]): Observable<UsuarioAdmin> {
    return this.http.patch<UsuarioAdmin>(`${this.apiUrl}/${id}/roles`, { roleIds });
  }

  cambiarEstado(id: number, enabled: boolean): Observable<UsuarioAdmin> {
    return this.http.patch<UsuarioAdmin>(`${this.apiUrl}/${id}/enabled`, { enabled });
  }

  resetPassword(id: number, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/reset-password`, { newPassword });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

}
