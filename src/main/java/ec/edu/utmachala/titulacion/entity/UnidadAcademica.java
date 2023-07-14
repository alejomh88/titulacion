package ec.edu.utmachala.titulacion.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "\"unidadesAcademicas\"")
public class UnidadAcademica {

	@Id
	@Column(unique = true, nullable = false, length = 5)
	private String id;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(name = "\"cedulaSecretarioAbogado\"", length = 10, nullable = false)
	private String cedulaSecretarioAbogado;

	@Column(name = "\"nombreSecretarioAbogado\"", length = 50, nullable = false)
	private String nombreSecretarioAbogado;

	@Column(name = "\"nombreJefeUmmog\"", length = 50, nullable = false)
	private String nombreJefeUmmog;

	@Column(name = "\"nombreDecano\"", length = 50, nullable = false)
	private String nombreDecano;

	@OneToMany(mappedBy = "unidadAcademica")
	private List<Carrera> carreras;

	@Column(name = "\"secuenciaActaGraduacion\"")
	private Integer secuenciaActaGraduacion;

}