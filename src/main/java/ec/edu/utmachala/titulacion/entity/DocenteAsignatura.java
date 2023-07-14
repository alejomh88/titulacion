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
@Table(schema = "exetasi", name = "\"docentesAsignaturas\"")
public class DocenteAsignatura {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "DOCENTESASIGNATURAS_ID_GENERATOR", sequenceName = "\"docentesAsignaturas_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCENTESASIGNATURAS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "docente", nullable = false)
	private Usuario docente;

	@ManyToOne
	@JoinColumn(name = "asignatura", nullable = false)
	private Asignatura asignatura;

	@ManyToOne
	@JoinColumn(name = "\"periodoReactivo\"")
	private PeriodoReactivo periodoReactivo;

	@Column(nullable = false)
	private Boolean activo;

	@OneToMany(mappedBy = "docenteAsignatura", cascade = CascadeType.ALL)
	private List<EstudianteExamenComplexivoPP> temasPracticos;

}