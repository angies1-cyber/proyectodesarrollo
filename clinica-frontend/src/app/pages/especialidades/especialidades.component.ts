import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EspecialidadService } from '../../core/services/especialidad.service';
import { AuthService } from '../../core/services/auth.service';
import { Especialidad, PageMetadata } from '../../core/models/clinica.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-especialidades',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="header-actions">
        <div>
          <h2>🏷️ Especialidades Médicas</h2>
          <p class="subtitle">Catálogo de ramas y especialidades disponibles en la clínica.</p>
        </div>
        <button *ngIf="can('CREATE')" (click)="toggleForm()" class="btn-primary">
          {{ showForm ? '✖ Cerrar Formulario' : 'Nueva Especialidad' }}
        </button>
      </div>

      <div *ngIf="showForm && can('CREATE')" class="form-card">
        <h3>Registrar Nueva Especialidad</h3>
        <form (ngSubmit)="guardarEspecialidad()">
          <div class="form-row">
            <div class="form-group">
              <label>Nombre de la Especialidad *</label>
              <input type="text" [(ngModel)]="nuevaEspecialidad.nombre" name="nombre" placeholder="Ej. Cardiología, Pediatría" required>
            </div>
            <div class="form-group">
              <label>Descripción</label>
              <input type="text" [(ngModel)]="nuevaEspecialidad.descripcion" name="descripcion" placeholder="Breve detalle de la rama médica...">
            </div>
          </div>

          <button type="submit" class="btn-success">Guardar Especialidad</button>
        </form>
      </div>

      <div class="filters-card">
        <div class="filters-grid">
          <div class="form-group">
            <label>Buscar por Nombre</label>
            <input type="text" [(ngModel)]="filtros.nombre" (input)="onFiltroChange()" placeholder="Escribe el nombre...">
          </div>
          <div class="form-group">
            <label>Estado</label>
            <select [(ngModel)]="filtros.activo" (change)="onFiltroChange()">
              <option value="">Todos los estados</option>
              <option [ngValue]="true">Solo Activas</option>
              <option [ngValue]="false">Solo Inactivas</option>
            </select>
          </div>
        </div>
      </div>

      <div class="table-card">
        <div class="table-header-info">
          <span>Mostrando <strong>{{ especialidades.length }}</strong> registros de un total de <strong>{{ metadata.totalRecords }}</strong> encontrados.</span>
          <div class="page-size-selector">
            <label>Tamaño de página:</label>
            <select [(ngModel)]="pageSize" (change)="onPageSizeChange()">
              <option [ngValue]="50">50</option>
              <option [ngValue]="100">100</option>
              <option [ngValue]="200">200</option>
            </select>
          </div>
        </div>

        <div class="table-responsive">
          <table class="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Descripción</th>
                <th>Estado</th>
                <th *ngIf="can('UPDATE')">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let e of especialidades">
                <td>{{ e.id }}</td>
                <td><strong>{{ e.nombre }}</strong></td>
                <td>{{ e.descripcion || '-' }}</td>
                <td>
                  <span class="status-badge" [class.active]="e.activo">
                    {{ e.activo ? 'Activa' : 'Inactiva' }}
                  </span>
                </td>
                <td *ngIf="can('UPDATE')">
                  <button *ngIf="e.activo" (click)="cambiarEstado(e, false)" class="btn-action-danger">
                    Desactivar
                  </button>
                  <button *ngIf="!e.activo" (click)="cambiarEstado(e, true)" class="btn-action-success">
                    Activar
                  </button>
                </td>
              </tr>
              <tr *ngIf="especialidades.length === 0">
                <td [attr.colspan]="can('UPDATE') ? 5 : 4" class="text-center">No se encontraron especialidades.</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="pagination-footer" *ngIf="metadata.totalPages > 0">
          <button (click)="cambiarPagina(currentPage - 1)" [disabled]="!metadata.hasPreviousPage" class="btn-pagination">
            ← Anterior
          </button>
          <span class="pagination-info">
            Página <strong>{{ metadata.page + 1 }}</strong> de <strong>{{ metadata.totalPages }}</strong>
          </span>
          <button (click)="cambiarPagina(currentPage + 1)" [disabled]="!metadata.hasNextPage" class="btn-pagination">
            Siguiente →
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-container { display: flex; flex-direction: column; gap: 1.2rem; }
    .header-actions { display: flex; justify-content: space-between; align-items: center; }
    h2 { margin: 0; color: #1e293b; }
    .subtitle { margin: 0.3rem 0 0 0; color: #64748b; font-size: 0.95rem; }
    .btn-primary { background: #b45f72; color: white; border: none; padding: 0.65rem 1.3rem; border-radius: 8px; cursor: pointer; font-weight: 700; transition: background .2s; }
    .btn-primary:hover { background: #984b5e; }
    .btn-success { background: #b45f72; color: white; border: none; padding: 0.7rem 1.5rem; border-radius: 8px; cursor: pointer; font-weight: 700; margin-top: 0.5rem; transition: background .2s; }
    .btn-success:hover { background: #984b5e; }

    .form-card, .filters-card, .table-card { background: white; padding: 1.3rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
    .form-row { display: flex; gap: 1rem; margin-bottom: 1rem; flex-wrap: wrap; }
    .form-group { flex: 1; min-width: 200px; display: flex; flex-direction: column; gap: 0.3rem; }
    .form-group label { font-size: 0.85rem; font-weight: 600; color: #475569; }
    .form-group input, .form-group select { padding: 0.6rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.95rem; }

    .filters-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; }

    .table-header-info { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; color: #475569; font-size: 0.9rem; flex-wrap: wrap; gap: 0.5rem; }
    .page-size-selector { display: flex; align-items: center; gap: 0.5rem; }
    .page-size-selector select { padding: 0.3rem 0.6rem; border: 1px solid #cbd5e1; border-radius: 4px; }

    .data-table { width: 100%; border-collapse: collapse; text-align: left; font-size: 0.9rem; }
    .data-table th, .data-table td { padding: 0.75rem 1rem; border-bottom: 1px solid #f1f5f9; }
    .data-table th { background: #f8fafc; color: #475569; font-weight: 600; }
    .status-badge { padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.75rem; font-weight: bold; background: #fee2e2; color: #991b1b; }
    .status-badge.active { background: #dcfce7; color: #166534; }

    .btn-action-danger { background: #ef4444; color: white; border: none; padding: 0.35rem 0.7rem; border-radius: 4px; font-size: 0.8rem; cursor: pointer; font-weight: 600; }
    .btn-action-success { background: #10b981; color: white; border: none; padding: 0.35rem 0.7rem; border-radius: 4px; font-size: 0.8rem; cursor: pointer; font-weight: 600; }

    .pagination-footer { display: flex; justify-content: center; align-items: center; gap: 1.5rem; margin-top: 1.2rem; padding-top: 1rem; border-top: 1px solid #f1f5f9; }
    .btn-pagination { background: #f8fafc; border: 1px solid #cbd5e1; padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer; font-weight: 600; color: #334155; }
    .btn-pagination:disabled { opacity: 0.4; cursor: not-allowed; }
    .pagination-info { font-size: 0.9rem; color: #475569; }
    .text-center { text-align: center; color: #64748b; }
  `]
})
export class EspecialidadesComponent implements OnInit {
  especialidades: Especialidad[] = [];
  metadata: PageMetadata = {
    totalRecords: 0,
    page: 0,
    pageSize: 50,
    totalPages: 0,
    hasPreviousPage: false,
    hasNextPage: false
  };

  currentPage = 0;
  pageSize = 50;
  showForm = false;

  filtros: any = {
    nombre: '',
    activo: ''
  };

  nuevaEspecialidad: Especialidad = {
    nombre: '',
    descripcion: ''
  };

  constructor(private especialidadService: EspecialidadService, private authService: AuthService) {}

  ngOnInit(): void {
    this.cargarEspecialidades();
  }

  can(permission: string): boolean { return this.authService.hasPermission('ESPECIALIDADES', permission); }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  onFiltroChange(): void {
    this.currentPage = 0;
    this.cargarEspecialidades();
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.cargarEspecialidades();
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 0 && nuevaPagina < this.metadata.totalPages) {
      this.currentPage = nuevaPagina;
      this.cargarEspecialidades();
    }
  }

  cargarEspecialidades(): void {
    this.especialidadService.obtenerPaginado(this.currentPage, this.pageSize, this.filtros).subscribe({
      next: (res) => {
        this.especialidades = res.data;
        this.metadata = res.metadata;
      },
      error: (err) => console.error('Error al cargar especialidades:', err)
    });
  }

  guardarEspecialidad(): void {
    this.especialidadService.crear(this.nuevaEspecialidad).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: '¡Especialidad creada!',
          timer: 1800,
          showConfirmButton: false
        });
        this.cargarEspecialidades();
        this.showForm = false;
        this.nuevaEspecialidad = { nombre: '', descripcion: '' };
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: err.error?.message || 'No se pudo registrar la especialidad.',
          confirmButtonColor: '#dc2626'
        });
      }
    });
  }

  cambiarEstado(e: Especialidad, activo: boolean): void {
    if (!e.id) return;
    this.especialidadService.cambiarEstado(e.id, activo).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: activo ? 'Especialidad activada' : 'Especialidad desactivada', timer: 1400, showConfirmButton: false });
        this.cargarEspecialidades();
      },
      error: (err) => Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'No se pudo cambiar el estado.' })
    });
  }
}
