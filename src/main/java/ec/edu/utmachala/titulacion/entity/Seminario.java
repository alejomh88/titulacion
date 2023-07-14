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
@Table(schema = "exetasi", name = "seminarios")
public class Seminario {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "SEMINARIOS_ID_GENERATOR", sequenceName = "SEMINARIOS_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEMINARIOS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false, length = 200)
	private String nombre;

	@ManyToOne
	@JoinColumn(name = "\"lineaInvestigacionCarrera\"", nullable = false)
	private LineaInvestigacionCarrera lineaInvestigacionCarrera;

	@ManyToOne
	@JoinColumn(name = "proceso", nullable = false)
	private Proceso proceso;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "seminario")
	private List<DocenteSeminario> docentesSeminarios;

	@OneToMany(mappedBy = "seminario", cascade = CascadeType.ALL)
	private List<SeminarioTrabajoTitulacion> seminariosTrabajosTitulacion;

}