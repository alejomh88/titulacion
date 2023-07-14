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
@Table(schema = "exetasi", name = "roles")
public class Rol {

	@Id
	@Column(unique = true, nullable = false, length = 4)
	private String id;

	@OneToMany(mappedBy = "rol")
	private List<Permiso> permisos;

}