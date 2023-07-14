package ec.edu.utmachala.titulacion.entity;

import java.util.List;

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
@Table(schema = "exetasi", name = "\"preguntasExamenes\"")
public class PreguntaExamen {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "PREGUNTASEXAMENES_ID_GENERATOR", sequenceName = "\"preguntasExamenes_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PREGUNTASEXAMENES_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(length = 64)
	private String respuesta;

	@ManyToOne
	@JoinColumn(name = "examen", nullable = false)
	private Examen examen;

	@ManyToOne
	@JoinColumn(name = "pregunta", nullable = false)
	private Pregunta pregunta;

	@OneToMany(mappedBy = "pregunta")
	private List<PreguntaExamen> preguntasExamenes;

}