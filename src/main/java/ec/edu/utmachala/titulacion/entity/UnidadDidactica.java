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
@Table(schema = "exetasi", name = "\"unidadesDidacticas\"")
public class UnidadDidactica {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "UNIDADESDIDACTICAS_ID_GENERATOR", sequenceName = "\"unidadesDidacticas_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UNIDADESDIDACTICAS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false, length = 500)
	private String nombre;

	@ManyToOne
	@JoinColumn(name = "asignatura", nullable = false)
	private Asignatura asignatura;

	@Column(nullable = false)
	private Boolean activo;

	@OneToMany(mappedBy = "unidadDidactica", cascade = CascadeType.ALL)
	private List<Pregunta> preguntas;

}