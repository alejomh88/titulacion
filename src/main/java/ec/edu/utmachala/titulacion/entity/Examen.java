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
@Table(schema = "exetasi", name = "examenes")
public class Examen {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "EXAMENES_ID_GENERATOR", sequenceName = "EXAMENES_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EXAMENES_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "\"estudianteExamenComplexivoPT\"", nullable = false)
	private EstudianteExamenComplexivoPT estudianteExamenComplexivoPT;

	@ManyToOne
	@JoinColumn(name = "grupo", nullable = false)
	private Grupo grupo;

	@ManyToOne
	@JoinColumn(name = "\"tipoExamen\"", nullable = false)
	private TipoExamen tipoExamen;

	@OneToMany(mappedBy = "examen", cascade = CascadeType.ALL)
	private List<PreguntaExamen> preguntasExamenes;

}