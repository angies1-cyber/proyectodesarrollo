package gt.edu.umg.core.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pacientes")
public class Paciente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String nombre;

	@Column(nullable = false, length = 50)
	private String apellido;

	@Column(nullable = false, unique = true, length = 13)
	private String dpi;

	@Column(nullable = false, length = 8)
	private String telefono;

	@Column(nullable = false)
	private String correo;

	@Column(nullable = false)
	private LocalDate fechaNacimiento;

	@Column(nullable = false)
	private Boolean activo = true;

	@Column(length = 150)
	private String direccion;

	@Column
	private LocalDate fechaCreacion = LocalDate.now();

	@Column
	private LocalDate fechaInhabilitacion;

	@Column(length = 8)
	private String telefonoAlterno;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getDpi() {
		return dpi;
	}

	public void setDpi(String dpi) {
		this.dpi = dpi;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public LocalDate getFechaInhabilitacion() {
		return fechaInhabilitacion;
	}

	public void setFechaInhabilitacion(LocalDate fechaInhabilitacion) {
		this.fechaInhabilitacion = fechaInhabilitacion;
	}

	public String getTelefonoAlterno() {
		return telefonoAlterno;
	}

	public void setTelefonoAlterno(String telefonoAlterno) {
		this.telefonoAlterno = telefonoAlterno;
	}
}
