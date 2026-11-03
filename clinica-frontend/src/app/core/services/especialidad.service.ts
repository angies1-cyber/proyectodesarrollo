import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Especialidad, PageResponse } from '../models/clinica.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EspecialidadService {
  private apiUrl = `${environment.clinicApiBaseUrl}/api/v1/especialidades`;

  constructor(private http: HttpClient) {}

  obtenerPaginado(page: number = 0, size: number = 50, filters?: any): Observable<PageResponse<Especialidad>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters) {
      if (filters.nombre) params = params.set('nombre', filters.nombre);
      if (filters.activo !== undefined && filters.activo !== null && filters.activo !== '') {
        params = params.set('activo', filters.activo.toString());
      }
    }

    return this.http.get<PageResponse<Especialidad>>(this.apiUrl, { params });
  }

  obtenerTodas(): Observable<Especialidad[]> {
    return this.http.get<Especialidad[]>(`${this.apiUrl}/todas`);
  }

  obtenerPorId(id: number): Observable<Especialidad> {
    return this.http.get<Especialidad>(`${this.apiUrl}/${id}`);
  }

  crear(especialidad: Especialidad): Observable<Especialidad> {
    return this.http.post<Especialidad>(this.apiUrl, especialidad);
  }

  cambiarEstado(id: number, activo: boolean): Observable<Especialidad> {
    return this.http.patch<Especialidad>(`${this.apiUrl}/${id}/estado?activo=${activo}`, {});
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
