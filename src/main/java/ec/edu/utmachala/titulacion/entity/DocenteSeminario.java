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
@Table(schema = "exetasi", name = "\"docentesSeminarios\"")
public class DocenteSeminario {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "DOCENTESSEMINARIOS_ID_GENERATOR", sequenceName = "\"docentesSeminarios_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCENTESSEMINARIOS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "docente", nullable = false)
	private Usuario docente;

	@ManyToOne
	@JoinColumn(name = "seminario", nullable = false)
	private Seminario seminario;

	@OneToMany(mappedBy = "seminario", cascade = CascadeType.ALL)
	private List<SeminarioTrabajoTitulacion> seminariosTrabajosTitulacion;

}