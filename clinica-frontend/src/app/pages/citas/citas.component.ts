import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CitaService } from '../../core/services/cita.service';
import { PacienteService } from '../../core/services/paciente.service';
import { MedicoService } from '../../core/services/medico.service';
import { AuthService } from '../../core/services/auth.service';
import { Cita, Paciente, Medico, PageMetadata } from '../../core/models/clinica.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-citas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page-container">
      <div class="header-actions">
        <div>
          <h2>📅 Agenda de Citas Médicas</h2>
          <p class="subtitle">Gestiona citas, programa consultas, cancela o reprograma horarios.</p>
        </div>
        <button *ngIf="can('CREATE')" (click)="toggleForm()" class="btn-primary">
          {{ showForm ? '✖ Cerrar Formulario' : 'Agendar Cita' }}
        </button>
      </div>

      <div *ngIf="showForm && can('CREATE')" class="form-card">
        <h3>Agendar Nueva Cita</h3>
        <form (ngSubmit)="guardarCita()">
          <div class="form-row">
            <div class="form-group">
              <label>Paciente *</label>
              <select [(ngModel)]="nuevaCita.pacienteId" name="pacienteId" required>
                <option [ngValue]="null" disabled selected>Selecciona un paciente</option>
                <option *ngFor="let p of listaPacientes" [ngValue]="p.id">
                  {{ p.nombre }} {{ p.apellido }} (DPI: {{ p.dpi }})
                </option>
              </select>
            </div>
            <div class="form-group">
              <label>Médico Especialista *</label>
              <select [(ngModel)]="nuevaCita.medicoId" name="medicoId" required>
                <option [ngValue]="null" disabled selected>Selecciona un médico</option>
                <option *ngFor="let m of listaMedicos" [ngValue]="m.id">
                  Dr. {{ m.nombre }} {{ m.apellido }} - {{ m.especialidadNombre }}
                </option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Fecha de la Cita *</label>
              <input type="date" [(ngModel)]="nuevaCita.fecha" name="fecha" required>
            </div>
            <div class="form-group">
              <label>Hora de la Cita *</label>
              <input type="time" [(ngModel)]="nuevaCita.hora" name="hora" required>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Motivo de la Consulta *</label>
              <input type="text" [(ngModel)]="nuevaCita.motivo" name="motivo" placeholder="Ej. Control de rutina, dolor de cabeza..." required>
            </div>
          </div>

          <button type="submit" class="btn-success">Confirmar y Agendar</button>
        </form>
      </div>

      <div class="filters-card">
        <div class="filters-grid">
          <div class="form-group">
            <label>Filtrar por Paciente</label>
            <select [(ngModel)]="filtros.pacienteId" (change)="onFiltroChange()">
              <option [ngValue]="null">Todos los pacientes</option>
              <option *ngFor="let p of listaPacientes" [ngValue]="p.id">
                {{ p.nombre }} {{ p.apellido }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Filtrar por Médico</label>
            <select [(ngModel)]="filtros.medicoId" (change)="onFiltroChange()">
              <option [ngValue]="null">Todos los médicos</option>
              <option *ngFor="let m of listaMedicos" [ngValue]="m.id">
                Dr. {{ m.nombre }} {{ m.apellido }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Estado</label>
            <select [(ngModel)]="filtros.estado" (change)="onFiltroChange()">
              <option value="">Todos los estados</option>
              <option value="PROGRAMADA">Programada</option>
              <option value="REPROGRAMADA">Reprogramada</option>
              <option value="CANCELADA">Cancelada</option>
            </select>
          </div>
          <div class="form-group">
            <label>Fecha</label>
            <input type="date" [(ngModel)]="filtros.fecha" (change)="onFiltroChange()">
          </div>
        </div>
      </div>

      <div class="table-card">
        <div class="table-header-info">
          <span>Mostrando <strong>{{ citas.length }}</strong> citas de un total de <strong>{{ metadata.totalRecords }}</strong> registradas.</span>
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
                <th>Paciente</th>
                <th>Médico / Especialidad</th>
                <th>Fecha y Hora</th>
                <th>Motivo</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let c of citas">
                <td>{{ c.id }}</td>
                <td>
                  <strong>{{ c.pacienteNombre }} {{ c.pacienteApellido }}</strong>
                  <small class="d-block text-muted">DPI: {{ c.pacienteDpi }} | Tel: {{ c.pacienteTelefono }}</small>
                </td>
                <td>
                  <strong>Dr. {{ c.medicoNombre }} {{ c.medicoApellido }}</strong>
                  <small class="d-block text-muted">{{ c.especialidadNombre }}</small>
                </td>
                <td>
                  <div>📅 {{ c.fecha }}</div>
                  <small class="text-muted">⏰ {{ c.hora }}</small>
                </td>
                <td>{{ c.motivo }}</td>
                <td>
                  <span class="status-badge" [ngClass]="c.estado?.toLowerCase()">
                    {{ c.estado }}
                  </span>
                </td>
                <td>
                  <div class="action-buttons" *ngIf="c.estado !== 'CANCELADA'">
                    <button *ngIf="can('UPDATE')" (click)="reprogramarCita(c)" class="btn-action-warning">
                      Reprogramar
                    </button>
                    <button *ngIf="can('UPDATE')" (click)="cancelarCita(c)" class="btn-action-danger">
                      Cancelar
                    </button>
                  </div>
                  <span *ngIf="c.estado === 'CANCELADA'" class="text-muted">-</span>
                </td>
              </tr>
              <tr *ngIf="citas.length === 0">
                <td colspan="7" class="text-center">No se encontraron citas médicas con los criterios especificados.</td>
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
    
    .status-badge { padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.75rem; font-weight: bold; }
    .status-badge.programada { background: #dbeafe; color: #1e40af; }
    .status-badge.reprogramada { background: #fef3c7; color: #92400e; }
    .status-badge.cancelada { background: #fee2e2; color: #991b1b; }
    .status-badge.completada { background: #dcfce7; color: #166534; }

    .action-buttons { display: flex; gap: 0.4rem; }
    .btn-action-warning { background: #f59e0b; color: white; border: none; padding: 0.35rem 0.6rem; border-radius: 4px; font-size: 0.8rem; cursor: pointer; font-weight: 600; }
    .btn-action-danger { background: #ef4444; color: white; border: none; padding: 0.35rem 0.6rem; border-radius: 4px; font-size: 0.8rem; cursor: pointer; font-weight: 600; }

    .pagination-footer { display: flex; justify-content: center; align-items: center; gap: 1.5rem; margin-top: 1.2rem; padding-top: 1rem; border-top: 1px solid #f1f5f9; }
    .btn-pagination { background: #f8fafc; border: 1px solid #cbd5e1; padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer; font-weight: 600; color: #334155; }
    .btn-pagination:disabled { opacity: 0.4; cursor: not-allowed; }
    .pagination-info { font-size: 0.9rem; color: #475569; }
    .text-center { text-align: center; color: #64748b; }
    .d-block { display: block; }
    .text-muted { color: #94a3b8; }
  `]
})
export class CitasComponent implements OnInit {
  citas: Cita[] = [];
  listaPacientes: Paciente[] = [];
  listaMedicos: Medico[] = [];

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
    pacienteId: null,
    medicoId: null,
    estado: '',
    fecha: ''
  };

  nuevaCita: any = {
    pacienteId: null,
    medicoId: null,
    fecha: '',
    hora: '',
    motivo: ''
  };

  constructor(
    private citaService: CitaService,
    private pacienteService: PacienteService,
    private medicoService: MedicoService,
    private authService: AuthService
  ) {}

  can(permission: string): boolean { return this.authService.hasPermission('CITAS', permission); }

  ngOnInit(): void {
    this.cargarSelects();
    this.cargarCitas();
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
  }

  cargarSelects(): void {
    this.pacienteService.obtenerTodos().subscribe({
      next: (data) => this.listaPacientes = data,
      error: (err) => console.error('Error al cargar pacientes para selects:', err)
    });

    this.medicoService.obtenerTodos().subscribe({
      next: (data) => this.listaMedicos = data,
      error: (err) => console.error('Error al cargar médicos para selects:', err)
    });
  }

  onFiltroChange(): void {
    this.currentPage = 0;
    this.cargarCitas();
  }

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.cargarCitas();
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 0 && nuevaPagina < this.metadata.totalPages) {
      this.currentPage = nuevaPagina;
      this.cargarCitas();
    }
  }

  cargarCitas(): void {
    this.citaService.obtenerPaginado(this.currentPage, this.pageSize, this.filtros).subscribe({
      next: (res) => {
        this.citas = res.data;
        this.metadata = res.metadata;
      },
      error: (err) => console.error('Error al cargar citas paginadas:', err)
    });
  }

  guardarCita(): void {
    if (!this.nuevaCita.pacienteId || !this.nuevaCita.medicoId) {
      Swal.fire({ icon: 'warning', title: 'Atención', text: 'Debes seleccionar paciente y médico.' });
      return;
    }

    this.citaService.crear(this.nuevaCita).subscribe({
      next: () => {
        Swal.fire({
          icon: 'success',
          title: '¡Cita agendada con éxito!',
          timer: 1800,
          showConfirmButton: false
        });
        this.cargarCitas();
        this.showForm = false;
        this.nuevaCita = { pacienteId: null, medicoId: null, fecha: '', hora: '', motivo: '' };
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Error al agendar cita',
          text: err.error?.message || 'Verifica los datos de la cita.',
          confirmButtonColor: '#dc2626'
        });
      }
    });
  }

  cancelarCita(c: Cita): void {
    if (!c.id) return;
    Swal.fire({
      title: '¿Cancelar Cita Médica?',
      text: `¿Deseas cancelar la cita de ${c.pacienteNombre} con el Dr. ${c.medicoNombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, cancelar cita',
      cancelButtonText: 'No',
      confirmButtonColor: '#dc2626'
    }).then((res) => {
      if (res.isConfirmed && c.id) {
        this.citaService.cancelar(c.id).subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Cita cancelada', timer: 1500, showConfirmButton: false });
            this.cargarCitas();
          },
          error: (err) => Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'No se pudo cancelar la cita.' })
        });
      }
    });
  }

  reprogramarCita(c: Cita): void {
    if (!c.id) return;
    Swal.fire({
      title: 'Reprogramar Cita Médica',
      html: `
        <p style="font-size: 0.9rem; color: #475569;">Paciente: <strong>${c.pacienteNombre} ${c.pacienteApellido}</strong></p>
        <div style="text-align: left; margin-top: 1rem;">
          <label style="font-size: 0.85rem; font-weight: 600;">Nueva Fecha:</label>
          <input type="date" id="reprogFecha" class="swal2-input" value="${c.fecha}">
          <label style="font-size: 0.85rem; font-weight: 600; margin-top: 0.5rem; display: block;">Nueva Hora:</label>
          <input type="time" id="reprogHora" class="swal2-input" value="${c.hora}">
        </div>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Guardar Reprogramación',
      cancelButtonText: 'Volver',
      confirmButtonColor: '#f59e0b',
      preConfirm: () => {
        const fecha = (document.getElementById('reprogFecha') as HTMLInputElement).value;
        const hora = (document.getElementById('reprogHora') as HTMLInputElement).value;
        if (!fecha || !hora) {
          Swal.showValidationMessage('Debes ingresar tanto fecha como hora');
          return false;
        }
        return { fecha, hora };
      }
    }).then((res) => {
      if (res.isConfirmed && res.value && c.id) {
        this.citaService.reprogramar(c.id, res.value.fecha, res.value.hora).subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: 'Cita reprogramada', timer: 1500, showConfirmButton: false });
            this.cargarCitas();
          },
          error: (err) => Swal.fire({ icon: 'error', title: 'Error', text: err.error?.message || 'No se pudo reprogramar la cita.' })
        });
      }
    });
  }
}
