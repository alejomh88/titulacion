package ec.edu.utmachala.titulacion.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "\"formasPresentaciones\"")
public class FormaPresentacion {

	@Id
	@Column(unique = true, nullable = false, length = 3)
	private String id;

	@Column(name = "nombre", nullable = false, length = 10)
	private String nombre;

	@OneToMany(mappedBy = "formaPresentacion", cascade = CascadeType.ALL)
	private List<Criterio> criterios;

}
