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
@Table(schema = "exetasi", name = "\"permisosCarreras\"")
public class PermisoCarrera {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "PERMISOSCARRERAS_ID_GENERATOR", sequenceName = "\"permisosCarreras_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISOSCARRERAS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "usuario", nullable = false)
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "carrera", nullable = false)
	private Carrera carrera;

}