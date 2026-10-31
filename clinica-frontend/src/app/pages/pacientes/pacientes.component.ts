import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PacienteService } from '../../core/services/paciente.service';
import { AuthService } from '../../core/services/auth.service';
import { Paciente, PageMetadata } from '../../core/models/clinica.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-pacientes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="header-actions">
        <div>
          <h2>📋 Gestión de Pacientes</h2>
          <p class="subtitle">Administra los pacientes, realiza búsquedas y gestiona su estado.</p>
        </div>
        <button *ngIf="can('CREATE')" (click)="toggleForm()" class="btn-primary">
          {{ showForm ? '✖ Cerrar Formulario' : 'Nuevo Paciente' }}
        </button>
      </div>

      <div *ngIf="showForm && can('CREATE')" class="form-card">
        <h3>Registrar Nuevo Paciente</h3>
        <form (ngSubmit)="guardarPaciente()">
          <div class="form-row">
            <div class="form-group">
              <label>Nombre *</label>
              <input type="text" [(ngModel)]="nuevoPaciente.nombre" name="nombre" placeholder="Ej. Maria" required>
            </div>
            <div class="form-group">
              <label>Apellido *</label>
              <input type="text" [(ngModel)]="nuevoPaciente.apellido" name="apellido" placeholder="Ej. Lopez" required>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>DPI (13 dígitos) *</label>
              <input type="text" [(ngModel)]="nuevoPaciente.dpi" name="dpi" placeholder="Ej. 1234567890123" maxlength="13" required>
            </div>
            <div class="form-group">
              <label>Teléfono Principal (8 dígitos) *</label>
              <input type="text" [(ngModel)]="nuevoPaciente.telefono" name="telefono" placeholder="Ej. 55554444" maxlength="8" required>
            </div>
            <div class="form-group">
              <label>Teléfono Alterno (Opcional)</label>
              <input type="text" [(ngModel)]="nuevoPaciente.telefonoAlterno" name="telefonoAlterno" placeholder="Ej. 44443333" maxlength="8">
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Correo Electrónico *</label>
              <input type="email" [(ngModel)]="nuevoPaciente.correo" name="correo" placeholder="ejemplo@correo.com" required>
            </div>
            <div class="form-group">
              <label>Fecha Nacimiento *</label>
              <input type="date" [(ngModel)]="nuevoPaciente.fechaNacimiento" name="fechaNacimiento" required>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Dirección *</label>
              <input type="text" [(ngModel)]="nuevoPaciente.direccion" name="direccion" placeholder="Ej. 5ta Avenida 12-34 Zona 1" required>
            </div>
          </div>

          <button type="submit" class="btn-success">Guardar en Base de Datos</button>
        </form>
      </div>

      <div class="filters-card">
        <div class="filters-grid">
          <div class="form-group">
            <label>Buscar por Nombre</label>
            <input type="text" [(ngModel)]="filtros.nombre" (input)="onFiltroChange()" placeholder="Escribe un nombre...">
          </div>
          <div class="form-group">
            <label>Buscar por Apellido</label>
            <input type="text" [(ngModel)]="filtros.apellido" (input)="onFiltroChange()" placeholder="Escribe un apellido...">
          </div>
          <div class="form-group">
            <label>Buscar por DPI</label>
            <input type="text" [(ngModel)]="filtros.dpi" (input)="onFiltroChange()" placeholder="DPI exacto o parcial...">
          </div>
          <div class="form-group">
            <label>Estado</label>
            <select [(ngModel)]="filtros.activo" (change)="onFiltroChange()">
              <option value="">Todos los estados</option>
              <option [ngValue]="true">Solo Activos</option>
              <option [ngValue]="false">Solo Inhabilitados</option>
            </select>
          </div>
        </div>
      </div>

      <div class="table-card">
        <div class="table-header-info">
          <span>Mostrando <strong>{{ pacientes.length }}</strong> registros de un total de <strong>{{ metadata.totalRecords }}</strong> encontrados.</span>
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
                <th>Nombre Completo</th>
                <th>DPI</th>
                <th>Teléfonos</th>
                <th>Correo</th>
                <th>Dirección</th>
                <th>Fecha Creación</th>
                <th>Fecha Inh.</th>
                <th>Estado</th>
                <th *ngIf="can('UPDATE')">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let p of pacientes">
                <td>{{ p.id }}</td>
                <td *ngIf="can('UPDATE')">
                  <strong>{{ p.nombre }} {{ p.apellido }}</strong>
                  <small class="d-block text-muted">{{ p.edad ? p.edad + ' años' : '-' }}</small>
                </td>
                <td>{{ p.dpi }}</td>
                <td>
                  <div>📞 {{ p.telefono }}</div>
                  <small *ngIf="p.telefonoAlterno" class="text-muted">Alt: {{ p.telefonoAlterno }}</small>
                </td>
                <td>{{ p.correo }}</td>
                <td>{{ p.direccion || '-' }}</td>
                <td>{{ p.fechaCreacion || '-' }}</td>
                <td>{{ p.fechaInhabilitacion || '-' }}</td>
                <td>
                  <span class="status-badge" [class.active]="p.activo">
                    {{ p.activo ? 'Activo' : 'Inhabilitado' }}
                  </span>
                </td>
                <td>
                  <button *ngIf="p.activo" (click)="inhabilitarPaciente(p)" class="btn-action-danger">
                    Inhabilitar
                  </button>
                  <button *ngIf="!p.activo" (click)="habilitarPaciente(p)" class="btn-action-success">
                    Habilitar
                  </button>
                </td>
              </tr>
              <tr *ngIf="pacientes.length === 0">
                <td colspan="10" class="text-center">No se encontraron pacientes con los filtros seleccionados.</td>
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
    .btn-action-danger:hover { background: #dc2626; }
    .btn-action-success { background: #10b981; color: white; border: none; padding: 0.35rem 0.7rem; border-radius: 4px; font-size: 0.8rem; cursor: pointer; font-weight: 600; }
    .btn-action-success:hover { background: #059669; }

    .pagination-footer { display: flex; justify-content: center; align-items: center; gap: 1.5rem; margin-top: 1.2rem; padding-top: 1rem; border-top: 1px solid #f1f5f9; }
    .btn-pagination { background: #f8fafc; border: 1px solid #cbd5e1; padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer; font-weight: 600; color: #334155; }
    .btn-pagination:disabled { opacity: 0.4; cursor: not-allowed; }
    .pagination-info { font-size: 0.9rem; color: #475569; }
    .text-center { text-align: center; color: #64748b; }
    .d-block { display: block; }
    .text-muted { color: #94a3b8; }
  `]
})
export class PacientesComponent implements OnInit {
  pacientes: Paciente[] = [];
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
    apellido: '',
    dpi: '',
    activo: ''
  };

  nuevoPaciente: Paciente = {
    nombre: '',
    apellido: '',
    dpi: '',
    telefono: '',
    correo: '',
    fechaNacimiento: '',
    direccion: '',
    telefonoAlterno: ''
  };

  constructor(private pacienteService: PacienteService, private authService: AuthService) {}

  ngOnInit(): void {
    this.cargarPacientes();
  }

  can(permission: string): boolean { return this.authService.hasPermission('PACIENTES', permission); }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  onFiltroChange(): void {
    this.currentPage = 0;
    this.cargarPacientes();
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.cargarPacientes();
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 0 && nuevaPagina < this.metadata.totalPages) {
      this.currentPage = nuevaPagina;
      this.cargarPacientes();
    }
  }

  cargarPacientes(): void {
    this.pacienteService.obtenerPaginado(this.currentPage, this.pageSize, this.filtros).subscribe({
      next: (res) => {
        this.pacientes = res.data;
        this.metadata = res.metadata;
      },
      error: (err) => console.error('Error al cargar pacientes paginados:', err)
    });
  }

  guardarPaciente(): void {
    this.pacienteService.crear(this.nuevoPaciente).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: '¡Registrado con éxito!',
          text: 'El paciente ha sido guardado correctamente.',
          timer: 2000,
          showConfirmButton: false
        });
        this.cargarPacientes();
        this.showForm = false;
        this.nuevoPaciente = {
          nombre: '',
          apellido: '',
          dpi: '',
          telefono: '',
          correo: '',
          fechaNacimiento: '',
          direccion: '',
          telefonoAlterno: ''
        };
      },
      error: (err) => {
        console.error('Error al registrar paciente:', err);
        let mensaje = 'Verifica los datos del formulario.';
        if (err.error?.message) {
          mensaje = err.error.message;
        } else if (err.error?.errors && Array.isArray(err.error.errors)) {
          mensaje = err.error.errors.map((e: any) => e.defaultMessage || e.message).join('<br>');
        }
        Swal.fire({
          icon: 'error',
          title: 'Error al Registrar Paciente',
          html: mensaje,
          confirmButtonColor: '#dc2626'
        });
      }
    });
  }

  inhabilitarPaciente(p: Paciente): void {
    if (!this.can('UPDATE')) return;
    Swal.fire({ title: '¿Inhabilitar paciente?', text: `¿Estás seguro de inhabilitar a ${p.nombre} ${p.apellido}?`, icon: 'warning', showCancelButton: true, confirmButtonText: 'Sí, inhabilitar', cancelButtonText: 'Cancelar', confirmButtonColor: '#dc2626' }).then(res => {
      if (res.isConfirmed && p.id) this.pacienteService.inhabilitar(p.id).subscribe({ next: () => { Swal.fire({ icon: 'success', title: 'Paciente inhabilitado', timer: 1500, showConfirmButton: false }); this.cargarPacientes(); }, error: err => Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'No se pudo inhabilitar.' }) });
    });
  }

  habilitarPaciente(p: Paciente): void {
    if (!p.id) return;
    this.pacienteService.habilitar(p.id).subscribe({
      next: () => {
        Swal.fire({ icon: 'success', title: 'Paciente habilitado', timer: 1500, showConfirmButton: false });
        this.cargarPacientes();
      },
      error: (err) => Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'No se pudo habilitar.' })
    });
  }
}
