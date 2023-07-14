package ec.edu.utmachala.titulacion.entity;

import java.sql.Timestamp;
import java.util.List;

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
@Table(schema = "exetasi", name = "grupos")
public class Grupo {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "GRUPO_ID_GENERATOR", sequenceName = "GRUPO_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GRUPO_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false, length = 100)
	private String bloque;

	@Column(nullable = false)
	private Integer computadoras;

	@Column(name = "\"encargadoLaboratorio\"", nullable = false, length = 100)
	private String encargadoLaboratorio;

	@Column(name = "\"nombreLaboratorio\"", nullable = false, length = 100)
	private String nombreLaboratorio;

	@Column(name = "\"fechaCierre\"", nullable = false)
	private Timestamp fechaCierre;

	@Column(name = "\"fechaInicio\"", nullable = false)
	private Timestamp fechaInicio;

	@Column(nullable = false, length = 100)
	private String responsable1;

	@Column(nullable = false, length = 100)
	private String responsable2;

	@Column(nullable = false, length = 100)
	private String supervisor;

	@ManyToOne
	@JoinColumn(name = "proceso", nullable = false)
	private Proceso proceso;

	@ManyToOne
	@JoinColumn(name = "\"tipoExamen\"", nullable = false)
	private TipoExamen tipoExamen;

	@ManyToOne
	@JoinColumn(name = "\"unidadAcademica\"", nullable = false)
	private UnidadAcademica unidadAcademica;

	@OneToMany(mappedBy = "grupo")
	private List<Examen> examen;

}