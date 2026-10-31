import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';
import { CatalogoSeguridad, RolAdmin } from '../../core/models/usuario-admin.model';
import { AuthService } from '../../core/services/auth.service';
import { GuardarRolRequest, RolAdminService } from '../../core/services/rol-admin.service';

@Component({
  selector: 'app-roles-permisos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="admin-page">
      <div class="admin-heading">
        <div><p class="section-kicker">Control de acceso</p><h2>Roles y permisos</h2><p>Configura los módulos y acciones permitidas para cada rol.</p></div>
        <button *ngIf="canCreate()" class="old-rose-button" type="button" (click)="nuevoRol()">Nuevo rol</button>
      </div>
      <div class="roles-layout" *ngIf="!loading; else cargando">
        <aside class="roles-list">
          <button *ngFor="let rol of roles" type="button" [class.active]="draft?.id === rol.id" (click)="seleccionar(rol)">
            <strong>{{ rol.nombre }}</strong><small>{{ rol.activo ? 'Activo' : 'Inactivo' }}</small>
          </button>
        </aside>
        <form class="role-editor" *ngIf="draft" (ngSubmit)="guardar()">
          <div class="admin-form-grid">
            <label>Nombre<input name="nombre" [(ngModel)]="draft.nombre" required [disabled]="isSuperRole()"></label>
            <label>Descripción<input name="descripcion" [(ngModel)]="draft.descripcion"></label>
            <label>Sistema<select name="sistema" [(ngModel)]="draft.sistemaId" required><option *ngFor="let sistema of catalogo.sistemas" [ngValue]="sistema.id">{{ sistema.nombre }}</option></select></label>
            <label class="check-label"><input type="checkbox" name="activo" [(ngModel)]="draft.activo"> Rol activo</label>
          </div>
          <div class="permission-matrix">
            <div class="permission-row" *ngFor="let modulo of modulosDelSistema()">
              <label class="module-check"><input type="checkbox" [checked]="hasModule(modulo.id)" (change)="toggleModule(modulo.id, $any($event.target).checked)"><strong>{{ modulo.nombre }}</strong><small>{{ modulo.codigo }}</small></label>
              <label *ngFor="let permiso of catalogo.permisos"><input type="checkbox" [disabled]="!hasModule(modulo.id)" [checked]="hasPermission(modulo.id, permiso.id)" (change)="togglePermission(modulo.id, permiso.id, $any($event.target).checked)">{{ permiso.nombre }}</label>
            </div>
          </div>
          <div class="admin-form-actions">
            <button *ngIf="canEdit()" class="save-user-button" type="submit" [disabled]="saving">{{ saving ? 'Guardando...' : 'Guardar rol' }}</button>
            <button *ngIf="draft.id && canDelete() && !isInitialRole()" class="delete-role" type="button" (click)="eliminar()">Eliminar</button>
          </div>
        </form>
        <div class="empty-state" *ngIf="!draft">Selecciona un rol para editarlo.</div>
      </div>
      <ng-template #cargando><div class="admin-loading">Cargando configuración...</div></ng-template>
    </section>
  `,
  styles: [`
    .roles-layout{display:grid;grid-template-columns:240px 1fr;gap:1rem}.roles-list,.role-editor{background:#fff;border-radius:14px;padding:1rem;box-shadow:0 8px 28px #29344912}.roles-list{display:flex;flex-direction:column;gap:.45rem;align-self:start}.roles-list button{border:1px solid #eadde1;background:#fff;border-radius:9px;padding:.75rem;text-align:left;display:flex;flex-direction:column;cursor:pointer}.roles-list button.active{border-color:#a64d68;background:#fff5f7}.roles-list small,.module-check small{color:#77808e}.permission-matrix{margin-top:1rem;border:1px solid #eadde1;border-radius:10px;overflow:auto}.permission-row{display:grid;grid-template-columns:minmax(190px,1.4fr) repeat(5,minmax(105px,1fr));gap:.5rem;align-items:center;padding:.75rem;border-bottom:1px solid #f0e7ea}.permission-row:last-child{border-bottom:0}.permission-row label{display:flex;gap:.35rem;align-items:center;font-size:.82rem}.module-check{flex-direction:column!important;align-items:flex-start!important}.check-label{display:flex;align-items:center;gap:.5rem}.delete-role{border:0;border-radius:8px;background:#b4233d;color:#fff;padding:.7rem 1rem}@media(max-width:900px){.roles-layout{grid-template-columns:1fr}.permission-row{min-width:780px}}
  `]
})
export class RolesPermisosComponent implements OnInit {
  roles: RolAdmin[] = [];
  catalogo: CatalogoSeguridad = { sistemas: [], modulos: [], permisos: [] };
  draft: RolAdmin | null = null;
  loading = true;
  saving = false;

  constructor(private service: RolAdminService, private auth: AuthService) {}

  ngOnInit(): void {
    forkJoin({ roles: this.service.listar(), catalogo: this.service.catalogo() }).subscribe({
      next: result => { this.roles = result.roles; this.catalogo = result.catalogo; this.loading = false; if (this.roles.length) this.seleccionar(this.roles[0]); },
      error: err => { this.loading = false; this.error(err); }
    });
  }

  seleccionar(rol: RolAdmin): void { this.draft = structuredClone(rol); }
  nuevoRol(): void {
    this.draft = { id: 0, nombre: 'ROLE_', descripcion: '', activo: true, sistemaId: this.catalogo.sistemas[0]?.id, sistemaNombre: '', modulos: [] };
  }
  modulosDelSistema() { return this.catalogo.modulos.filter(m => m.sistemaId === this.draft?.sistemaId); }
  hasModule(id: number): boolean { return !!this.draft?.modulos.some(m => m.moduloId === id); }
  hasPermission(moduloId: number, permisoId: number): boolean { return !!this.draft?.modulos.find(m => m.moduloId === moduloId)?.permisoIds.includes(permisoId); }
  toggleModule(id: number, enabled: boolean): void {
    if (!this.draft) return;
    if (enabled) this.draft.modulos.push({ moduloId: id, permisoIds: [] });
    else this.draft.modulos = this.draft.modulos.filter(m => m.moduloId !== id);
  }
  togglePermission(moduloId: number, permisoId: number, enabled: boolean): void {
    const item = this.draft?.modulos.find(m => m.moduloId === moduloId); if (!item) return;
    item.permisoIds = enabled ? [...new Set([...item.permisoIds, permisoId])] : item.permisoIds.filter(id => id !== permisoId);
  }
  guardar(): void {
    if (!this.draft) return;
    const request: GuardarRolRequest = { nombre: this.draft.nombre, descripcion: this.draft.descripcion, activo: this.draft.activo, sistemaId: this.draft.sistemaId, modulos: this.draft.modulos };
    this.saving = true;
    const action = this.draft.id ? this.service.actualizar(this.draft.id, request) : this.service.crear(request);
    action.subscribe({ next: rol => { const index = this.roles.findIndex(r => r.id === rol.id); if (index >= 0) this.roles[index] = rol; else this.roles.push(rol); this.seleccionar(rol); this.saving = false; Swal.fire({ icon: 'success', title: 'Rol guardado', timer: 1400, showConfirmButton: false }); }, error: err => { this.saving = false; this.error(err); } });
  }
  eliminar(): void {
    if (!this.draft?.id) return;
    Swal.fire({ icon: 'warning', title: '¿Eliminar rol?', showCancelButton: true, confirmButtonText: 'Eliminar', cancelButtonText: 'Cancelar' }).then(result => {
      if (!result.isConfirmed || !this.draft) return;
      this.service.eliminar(this.draft.id).subscribe({ next: () => { this.roles = this.roles.filter(r => r.id !== this.draft?.id); this.draft = this.roles[0] ? structuredClone(this.roles[0]) : null; }, error: err => this.error(err) });
    });
  }
  canCreate(): boolean { return this.auth.hasPermission('ROLES', 'CREATE'); }
  canEdit(): boolean { return this.auth.hasPermission('ROLES', 'UPDATE'); }
  canDelete(): boolean { return this.auth.hasPermission('ROLES', 'DELETE'); }
  isSuperRole(): boolean { return this.draft?.nombre === 'ROLE_SUPER_ADMIN'; }
  isInitialRole(): boolean { return ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_USER'].includes(this.draft?.nombre ?? ''); }
  private error(err: any): void { Swal.fire({ icon: 'error', title: 'No se pudo completar la operación', text: err.error?.message || err.message || 'Inténtalo nuevamente.' }); }
}
