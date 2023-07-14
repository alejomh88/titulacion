package ec.edu.utmachala.titulacion.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
@Table(schema = "exetasi", name = "\"lineasInvestigaciones\"")
public class LineaInvestigacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "LINEASINVESTIGACIONES_ID_GENERATOR", sequenceName = "\"lineasInvestigaciones_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LINEASINVESTIGACIONES_ID_GENERATOR")
	@Column(unique = true, nullable = false, length = 2)
	private Integer id;

	@Column
	private String nombre;

	@Column(name = "\"fechaResolucion\"")
	private Date fechaResolucion;

	@Column(name = "resolucion")
	private String resolucion;

	@Column(name = "proceso")
	private String proceso;

	@OneToMany(mappedBy = "lineaInvestigacion", cascade = CascadeType.ALL)
	private List<LineaInvestigacionCarrera> lineasInvestigacionesCarreras;

}