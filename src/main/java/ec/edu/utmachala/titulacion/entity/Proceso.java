package ec.edu.utmachala.titulacion.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "procesos")
public class Proceso {

	@Id
	@Column(unique = true, nullable = false, length = 10)
	private String id;

	@Column(name = "\"fechaCierre\"", nullable = false)
	private Timestamp fechaCierre;

	@Column(name = "\"fechaInicio\"", nullable = false)
	private Timestamp fechaInicio;

	@Column(unique = true, nullable = false, length = 500)
	private String observacion;

	@Column(name = "\"numeroTutoria\"", nullable = false)
	private Integer numeroTutoria;

	@Column(name = "\"preguntaHumano\"", nullable = false)
	private Integer preguntaHumano;

	@Column(name = "\"preguntaBasico\"", nullable = false)
	private Integer preguntaBasico;

	@Column(name = "\"preguntaProfesional\"", nullable = false)
	private Integer preguntaProfesional;

	@Column(name = "\"tiempoExamen\"", nullable = false)
	private Integer tiempoExamen;

	@Column(name = "\"enlaceCronograma\"", length = 200)
	private String enlaceCronograma;

	@OneToMany(mappedBy = "proceso")
	private List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacion;

	@OneToMany(mappedBy = "proceso")
	private List<Seminario> seminarios;

	@OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPT> estudiantesExamenComplexivoPT;

	@OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPP;

	@OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL)
	private List<CarreraMallaProceso> carreraMallaProceso;

	@OneToMany(mappedBy = "proceso", cascade = CascadeType.ALL)
	private List<FechaProceso> fechaProceso;

}