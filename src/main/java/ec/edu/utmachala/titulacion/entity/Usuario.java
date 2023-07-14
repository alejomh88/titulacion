package ec.edu.utmachala.titulacion.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ec.edu.utmachala.titulacion.seguridad.Log;
import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "usuarios")
public class Usuario {

	@Id
	@Column(unique = true, nullable = false, length = 15)
	private String id;

	@Column(nullable = false)
	private Boolean activo;

	@Column(name = "\"apellidoNombre\"", nullable = false, length = 150)
	private String apellidoNombre;

	@Column(nullable = false, length = 1)
	private String genero;

	@Column(unique = true, nullable = false, length = 50)
	private String email;

	@Column(name = "\"emailPersonal\"", length = 50)
	private String emailPersonal;

	@Column(name = "\"usuarioUrkund\"", unique = true, length = 100)
	private String usuarioUrkund;

	@Column(nullable = false, length = 64)
	private String password;

	@Column(length = 10)
	private String telefono;

	@Column(length = 100)
	private String nacionalidad;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "docente")
	private List<DocenteAsignatura> docentesAsignaturas;

	@OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPT> estudiantesExamenComplexivoPT;

	@OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPP;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private List<Permiso> permisos;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private List<PermisoCarrera> permisosCarreras;

	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
	private List<Log> logs;

	@OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
	private List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulaciones;

	@OneToMany(mappedBy = "docente", cascade = CascadeType.ALL)
	private List<DocenteSeminario> docentesSeminarios;

	@OneToMany(mappedBy = "especialista1", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPP> especialistas1;

	@OneToMany(mappedBy = "especialista2", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPP> especialistas2;

	@OneToMany(mappedBy = "especialista3", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPP> especialistas3;

	@OneToMany(mappedBy = "especialistaSuplantadoE", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPP> especialistaSuplantadoE;

	@OneToMany(mappedBy = "especialistaSuplantadoO", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPP> especialistaSuplantadoO;

	@OneToMany(mappedBy = "usuario")
	private List<PermisoCertificado> permisosCertificados;

	@OneToMany(mappedBy = "docente")
	private List<Tutoria> tutorias;

}