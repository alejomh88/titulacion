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
@Table(schema = "exetasi", name = "asignaturas")
public class Asignatura {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "ASIGNATURAS_ID_GENERATOR", sequenceName = "ASIGNATURAS_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ASIGNATURAS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String nombre;

	@ManyToOne
	@JoinColumn(name = "\"mallaCurricular\"", nullable = false)
	private MallaCurricular mallaCurricular;

	@ManyToOne
	@JoinColumn(name = "\"unidadCurricular\"", nullable = false)
	private UnidadCurricular unidadCurricular;

	@Column(nullable = false)
	private Boolean activo;

	@OneToMany(mappedBy = "asignatura")
	private List<DocenteAsignatura> docentesAsignaturas;

	@OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL)
	private List<UnidadDidactica> unidadesDidacticas;
	
}