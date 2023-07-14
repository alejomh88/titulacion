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
@Table(schema = "exetasi", name = "preguntas")
public class Pregunta {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "PREGUNTAS_ID_GENERATOR", sequenceName = "preguntas_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PREGUNTAS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(length = 2147483647)
	private String bibliografia;

	@Column(name = "\"ejeTematico\"", length = 2147483647)
	private String ejeTematico;

	@Column(nullable = false, length = 2147483647)
	private String enunciado;

	@Column(name = "\"imagenEnunciado\"")
	private String imagenEnunciado;

	@Column(name = "\"imagenJustificacion\"", length = 2147483647)
	private String imagenJustificacion;

	@Column(length = 2147483647)
	private String justificacion;

	@Column(name = "\"sostiApantisi\"", nullable = false, length = 64)
	private String sostiApantisi;

	private Integer tiempo;

	private Boolean activo;

	private Boolean seleccion;

	private Boolean revisado;

	@ManyToOne
	@JoinColumn(name = "\"nivelDificultad\"", nullable = false)
	private NivelDificultad nivelDificultad;

	@ManyToOne
	@JoinColumn(name = "\"unidadDidactica\"", nullable = false)
	private UnidadDidactica unidadDidactica;

	@OneToMany(orphanRemoval = true, mappedBy = "pregunta", cascade = CascadeType.ALL)
	// @OrderColumn(name = "id", nullable = false)
	private List<PosibleRespuesta> posiblesRespuestas;

}