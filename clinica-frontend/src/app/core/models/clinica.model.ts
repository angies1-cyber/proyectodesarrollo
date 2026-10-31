export interface PageMetadata {
  totalRecords: number;
  page: number;
  pageSize: number;
  totalPages: number;
  hasPreviousPage: boolean;
  hasNextPage: boolean;
}

export interface PageResponse<T> {
  data: T[];
  metadata: PageMetadata;
}

export interface Paciente {
  id?: number;
  nombre: string;
  apellido: string;
  dpi: string;
  telefono: string;
  correo: string;
  fechaNacimiento: string;
  edad?: number;
  activo?: boolean;
  direccion?: string;
  fechaCreacion?: string;
  fechaInhabilitacion?: string;
  telefonoAlterno?: string;
}

export interface Especialidad {
  id?: number;
  nombre: string;
  descripcion?: string;
  activo?: boolean;
}

export interface Medico {
  id?: number;
  nombre: string;
  apellido: string;
  dpi: string;
  fechaNacimiento: string;
  edad?: number;
  telefono?: string;
  correo?: string;
  direccion?: string;
  activo?: boolean;
  fechaCreacion?: string;
  especialidadId: number;
  especialidadNombre?: string;
}

export interface Cita {
  id?: number;
  pacienteId: number;
  pacienteNombre?: string;
  pacienteApellido?: string;
  pacienteDpi?: string;
  pacienteTelefono?: string;
  medicoId: number;
  medicoNombre?: string;
  medicoApellido?: string;
  especialidadNombre?: string;
  fecha: string;
  hora: string;
  motivo: string;
  estado?: 'PROGRAMADA' | 'CANCELADA' | 'REPROGRAMADA' | 'COMPLETADA' | string;
  fechaCreacion?: string;
  fechaModificacion?: string;
}

export interface DashboardMetricas {
  totalPacientes: number;
  pacientesActivos: number;
  pacientesInactivos: number;
  rangosEdad: { [key: string]: number };
  totalMedicos: number;
  totalCitas: number;
}
