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
@Table(schema = "exetasi", name = "\"preguntasHistorico\"")
public class PreguntaHistorico {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "PREGUNTASHISTORICO_ID_GENERATOR", sequenceName = "\"preguntasHistorico_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PREGUNTASHISTORICO_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(length = 2147483647)
	private String bibliografia;

	@Column(name = "\"ejeTematico\"", length = 2147483647)
	private String ejeTematico;

	@Column(nullable = false, length = 2147483647)
	private String enunciado;

	@Column(name = "\"imagenEnunciado\"", length = 2147483647)
	private String imagenEnunciado;

	@Column(name = "\"imagenJustificacion\"", length = 2147483647)
	private String imagenJustificacion;

	@Column(length = 2147483647)
	private String justificacion;

	@Column(name = "\"sostiApantisi\"", nullable = false, length = 64)
	private String sostiApantisi;

	private Integer tiempo;

	private Boolean activo;

	@Column(name = "\"unidadDidactica\"", nullable = false)
	private int unidadDidactica;

	@Column(name = "\"unidadesDidacticas\"", nullable = false)
	private String unidadesDidacticas;

	@Column(name = "asignatura", nullable = false)
	private String asignatura;

	@Column(name = "\"unidadesCurriculares\"", nullable = false)
	private String unidadesCurriculares;

	@Column(name = "\"nivelDificultad\"", nullable = false)
	private String nivelDificultad;

	@ManyToOne
	@JoinColumn(name = "docente", nullable = false)
	private Usuario docente;

	@OneToMany(orphanRemoval = true, mappedBy = "pregunta", cascade = CascadeType.ALL)
	private List<PosibleRespuestaHistorico> posiblesRespuestas;

	@OneToMany(mappedBy = "pregunta")
	private List<PreguntaExamen> preguntasExamenes;

}