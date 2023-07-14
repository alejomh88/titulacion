package ec.edu.utmachala.titulacion.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "\"mallasCurriculares\"")
public class MallaCurricular {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "MALLASCURRICULARES_ID_GENERATOR", sequenceName = "MALLASCURRICULARES_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MALLASCURRICULARES_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@OneToMany(mappedBy = "mallaCurricular")
	private List<CarreraMallaProceso> carreraMallaProceso;

	@Column(name = "observaciones", length = 100)
	private String observaciones;

	@OneToMany(mappedBy = "mallaCurricular")
	private List<Asignatura> asignaturas;

}