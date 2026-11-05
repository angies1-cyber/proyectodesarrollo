export interface Usuario {
  id?: number;
  username: string;
  email: string;
  roles: string[];
}

export interface ModuloAcceso {
  id: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  ruta: string | null;
  icono: string;
  moduloPadreId: number | null;
  orden: number;
  permisos: string[];
  hijos?: ModuloAcceso[];
}

export interface AuthRequest {
  username: string;
  password: string;
  turnstileToken: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  username: string;
  email: string;
  roles: string[];
  modulos: ModuloAcceso[];
}
