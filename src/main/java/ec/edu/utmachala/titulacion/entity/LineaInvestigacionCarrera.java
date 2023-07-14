package ec.edu.utmachala.titulacion.entity;

import java.util.Date;
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
@Table(schema = "exetasi", name = "\"lineasInvestigacionesCarreras\"")
public class LineaInvestigacionCarrera {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "LINEASINVESTIGACIONESCARRERAS_ID_GENERATOR", sequenceName = "\"lineasInvestigacionesCarreras_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LINEASINVESTIGACIONESCARRERAS_ID_GENERATOR")
	@Column(unique = true, nullable = false, length = 2)
	private Integer id;

	@Column(name = "\"fechaResolucion\"")
	private Date fechaResolucion;

	@Column(name = "resolucion")
	private String resolucion;

	@ManyToOne
	@JoinColumn(name = "carrera", nullable = false)
	private Carrera carrera;

	@ManyToOne
	@JoinColumn(name = "\"lineaInvestigacion\"", nullable = false)
	private LineaInvestigacion lineaInvestigacion;

	@OneToMany(mappedBy = "lineaInvestigacionCarrera", cascade = CascadeType.ALL)
	private List<Seminario> seminarios;

}