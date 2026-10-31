export interface RolResumen {
  id: number;
  nombre: string;
  descripcion: string;
  activo: boolean;
}

export interface UsuarioAdmin {
  id: number;
  username: string;
  email: string;
  roles: RolResumen[];
  enabled: boolean;
}

export interface CrearUsuarioRequest {
  username: string;
  password: string;
  email: string;
  roleIds: number[];
}

export interface AsignacionModulo {
  moduloId: number;
  permisoIds: number[];
}

export interface RolAdmin extends RolResumen {
  sistemaId: number;
  sistemaNombre: string;
  modulos: AsignacionModulo[];
}

export interface CatalogoSeguridad {
  sistemas: { id: number; codigo: string; nombre: string; activo: boolean }[];
  modulos: { id: number; codigo: string; nombre: string; sistemaId: number; moduloPadreId: number | null; activo: boolean }[];
  permisos: { id: number; nombre: string; descripcion: string }[];
}
