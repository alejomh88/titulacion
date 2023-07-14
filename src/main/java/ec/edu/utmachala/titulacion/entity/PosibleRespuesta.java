package ec.edu.utmachala.titulacion.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "\"posiblesRespuestas\"")
public class PosibleRespuesta {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "POSIBLESRESPUESTAS_ID_GENERATOR", sequenceName = "\"posiblesRespuestas_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSIBLESRESPUESTAS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false)
	private String descripcion;

	@Column(name = "\"imagenDescripcion\"")
	private String imagenDescripcion;

	@Column(nullable = false, length = 1)
	private String literal;

	@ManyToOne
	@JoinColumn(name = "pregunta", nullable = false)
	private Pregunta pregunta;

}