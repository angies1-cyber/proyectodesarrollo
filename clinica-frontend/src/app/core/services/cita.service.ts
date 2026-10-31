import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cita, PageResponse } from '../models/clinica.model';

@Injectable({
  providedIn: 'root'
})
export class CitaService {
  private apiUrl = 'http://localhost:8082/api/v1/citas';

  constructor(private http: HttpClient) {}

  obtenerPaginado(page: number = 0, size: number = 50, filters?: any): Observable<PageResponse<Cita>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters) {
      if (filters.pacienteId) params = params.set('pacienteId', filters.pacienteId.toString());
      if (filters.medicoId) params = params.set('medicoId', filters.medicoId.toString());
      if (filters.estado) params = params.set('estado', filters.estado);
      if (filters.fecha) params = params.set('fecha', filters.fecha);
    }

    return this.http.get<PageResponse<Cita>>(this.apiUrl, { params });
  }

  obtenerPorId(id: number): Observable<Cita> {
    return this.http.get<Cita>(`${this.apiUrl}/${id}`);
  }

  crear(cita: { pacienteId: number; medicoId: number; fecha: string; hora: string; motivo: string }): Observable<Cita> {
    return this.http.post<Cita>(this.apiUrl, cita);
  }

  cancelar(id: number): Observable<Cita> {
    return this.http.patch<Cita>(`${this.apiUrl}/${id}/cancelar`, {});
  }

  reprogramar(id: number, nuevaFecha: string, nuevaHora: string): Observable<Cita> {
    return this.http.patch<Cita>(`${this.apiUrl}/${id}/reprogramar`, { fecha: nuevaFecha, hora: nuevaHora });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}