import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CatalogoSeguridad, RolAdmin } from '../models/usuario-admin.model';

export interface GuardarRolRequest {
  nombre: string;
  descripcion: string;
  activo: boolean;
  sistemaId: number;
  modulos: { moduloId: number; permisoIds: number[] }[];
}

@Injectable({ providedIn: 'root' })
export class RolAdminService {
  private readonly apiUrl = 'http://localhost:8081/api/v1/admin/roles';
  constructor(private http: HttpClient) {}
  listar(): Observable<RolAdmin[]> { return this.http.get<RolAdmin[]>(this.apiUrl); }
  catalogo(): Observable<CatalogoSeguridad> { return this.http.get<CatalogoSeguridad>(`${this.apiUrl}/catalogo`); }
  crear(request: GuardarRolRequest): Observable<RolAdmin> { return this.http.post<RolAdmin>(this.apiUrl, request); }
  actualizar(id: number, request: GuardarRolRequest): Observable<RolAdmin> { return this.http.put<RolAdmin>(`${this.apiUrl}/${id}`, request); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.apiUrl}/${id}`); }
}
