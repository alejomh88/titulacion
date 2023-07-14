package ec.edu.utmachala.titulacion.entity;

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
@Table(schema = "exetasi", name = "carreras")
public class Carrera {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "CARRERAS_ID_GENERATOR", sequenceName = "CARRERAS_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CARRERAS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false, length = 200)
	private String nombre;

	@Column(name = "\"nomenclaturaTituloFemenino\"", length = 300)
	private String nomenclaturaTituloFemenino;

	@Column(name = "\"nomenclaturaTituloMasculino\"", length = 300)
	private String nomenclaturaTituloMasculino;

	@Column(name = "\"tipoTitulo\"", length = 300)
	private String tipoTitulo;

	@Column(name = "\"detalleTitulo\"", length = 300)
	private String detalleTitulo;

	@ManyToOne
	@JoinColumn(name = "\"unidadAcademica\"", nullable = false)
	private UnidadAcademica unidadAcademica;

	@OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL)
	private List<CarreraMallaProceso> carreraMallaProceso;

	@OneToMany(mappedBy = "carrera")
	private List<PermisoCarrera> permisosCarreras;

	@OneToMany(mappedBy = "carrera")
	private List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulaciones;

	@OneToMany(mappedBy = "carrera")
	private List<LineaInvestigacionCarrera> lineasInvestigacionesCarreras;

	@OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPT> estudiantesExamenComplexivoPT;

	@OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPP;

	@OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL)
	private List<FechaProceso> fechaProceso;

	@OneToMany(mappedBy = "carrera")
	private List<Certificado> certificados;

}