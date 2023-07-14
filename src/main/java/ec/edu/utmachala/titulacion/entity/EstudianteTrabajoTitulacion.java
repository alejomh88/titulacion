package ec.edu.utmachala.titulacion.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "\"estudiantesTrabajosTitulacion\"")
public class EstudianteTrabajoTitulacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "ESTUDIANTESTRABAJOSTITULACION_ID_GENERATOR", sequenceName = "\"estudiantesTrabajosTitulacion_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ESTUDIANTESTRABAJOSTITULACION_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "carrera", nullable = false)
	private Carrera carrera;

	@ManyToOne
	@JoinColumn(name = "estudiante", nullable = false)
	private Usuario estudiante;

	@ManyToOne
	@JoinColumn(name = "cotutor")
	private Usuario cotutor;

	@Column(name = "antiplagio2", precision = 5, scale = 2)
	private BigDecimal antiplagio2;

	private Integer citas1;

	private Integer citas2;

	@Column(name = "\"recepcionDE\"")
	private Boolean recepcionDE;

	@Column(name = "\"resolucionAptitudLegal\"")
	private String resolucionAptitudLegal;

	@Column(name = "\"fechaResolucionAptitudLegal\"")
	private Date fechaResolucionAptitudLegal;

	@Column(name = "\"fechaResolucion\"")
	private Date fechaResolucion;

	@Column(name = "\"fechaSustentacion\"")
	private Date fechaSustentacion;

	@Column(name = "\"reporteUrkund\"", length = 500)
	private String reporteUrkund;

	@Column(name = "\"observacionesUrkund\"")
	private String observacionesUrkund;

	@Column(name = "\"observacionesCitas\"")
	private String observacionesCitas;

	@Column
	private Boolean aprobado;

	@Column(length = 500)
	private String archivo;

	@ManyToOne
	@JoinColumn(name = "especialista1")
	private Usuario especialista1;

	@ManyToOne
	@JoinColumn(name = "especialista2")
	private Usuario especialista2;

	@ManyToOne
	@JoinColumn(name = "proceso")
	private Proceso proceso;

	@ManyToOne
	@JoinColumn(name = "especialista3")
	private Usuario especialista3;

	@ManyToOne
	@JoinColumn(name = "\"especialistaSuplente1\"")
	private Usuario especialistaSuplente1;

	@ManyToOne
	@JoinColumn(name = "\"seminarioTrabajoTitulacion\"")
	private SeminarioTrabajoTitulacion seminarioTrabajoTitulacion;

	@ManyToOne
	@JoinColumn(name = "\"especialistaSuplente2\"")
	private Usuario especialistaSuplente2;

	@Column(name = "resolucion")
	private String resolucion;

	@OneToMany(mappedBy = "estudianteTrabajoTitulacion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Tutoria> tutorias;

	@ManyToOne
	@JoinColumn(name = "\"opcionTitulacion\"")
	private OpcionTitulacion opcionTitulacion;

	@OneToMany(mappedBy = "estudianteTrabajoTitulacion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CalificacionTrabajoTitulacion> calificacionesTrabajoTitulaciones;

	@Column(name = "\"calificacionEscrita\"", precision = 4, scale = 2)
	private BigDecimal calificacionEscrita;

	@Column(name = "\"calificacionOral\"", precision = 4, scale = 2)
	private BigDecimal calificacionOral;

	@Column(name = "\"validarCalificacion\"")
	private Boolean validarCalificacion;

	@Column(name = "\"fechaInicioClase\"")
	private Date fechaInicioClase;

	@Column(name = "\"fechaFinClase\"")
	private Date fechaFinClase;

	@Column(name = "\"fechaActaIncorporacion\"")
	private Date fechaActaIncorporacion;

	@Column(name = "\"numeroActaIncorporacion\"")
	private String numeroActaIncorporacion;

	@Column(name = "\"numeroActaCalificacion\"")
	private Integer numeroActaCalificacion;

	@Column(name = "\"tituloInvestigacion\"")
	private String tituloInvestigacion;

	@Column(name = "\"numPaginas\"")
	private Integer numPaginas;

	@Column(name = "\"areasTematicas\"")
	private String areasTematicas;

	@Column(name = "\"palabrasClaves\"")
	private String palabrasClaves;

	@Column(name = "\"resumen\"")
	private String resumen;

	@Column(name = "\"adjuntoPDF\"")
	private Boolean adjuntoPDF;

	@Column(name = "\"numRegistro\"", length = 50)
	private String numRegistro;

	@Column(name = "\"numClasificacion\"", length = 30)
	private String numClasificacion;

	@Column(name = "\"direccionURL\"")
	private String direccionURL;

	@Column(name = "antiplagio", precision = 5, scale = 2)
	private BigDecimal antiplagio;

	@Column(name = "documentos")
	private Boolean documentos;

	@Column(name = "\"numeroActaGraduacion\"")
	private String numeroActaGraduacion;

	@Column(name = "\"fechaActaGraduacion\"")
	private Date fechaActaGraduacion;

	@Column(name = "ee1", precision = 4, scale = 2)
	private BigDecimal ee1;

	@Column(name = "ee2", precision = 4, scale = 2)
	private BigDecimal ee2;

	@Column(name = "ee3", precision = 4, scale = 2)
	private BigDecimal ee3;

	@Column(name = "es1", precision = 4, scale = 2)
	private BigDecimal es1;

	@Column(name = "oe1", precision = 4, scale = 2)
	private BigDecimal oe1;

	@Column(name = "oe2", precision = 4, scale = 2)
	private BigDecimal oe2;

	@Column(name = "oe3", precision = 4, scale = 2)
	private BigDecimal oe3;

	@Column(name = "os1", precision = 4, scale = 2)
	private BigDecimal os1;

	@Column(name = "\"activarSuplenteE\"")
	private Boolean activarSuplenteE;

	@ManyToOne
	@JoinColumn(name = "\"especialistaSuplantadoE\"")
	private Usuario especialistaSuplantadoE;

	@Column(name = "\"activarSuplenteO\"")
	private Boolean activarSuplenteO;

	@ManyToOne
	@JoinColumn(name = "\"especialistaSuplantadoO\"")
	private Usuario especialistaSuplantadoO;

	@Column
	private Boolean prorroga;

	@Column(name = "\"validarArchivo\"")
	private Boolean validarArchivo;

	@Column(name = "\"urlBiblioteca\"")
	private String urlBiblioteca;

	@Column(name = "\"actualizarDatos\"")
	private Boolean actualizarDatos;

	@Column(name = "\"abstract1\"")
	private String abstract1;

	@Column
	private Boolean problema;

	@Column(name = "\"observacionProblema\"")
	private String observacionProblema;

	@Column(name = "\"lugarSustentacion\"")
	private String lugarSustentacion;

	@Column(name = "\"validadoBiblioteca\"")
	private Boolean validadoBiblioteca;

	@OneToMany(mappedBy = "estudianteTrabajoTitulacion")
	private List<CertificadoEstudiante> certificadosEstudiantes;

	@Column(name = "\"urlDrive\"", length = 100)
	private String urlDrive;

	@Column(name = "\"validarDocumentoE3\"")
	private Boolean validarDocumentoE3;

	@Column(name = "\"esProfesional\"")
	private Boolean esProfesional;

	@Column(name = "\"creditosTotalesReprobados\"")
	private Boolean creditosTotalesReprobados;

	@Column(name = "\"nroOficioTesoreria\"", length = 100)
	private String nroOficioTesoreria;

	@Column(name = "\"nroOficioSecretaria\"", length = 100)
	private String nroOficioSecretaria;

}