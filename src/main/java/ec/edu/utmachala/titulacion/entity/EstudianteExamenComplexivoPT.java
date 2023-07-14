package ec.edu.utmachala.titulacion.entity;

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
@Table(schema = "exetasi", name = "\"estudiantesExamenComplexivoPT\"")
public class EstudianteExamenComplexivoPT {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "ESTUDIANTESEXAMENCOMPLEXIVOPT_ID_GENERATOR", sequenceName = "\"estudiantesExamenComplexivoPT_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ESTUDIANTESEXAMENCOMPLEXIVOPT_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(name = "\"numeroActaGraduacion\"", length = 7)
	private String numeroActaGraduacion;

	@Column(name = "\"fechaActaGraduacion\"")
	private Date fechaActaGraduacion;

	@Column(name = "\"numeroActaCalificacion\"", length = 4)
	private String numeroActaCalificacion;

	@Column(name = "\"fechaActaCalificacion\"")
	private Date fechaActaCalificacion;

	@ManyToOne
	@JoinColumn(name = "estudiante", nullable = false)
	private Usuario estudiante;

	@ManyToOne
	@JoinColumn(name = "proceso", nullable = false)
	private Proceso proceso;

	@ManyToOne
	@JoinColumn(name = "carrera", nullable = false)
	private Carrera carrera;

	@OneToMany(mappedBy = "estudianteExamenComplexivoPT", cascade = CascadeType.ALL)
	private List<Examen> examenes;

	@Column(name = "\"esProfesional\"")
	private Boolean esProfesional;

	@Column(name = "\"creditosTotalesReprobados\"")
	private Boolean creditosTotalesReprobados;

}