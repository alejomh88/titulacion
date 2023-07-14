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
@Table(schema = "exetasi", name = "permisos")
public class Permiso {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "PERMISOS_ID_GENERATOR", sequenceName = "PERMISOS_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISOS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "rol", nullable = false)
	private Rol rol;

	@ManyToOne
	@JoinColumn(name = "usuario", nullable = false)
	private Usuario usuario;

}
