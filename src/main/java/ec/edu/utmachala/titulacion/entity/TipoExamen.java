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
@Table(schema = "exetasi", name = "\"tiposExamenes\"")
public class TipoExamen {

	@Id
	@Column(unique = true, nullable = false, length = 2)
	private String id;

	@Column(unique = true, nullable = false, length = 50)
	private String nombre;

	@OneToMany(mappedBy = "tipoExamen")
	private List<Grupo> periodosExamenes;

}