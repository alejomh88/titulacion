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
@Table(schema = "exetasi", name = "\"seminariosTrabajosTitulacion\"")
public class SeminarioTrabajoTitulacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "SEMINARIOSTRABAJOSTITULACION_ID_GENERATOR", sequenceName = "\"seminariosTrabajosTitulacion_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEMINARIOSTRABAJOSTITULACION_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "seminario", nullable = false)
	private Seminario seminario;

	@ManyToOne
	@JoinColumn(name = "\"trabajoTitulacion\"", nullable = false)
	private TrabajoTitulacion trabajoTitulacion;

	@ManyToOne
	@JoinColumn(name = "\"docenteSeminario\"", nullable = false)
	private DocenteSeminario docenteSeminario;

	@OneToMany(mappedBy = "seminarioTrabajoTitulacion", cascade = CascadeType.ALL)
	private List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacion;

}