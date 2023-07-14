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
@Table(schema = "exetasi", name = "\"nivelesDificultades\"")
public class NivelDificultad {

	@Id
	@Column(unique = true, nullable = false, length = 2)
	private String id;

	@Column(nullable = false, unique = true, length = 20)
	private String nombre;

	@Column(nullable = false)
	private Integer tiempo;

	@OneToMany(mappedBy = "nivelDificultad")
	private List<Pregunta> preguntas;

}