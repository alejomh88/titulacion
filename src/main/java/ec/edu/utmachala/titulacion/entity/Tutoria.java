package ec.edu.utmachala.titulacion.entity;

import java.sql.Timestamp;

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
@Table(schema = "exetasi", name = "tutorias")
public class Tutoria {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "TUTORIAS_ID_GENERATOR", sequenceName = "tutorias_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TUTORIAS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false)
	private String actividad;

	@Column(name = "\"tareasNoRealizadas\"")
	private String tareasNoRealizadas;

	@Column(name = "\"tareasRealizadas\"")
	private String tareasRealizadas;

	@Column
	private String tarea;

	@Column(nullable = false)
	private Timestamp fecha;

	@Column(name = "\"fechaProximaTutoria\"")
	private Timestamp fechaProximaTutoria;

	@Column(name = "\"fechaFinTutoria\"")
	private Timestamp fechaFinTutoria;

	@Column(name = "\"fechaFinProximaTutoria\"")
	private Timestamp fechaFinProximaTutoria;

	@ManyToOne
	@JoinColumn(name = "\"estudianteTrabajoTitulacion\"", nullable = false)
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	@Column(name = "\"comentarioEstudiante\"")
	private String comentarioEstudiante;

	@ManyToOne
	@JoinColumn(name = "\"docente\"")
	private Usuario docente;

}