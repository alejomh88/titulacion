package ec.edu.utmachala.titulacion.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
@Table(schema = "exetasi", name = "\"estudiantesExamenComplexivoPP\"")
public class EstudianteExamenComplexivoPP {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "ESTUDIANTESEXAMENCOMPLEXIVOPP_ID_GENERATOR", sequenceName = "\"estudiantesExamenComplexivoPP_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ESTUDIANTESEXAMENCOMPLEXIVOPP_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	private Integer citas1;

	private Integer citas2;

	@Column
	private Boolean aprobado;

	@Column(nullable = false)
	private Boolean activo;

	@Column(name = "\"fechaSeleccion\"")
	private Timestamp fechaSeleccion;

	@Column(name = "\"archivoAdjunto\"", length = 100)
	private String archivoAdjunto;

	@Column(length = 500)
	private String archivo;

	@Column(name = "\"reporteUrkund\"", length = 500)
	private String reporteUrkund;

	@Column(name = "\"observacionesUrkund\"")
	private String observacionesUrkund;

	@Column(name = "\"observacionesCitas\"")
	private String observacionesCitas;

	@Column(name = "\"casoInvestigacion\"", nullable = false)
	private String casoInvestigacion;

	@Column(name = "\"tituloInvestigacion\"")
	private String tituloInvestigacion;

	@ManyToOne
	@JoinColumn(name = "estudiante")
	private Usuario estudiante;

	@ManyToOne
	@JoinColumn(name = "proceso")
	private Proceso proceso;

	@ManyToOne
	@JoinColumn(name = "carrera")
	private Carrera carrera;

	@ManyToOne
	@JoinColumn(name = "\"docenteAsignatura\"", nullable = false)
	private DocenteAsignatura docenteAsignatura;

	@Column(name = "\"fechaResolucion\"")
	private Date fechaResolucion;

	@Column(name = "\"fechaResolucionAptitudLegal\"")
	private Date fechaResolucionAptitudLegal;

	@Column(name = "\"fechaSustentacionOrdinaria\"")
	private Date fechaSustentacionOrdinaria;

	@Column(name = "\"calificacionEscritaOrdinaria\"", precision = 4, scale = 2)
	private BigDecimal calificacionEscritaOrdinaria;

	@Column(name = "\"calificacionOralOrdinaria\"", precision = 4, scale = 2)
	private BigDecimal calificacionOralOrdinaria;

	@Column(name = "\"fechaSustentacionGracia\"")
	private Date fechaSustentacionGracia;

	@Column(name = "\"calificacionEscritaGracia\"", precision = 4, scale = 2)
	private BigDecimal calificacionEscritaGracia;

	@Column(name = "\"calificacionOralGracia\"", precision = 4, scale = 2)
	private BigDecimal calificacionOralGracia;

	@ManyToOne
	@JoinColumn(name = "especialista1")
	private Usuario especialista1;

	@ManyToOne
	@JoinColumn(name = "especialista2")
	private Usuario especialista2;

	@ManyToOne
	@JoinColumn(name = "especialista3")
	private Usuario especialista3;

	@ManyToOne
	@JoinColumn(name = "\"especialistaSuplente1\"")
	private Usuario especialistaSuplente1;

	@ManyToOne
	@JoinColumn(name = "\"especialistaSuplente2\"")
	private Usuario especialistaSuplente2;

	@ManyToOne
	@JoinColumn(name = "\"especialistaSuplantadoE\"")
	private Usuario especialistaSuplantadoE;

	@ManyToOne
	@JoinColumn(name = "\"especialistaSuplantadoO\"")
	private Usuario especialistaSuplantadoO;

	@Column(name = "resolucion")
	private String resolucion;

	@Column(name = "\"resolucionAptitudLegal\"")
	private String resolucionAptitudLegal;

	@Column(name = "\"validarCalificacionOrdinaria\"")
	private Boolean validarCalificacionOrdinaria;

	@Column(name = "\"validarCalificacionGracia\"")
	private Boolean validarCalificacionGracia;

	@OneToMany(mappedBy = "temaPractico", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Calificacion> calificaciones;

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

	@Column(name = "\"activarSuplenteE\"")
	private Boolean activarSuplenteE;

	@Column(name = "\"activarSuplenteO\"")
	private Boolean activarSuplenteO;

	@Column(name = "\"numRegistro\"", length = 50)
	private String numRegistro;

	@Column(name = "\"numClasificacion\"", length = 30)
	private String numClasificacion;

	@Column(name = "\"direccionURL\"")
	private String direccionURL;

	@Column(name = "antiplagio", precision = 5, scale = 2)
	private BigDecimal antiplagio;

	@Column(name = "antiplagio2", precision = 5, scale = 2)
	private BigDecimal antiplagio2;

	@Column(name = "documentos")
	private Boolean documentos;

	@Column(name = "\"recepcionPPDE\"")
	private Boolean recepcionPPDE;

	@Column(name = "\"numeroActaGraduacion\"")
	private String numeroActaGraduacion;

	@Column(name = "\"fechaActaGraduacion\"")
	private Date fechaActaGraduacion;

	@Column(precision = 4, scale = 2)
	private BigDecimal ee1;

	@Column(precision = 4, scale = 2)
	private BigDecimal ee2;

	@Column(precision = 4, scale = 2)
	private BigDecimal ee3;

	@Column(precision = 4, scale = 2)
	private BigDecimal es1;

	@Column(precision = 4, scale = 2)
	private BigDecimal ooe1;

	@Column(precision = 4, scale = 2)
	private BigDecimal ooe2;

	@Column(precision = 4, scale = 2)
	private BigDecimal ooe3;

	@Column(precision = 4, scale = 2)
	private BigDecimal oos1;

	@Column(precision = 4, scale = 2)
	private BigDecimal oge1;

	@Column(precision = 4, scale = 2)
	private BigDecimal oge2;

	@Column(precision = 4, scale = 2)
	private BigDecimal oge3;

	@Column(precision = 4, scale = 2)
	private BigDecimal ogs1;

	@Column(name = "\"notaTeorica\"", precision = 4, scale = 2)
	private BigDecimal notaTeorica;

	@Column(name = "\"validarArchivo\"")
	private Boolean validarArchivo;

	@Column(name = "\"urlBiblioteca\"")
	private String urlBiblioteca;

	@ManyToOne
	@JoinColumn(name = "tutor")
	private Usuario tutor;

	@OneToMany(mappedBy = "estudianteExamenComplexivoPP", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TutoriaEC> tutorias;

	@Column(name = "\"actualizarDatos\"")
	private Boolean actualizarDatos;

	@Column(name = "\"abstract1\"")
	private String abstract1;

	@Column
	private Boolean problema;

	@Column(name = "\"observacionProblema\"")
	private String observacionProblema;

	@Column(name = "\"lugarSustentacionOrdinaria\"")
	private String lugarSustentacionOrdinaria;

	@Column(name = "\"lugarSustentacionGracia\"")
	private String lugarSustentacionGracia;

	@Column(name = "\"validadoBiblioteca\"")
	private Boolean validadoBiblioteca;

	@OneToMany(mappedBy = "estudianteExamenComplexivoPP")
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