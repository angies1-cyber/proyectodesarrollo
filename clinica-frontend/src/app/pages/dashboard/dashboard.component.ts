import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { PacienteService } from '../../core/services/paciente.service';
import { DashboardMetricas } from '../../core/models/clinica.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard-container">
      <div class="welcome-banner">
        <div>
          <h2>👋 ¡Bienvenid&#64;, {{ user?.username }}!</h2>
          <p class="role-desc">Sesión activa con el rol: 
            <strong [class.text-admin]="isAdmin()">{{ user?.roles?.join(', ') }}</strong>
          </p>
        </div>
      </div>

      <!-- Métricas dinámicas obtenidas del Backend -->
      <div class="stats-grid" *ngIf="metricas">
        <div class="stat-card blue">
          <div class="stat-icon">👥</div>
          <div class="stat-info">
            <span class="stat-value">{{ metricas.totalPacientes }}</span>
            <span class="stat-label">Total Pacientes</span>
          </div>
          <div class="stat-sub">
            <span>✅ {{ metricas.pacientesActivos }} activos</span>
            <span>⛔ {{ metricas.pacientesInactivos }} inactivos</span>
          </div>
        </div>

        <div class="stat-card teal">
          <div class="stat-icon">🩺</div>
          <div class="stat-info">
            <span class="stat-value">{{ metricas.totalMedicos }}</span>
            <span class="stat-label">Médicos Registrados</span>
          </div>
          <div class="stat-sub">
            <a routerLink="/medicos">Gestionar médicos →</a>
          </div>
        </div>

        <div class="stat-card indigo">
          <div class="stat-icon">📅</div>
          <div class="stat-info">
            <span class="stat-value">{{ metricas.totalCitas }}</span>
            <span class="stat-label">Citas Médicas</span>
          </div>
          <div class="stat-sub">
            <a routerLink="/citas">Ver agenda →</a>
          </div>
        </div>
      </div>

      <!-- Distribución de Rango de Edades requerida -->
      <div class="chart-card" *ngIf="metricas">
        <div class="chart-header">
          <h3>📊 Distribución por Rango de Edades de Pacientes</h3>
          <span>Obtenido en tiempo real desde el Backend</span>
        </div>

        <div class="age-distribution">
          <div class="age-group" *ngFor="let item of rangosEdadArray">
            <div class="age-info">
              <span class="age-label">{{ item.label }}</span>
              <span class="age-count">{{ item.count }} pacientes ({{ calcularPorcentaje(item.count) }}%)</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" [style.width.%]="calcularPorcentaje(item.count)"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="grid-cards">
        <div class="card card-blue" routerLink="/pacientes">
          <h3>📋 Módulo de Pacientes</h3>
          <p>Listado paginado server-side, filtros dinámicos, registro e inhabilitación.</p>
          <span class="card-link">Ir a Pacientes →</span>
        </div>

        <div class="card card-teal" routerLink="/medicos">
          <h3>🩺 Módulo de Médicos</h3>
          <p>Gestión de personal médico, especialidades asignadas y horarios.</p>
          <span class="card-link">Ir a Médicos →</span>
        </div>

        <div class="card card-indigo" routerLink="/citas">
          <h3>📅 Módulo de Citas</h3>
          <p>Creación de citas médicas, cancelación y reprogramación.</p>
          <span class="card-link">Ir a Citas →</span>
        </div>

        <div class="card card-purple" *ngIf="canViewEspecialidades()" routerLink="/especialidades">
          <h3>🏷️ Especialidades</h3>
          <p>Administración de especialidades médicas clínicas.</p>
          <span class="card-link">Ir a Especialidades →</span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container { display: flex; flex-direction: column; gap: 1.5rem; }
    .welcome-banner { background: white; padding: 1.8rem; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
    h2 { color: #1e293b; margin: 0 0 0.5rem 0; }
    .role-desc { color: #64748b; margin: 0; font-size: 1.05rem; }
    .text-admin { color: #b45f72; font-weight: bold; }

    .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.2rem; }
    .stat-card { background: white; padding: 1.5rem; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); display: flex; flex-direction: column; gap: 1rem; border-left: 5px solid #cbd5e1; }
    .stat-card.blue { border-left-color: #0284c7; }
    .stat-card.teal { border-left-color: #0d9488; }
    .stat-card.indigo { border-left-color: #6366f1; }
    .stat-icon { font-size: 2rem; }
    .stat-info { display: flex; flex-direction: column; }
    .stat-value { font-size: 2rem; font-weight: bold; color: #1e293b; }
    .stat-label { font-size: 0.95rem; color: #64748b; }
    .stat-sub { display: flex; justify-content: space-between; font-size: 0.85rem; color: #64748b; border-top: 1px solid #f1f5f9; padding-top: 0.6rem; }
    .stat-sub a { color: #0284c7; text-decoration: none; font-weight: 600; }

    .chart-card { background: white; padding: 1.8rem; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
    .chart-header { margin-bottom: 1.5rem; }
    .chart-header h3 { margin: 0 0 0.3rem 0; color: #1e293b; }
    .chart-header span { color: #64748b; font-size: 0.9rem; }

    .age-distribution { display: flex; flex-direction: column; gap: 1.2rem; }
    .age-group { display: flex; flex-direction: column; gap: 0.4rem; }
    .age-info { display: flex; justify-content: space-between; font-size: 0.95rem; color: #334155; font-weight: 600; }
    .progress-bar { height: 12px; background: #e2e8f0; border-radius: 6px; overflow: hidden; }
    .progress-fill { height: 100%; background: linear-gradient(90deg, #0284c7, #38bdf8); border-radius: 6px; transition: width 0.5s ease; min-width: 4px; }

    .grid-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1.2rem; }
    .card { padding: 1.5rem; border-radius: 10px; color: white; cursor: pointer; transition: transform 0.2s, box-shadow 0.2s; display: flex; flex-direction: column; justify-content: space-between; }
    .card:hover { transform: translateY(-3px); box-shadow: 0 6px 12px rgba(0,0,0,0.15); }
    .card-blue { background: linear-gradient(135deg, #0284c7, #2563eb); }
    .card-teal { background: linear-gradient(135deg, #0d9488, #059669); }
    .card-indigo { background: linear-gradient(135deg, #6366f1, #4f46e5); }
    .card-purple { background: linear-gradient(135deg, #8b5cf6, #7c3aed); }
    .card h3 { margin: 0 0 0.5rem 0; }
    .card p { margin: 0 0 1rem 0; font-size: 0.9rem; opacity: 0.9; }
    .card-link { font-weight: bold; font-size: 0.9rem; text-decoration: underline; }
  `]
})
export class DashboardComponent implements OnInit {
  user = this.authService.getUser();
  metricas?: DashboardMetricas;

  constructor(private authService: AuthService, private pacienteService: PacienteService) {}

  ngOnInit(): void {
    this.pacienteService.obtenerDashboardMetricas().subscribe({
      next: (data) => this.metricas = data,
      error: (err) => console.error('Error al cargar métricas del dashboard:', err)
    });
  }

  isAdmin(): boolean {
    return this.authService.hasRole('ROLE_ADMIN') || this.authService.isSuperAdmin();
  }

  canViewEspecialidades(): boolean { return this.authService.hasPermission('ESPECIALIDADES', 'READ'); }


  get rangosEdadArray(): { label: string; count: number }[] {
    if (!this.metricas || !this.metricas.rangosEdad) return [];
    const labelsMap: { [key: string]: string } = {
      '0-17': 'Menores de edad (0 a 17 años)',
      '18-30': 'Jóvenes adultos (18 a 30 años)',
      '31-50': 'Adultos (31 a 50 años)',
      '51-65': 'Adultos mayores (51 a 65 años)',
      '65+': 'Tercera edad (65+ años)'
    };
    return ['0-17', '18-30', '31-50', '51-65', '65+'].map(k => ({
      label: labelsMap[k] || k,
      count: this.metricas?.rangosEdad[k] || 0
    }));
  }

  calcularPorcentaje(count: number): number {
    if (!this.metricas || this.metricas.totalPacientes === 0) return 0;
    return Math.round((count / this.metricas.totalPacientes) * 100);
  }
}
