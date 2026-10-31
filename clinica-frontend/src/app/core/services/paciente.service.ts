import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Paciente, PageResponse, DashboardMetricas } from '../models/clinica.model';

@Injectable({
  providedIn: 'root'
})
export class PacienteService {
  private apiUrl = 'http://localhost:8082/api/v1/pacientes';

  constructor(private http: HttpClient) {}

  obtenerPaginado(page: number = 0, size: number = 50, filters?: any): Observable<PageResponse<Paciente>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters) {
      if (filters.nombre) params = params.set('nombre', filters.nombre);
      if (filters.apellido) params = params.set('apellido', filters.apellido);
      if (filters.dpi) params = params.set('dpi', filters.dpi);
      if (filters.telefono) params = params.set('telefono', filters.telefono);
      if (filters.correo) params = params.set('correo', filters.correo);
      if (filters.activo !== undefined && filters.activo !== null && filters.activo !== '') {
        params = params.set('activo', filters.activo.toString());
      }
    }

    return this.http.get<PageResponse<Paciente>>(this.apiUrl, { params });
  }

  obtenerTodos(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(`${this.apiUrl}/todos`);
  }

  obtenerPorId(id: number): Observable<Paciente> {
    return this.http.get<Paciente>(`${this.apiUrl}/${id}`);
  }

  obtenerDashboardMetricas(): Observable<DashboardMetricas> {
    return this.http.get<DashboardMetricas>(`${this.apiUrl}/dashboard`);
  }

  crear(paciente: Paciente): Observable<Paciente> {
    return this.http.post<Paciente>(this.apiUrl, paciente);
  }

  inhabilitar(id: number): Observable<Paciente> {
    return this.http.patch<Paciente>(`${this.apiUrl}/${id}/inhabilitar`, {});
  }

  habilitar(id: number): Observable<Paciente> {
    return this.http.patch<Paciente>(`${this.apiUrl}/${id}/habilitar`, {});
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
