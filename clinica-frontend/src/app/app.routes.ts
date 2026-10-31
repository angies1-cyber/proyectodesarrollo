import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { MainLayoutComponent } from './layout/main-layout.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { PacientesComponent } from './pages/pacientes/pacientes.component';
import { MedicosComponent } from './pages/medicos/medicos.component';
import { EspecialidadesComponent } from './pages/especialidades/especialidades.component';
import { CitasComponent } from './pages/citas/citas.component';
import { UsuariosComponent } from './pages/administracion/usuarios.component';
import { RolesPermisosComponent } from './pages/administracion/roles-permisos.component';
import { ForbiddenComponent } from './pages/forbidden/forbidden.component';
import { authGuard, permissionGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'forbidden', component: ForbiddenComponent },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent, canActivate: [permissionGuard('DASHBOARD')] },
      { path: 'pacientes', component: PacientesComponent, canActivate: [permissionGuard('PACIENTES')] },
      { path: 'medicos', component: MedicosComponent, canActivate: [permissionGuard('MEDICOS')] },
      {
        path: 'especialidades',
        component: EspecialidadesComponent,
        canActivate: [permissionGuard('ESPECIALIDADES')]
      },
      { path: 'citas', component: CitasComponent, canActivate: [permissionGuard('CITAS')] },
      {
        path: 'administracion/usuarios',
        component: UsuariosComponent,
        canActivate: [permissionGuard('USUARIOS')]
      },
      { path: 'administracion/roles', component: RolesPermisosComponent, canActivate: [permissionGuard('ROLES')] }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
