import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { CrearUsuarioRequest, RolAdmin, UsuarioAdmin } from '../../core/models/usuario-admin.model';
import { AuthService } from '../../core/services/auth.service';
import { UsuarioAdminService } from '../../core/services/usuario-admin.service';
import { RolAdminService } from '../../core/services/rol-admin.service';
import { finalize, forkJoin, timeout } from 'rxjs';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="admin-page">
      <div class="admin-heading">
        <div>
          <p class="section-kicker">Acceso restringido</p>
          <h2>Administración de usuarios</h2>
          <p>Gestiona cuentas, roles, restablecimiento de contraseñas y acceso al sistema.</p>
        </div>
        <button *ngIf="can('CREATE')" class="old-rose-button" type="button" (click)="showForm = !showForm">
          {{ showForm ? 'Cerrar formulario' : 'Nuevo usuario' }}
        </button>
      </div>

      <form *ngIf="showForm" class="admin-form" (ngSubmit)="crearUsuario()" #usuarioForm="ngForm">
        <div class="admin-form-title">
          <h3>Crear usuario</h3>
          <span>Todos los campos son obligatorios</span>
        </div>
        <div class="admin-form-grid">
          <label>Usuario
            <input type="text" name="username" [(ngModel)]="nuevoUsuario.username" autocomplete="off" required placeholder="Ej. jperez">
          </label>
          <label>Correo electrónico
            <input type="email" name="email" [(ngModel)]="nuevoUsuario.email" autocomplete="off" required email placeholder="ejemplo@clinica.com">
          </label>
          <label>Contraseña
            <input type="password" name="password" [(ngModel)]="nuevoUsuario.password" autocomplete="new-password" required minlength="6" placeholder="Mínimo 6 caracteres">
          </label>
          <label>Rol asignado
            <select name="selectedRole" [(ngModel)]="selectedNewUserRoleId" required>
              <option *ngFor="let rol of rolesDisponibles" [ngValue]="rol.id">{{ rol.nombre }} - {{ rol.descripcion }}</option>
            </select>
          </label>
        </div>
        <div class="admin-form-actions">
          <button class="save-user-button" type="submit" [disabled]="usuarioForm.invalid || saving">
            {{ saving ? 'Guardando...' : 'Crear usuario' }}
          </button>
        </div>
      </form>

      <div class="admin-table-card">
        <div class="table-heading">
          <h3>Usuarios registrados</h3>
          <span>{{ usuarios.length }} {{ usuarios.length === 1 ? 'usuario' : 'usuarios' }}</span>
        </div>
        <div class="admin-loading" *ngIf="loading">Cargando usuarios y roles...</div>
        <div class="load-error" *ngIf="!loading && loadError">
          {{ loadError }} 
          <button type="button" (click)="cargarDatos()">Reintentar</button>
        </div>
        <div class="table-responsive" *ngIf="!loading && !loadError">
          <table class="admin-table">
            <thead>
              <tr>
                <th>Usuario</th>
                <th>Correo</th>
                <th>Rol</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let usuario of usuarios; trackBy: trackById">
                <td data-label="Usuario">
                  <strong>{{ usuario.username }}</strong>
                  <small class="d-block text-muted">ID: #{{ usuario.id }}</small>
                </td>
                <td data-label="Correo">{{ usuario.email }}</td>
                <td data-label="Rol">
                  <div *ngIf="can('UPDATE'); else soloTextoRol" class="role-selector-container">
                    <select class="role-select" [(ngModel)]="selectedUserRoles[usuario.id]" (ngModelChange)="cambiarRolUsuario(usuario, $event)" [disabled]="updatingIds.has(usuario.id)">
                      <option *ngFor="let rol of rolesDisponibles" [ngValue]="rol.id">{{ rol.nombre }}</option>
                    </select>
                  </div>
                  <ng-template #soloTextoRol>
                    <span class="role-tag" *ngFor="let r of usuario.roles">{{ r.nombre }}</span>
                  </ng-template>
                </td>
                <td data-label="Estado">
                  <span class="account-status" [class.enabled]="usuario.enabled">
                    {{ usuario.enabled ? 'Habilitado' : 'Inhabilitado' }}
                  </span>
                </td>
                <td data-label="Acciones">
                  <div class="action-buttons-group">
                    <button *ngIf="can('UPDATE')" class="state-button" [class.disable]="usuario.enabled" type="button" (click)="cambiarEstado(usuario)" [disabled]="isCurrentUser(usuario) || updatingIds.has(usuario.id)" [attr.title]="isCurrentUser(usuario) ? 'No puedes inhabilitar tu propia cuenta' : null">
                      {{ usuario.enabled ? 'Inhabilitar' : 'Habilitar' }}
                    </button>
                    <button *ngIf="can('RESET_PASSWORD')" class="reset-pass-btn" type="button" (click)="abrirResetPassword(usuario)">
                      🔑 Restablecer clave
                    </button>
                    <button *ngIf="can('DELETE')" class="delete-user-btn" type="button" (click)="eliminarUsuario(usuario)" [disabled]="isCurrentUser(usuario)">
                      🗑️ Eliminar
                    </button>
                  </div>
                  <small class="self-note" *ngIf="isCurrentUser(usuario)">Sesión actual</small>
                </td>
              </tr>
              <tr *ngIf="usuarios.length === 0">
                <td colspan="5" class="empty-state">No hay usuarios registrados.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .action-buttons-group { display: flex; gap: 0.4rem; align-items: center; flex-wrap: wrap; }
    .reset-pass-btn { background: #3b82f6; color: white; border: none; padding: 0.35rem 0.6rem; border-radius: 4px; font-size: 0.8rem; cursor: pointer; font-weight: 600; }
    .reset-pass-btn:hover { background: #2563eb; }
    .delete-user-btn { background: #b4233d; color: #fff; border: 0; padding: 0.35rem 0.6rem; border-radius: 4px; font-size: 0.8rem; font-weight: 600; cursor: pointer; }
    .delete-user-btn:hover { background: #94172e; }
    .role-select { padding: 0.35rem 0.5rem; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.85rem; background: #fff; }
    .role-tag { background: #e0e7ff; color: #3730a3; padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.78rem; font-weight: 600; display: inline-block; margin-right: 0.2rem; }
    .load-error { margin: 1rem; padding: 1rem; border-radius: 8px; color: #8b1e36; background: #fff0f3; }
    .load-error button { margin-left: 0.5rem; border: 0; border-radius: 6px; padding: 0.4rem 0.7rem; color: #fff; background: #8b1e36; cursor: pointer; }
    .d-block { display: block; }
    .text-muted { color: #64748b; font-size: 0.8rem; }
  `]
})
export class UsuariosComponent implements OnInit {
  usuarios: UsuarioAdmin[] = [];
  showForm = false;
  loading = true;
  saving = false;
  updatingIds = new Set<number>();
  rolesDisponibles: RolAdmin[] = [];
  selectedUserRoles: { [userId: number]: number } = {};
  selectedNewUserRoleId: number | null = null;
  nuevoUsuario: CrearUsuarioRequest = this.emptyUser();
  loadError = '';
  private readonly currentUsername = this.authService.getUser()?.username;

  constructor(
    private usuarioService: UsuarioAdminService,
    private rolService: RolAdminService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading = true;
    this.loadError = '';
    forkJoin({
      usuarios: this.usuarioService.obtenerTodos(),
      roles: this.rolService.listar()
    }).pipe(
      timeout(10000),
      finalize(() => this.loading = false)
    ).subscribe({
      next: ({ usuarios, roles }) => {
        this.usuarios = usuarios;
        this.rolesDisponibles = roles.filter(rol => rol.activo);
        
        // Mapear el rol seleccionado para cada usuario
        this.selectedUserRoles = {};
        this.usuarios.forEach(u => {
          this.selectedUserRoles[u.id] = u.roles && u.roles.length > 0 ? u.roles[0].id : (this.rolesDisponibles[0]?.id ?? 0);
        });

        // Configurar rol por defecto para nuevo usuario
        const defaultRole = this.rolesDisponibles.find(r => r.nombre === 'ROLE_USER') || this.rolesDisponibles[0];
        this.selectedNewUserRoleId = defaultRole ? defaultRole.id : null;
      },
      error: err => {
        this.loadError = err.name === 'TimeoutError'
          ? 'La solicitud tardó demasiado. Verifica que auth-service esté ejecutándose en el puerto 8081.'
          : err.error?.message || `No se pudieron cargar los usuarios (HTTP ${err.status || 'desconocido'}).`;
        this.showError('No se pudieron cargar los usuarios', err);
      }
    });
  }

  trackById(_: number, usuario: UsuarioAdmin): number {
    return usuario.id;
  }

  can(permission: string): boolean {
    return this.authService.hasPermission('USUARIOS', permission);
  }

  isCurrentUser(usuario: UsuarioAdmin): boolean {
    return usuario.username === this.currentUsername;
  }

  crearUsuario(): void {
    if (!this.selectedNewUserRoleId) {
      Swal.fire({ icon: 'warning', title: 'Rol requerido', text: 'Debes seleccionar un rol para el usuario' });
      return;
    }
    this.saving = true;
    this.nuevoUsuario.roleIds = [this.selectedNewUserRoleId];

    this.usuarioService.crear(this.nuevoUsuario).subscribe({
      next: () => {
        this.nuevoUsuario = this.emptyUser();
        this.showForm = false;
        this.saving = false;
        this.cargarDatos();
        Swal.fire({ icon: 'success', title: 'Usuario creado', timer: 1600, showConfirmButton: false });
      },
      error: err => {
        this.saving = false;
        this.showError('No se pudo crear el usuario', err);
      }
    });
  }

  cambiarRolUsuario(usuario: UsuarioAdmin, newRoleId: number): void {
    if (!newRoleId) return;
    const currentRoleId = usuario.roles && usuario.roles.length > 0 ? usuario.roles[0].id : null;
    if (newRoleId === currentRoleId) return;

    this.updatingIds.add(usuario.id);
    this.usuarioService.cambiarRoles(usuario.id, [newRoleId]).subscribe({
      next: actualizado => {
        usuario.roles = actualizado.roles;
        this.selectedUserRoles[usuario.id] = newRoleId;
        this.updatingIds.delete(usuario.id);
        Swal.fire({ icon: 'success', title: 'Rol actualizado', timer: 1300, showConfirmButton: false });
      },
      error: err => {
        // Revertir selección en caso de error
        if (currentRoleId) {
          this.selectedUserRoles[usuario.id] = currentRoleId;
        }
        this.updatingIds.delete(usuario.id);
        this.showError('No se pudo actualizar el rol', err);
      }
    });
  }

  cambiarEstado(usuario: UsuarioAdmin): void {
    if (this.isCurrentUser(usuario)) return;
    const enabled = !usuario.enabled;
    this.updatingIds.add(usuario.id);
    this.usuarioService.cambiarEstado(usuario.id, enabled).subscribe({
      next: () => {
        usuario.enabled = enabled;
        this.updatingIds.delete(usuario.id);
        Swal.fire({ icon: 'success', title: enabled ? 'Usuario habilitado' : 'Usuario inhabilitado', timer: 1300, showConfirmButton: false });
      },
      error: err => {
        this.updatingIds.delete(usuario.id);
        this.showError('No se pudo actualizar el estado', err);
      }
    });
  }

  abrirResetPassword(usuario: UsuarioAdmin): void {
    Swal.fire({
      title: 'Restablecer Contraseña',
      html: `
        <p style="font-size: 0.9rem; color: #475569;">Ingresa la nueva contraseña para <strong>${usuario.username}</strong>:</p>
        <input type="password" id="newPasswordInput" class="swal2-input" placeholder="Nueva Contraseña (mínimo 6 caracteres)">
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Guardar Contraseña',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#3b82f6',
      preConfirm: () => {
        const pass = (document.getElementById('newPasswordInput') as HTMLInputElement).value;
        if (!pass || pass.length < 6) {
          Swal.showValidationMessage('La contraseña debe contener al menos 6 caracteres');
          return false;
        }
        return pass;
      }
    }).then((res) => {
      if (res.isConfirmed && res.value) {
        this.usuarioService.resetPassword(usuario.id, res.value).subscribe({
          next: () => {
            Swal.fire({ icon: 'success', title: '¡Contraseña Restablecida!', text: `La clave de ${usuario.username} ha sido actualizada.`, timer: 2000, showConfirmButton: false });
          },
          error: (err) => this.showError('Error al restablecer contraseña', err)
        });
      }
    });
  }

  eliminarUsuario(usuario: UsuarioAdmin): void {
    if (this.isCurrentUser(usuario)) return;
    Swal.fire({
      icon: 'warning',
      title: '¿Eliminar usuario?',
      text: `¿Estás seguro de eliminar a "${usuario.username}"?`,
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#b4233d'
    }).then(result => {
      if (!result.isConfirmed) return;
      this.usuarioService.eliminar(usuario.id).subscribe({
        next: () => {
          this.usuarios = this.usuarios.filter(item => item.id !== usuario.id);
          delete this.selectedUserRoles[usuario.id];
          Swal.fire({ icon: 'success', title: 'Usuario eliminado', timer: 1300, showConfirmButton: false });
        },
        error: err => this.showError('No se pudo eliminar el usuario', err)
      });
    });
  }

  private emptyUser(): CrearUsuarioRequest {
    return { username: '', password: '', email: '', roleIds: [] };
  }

  private showError(title: string, err: any): void {
    const text = err.error?.message || err.error?.error || err.message || 'Inténtalo nuevamente.';
    Swal.fire({ icon: 'error', title, text, confirmButtonColor: '#b45f72' });
  }
}

