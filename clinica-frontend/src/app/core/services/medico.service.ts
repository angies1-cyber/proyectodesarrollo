import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Medico, PageResponse } from '../models/clinica.model';

@Injectable({
  providedIn: 'root'
})
export class MedicoService {
  private apiUrl = 'http://localhost:8082/api/v1/medicos';

  constructor(private http: HttpClient) {}

  obtenerPaginado(page: number = 0, size: number = 50, filters?: any): Observable<PageResponse<Medico>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters) {
      if (filters.nombre) params = params.set('nombre', filters.nombre);
      if (filters.apellido) params = params.set('apellido', filters.apellido);
      if (filters.dpi) params = params.set('dpi', filters.dpi);
      if (filters.especialidadId) params = params.set('especialidadId', filters.especialidadId.toString());
      if (filters.activo !== undefined && filters.activo !== null && filters.activo !== '') {
        params = params.set('activo', filters.activo.toString());
      }
    }

    return this.http.get<PageResponse<Medico>>(this.apiUrl, { params });
  }

  obtenerTodos(): Observable<Medico[]> {
    return this.http.get<Medico[]>(`${this.apiUrl}/todos`);
  }

  obtenerPorId(id: number): Observable<Medico> {
    return this.http.get<Medico>(`${this.apiUrl}/${id}`);
  }

  crear(medico: Medico): Observable<Medico> {
    return this.http.post<Medico>(this.apiUrl, medico);
  }

  cambiarEstado(id: number, activo: boolean): Observable<Medico> {
    return this.http.patch<Medico>(`${this.apiUrl}/${id}/estado?activo=${activo}`, {});
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}