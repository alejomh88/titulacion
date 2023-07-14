package ec.edu.utmachala.titulacion.entity;

import java.math.BigDecimal;
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
@Table(schema = "exetasi", name = "\"indicadoresTrabajosTitulacion\"")
public class IndicadorTrabajoTitulacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "INDICADORTRABAJOTITULACION_ID_GENERATOR", sequenceName = "\"indicadoresTrabajosTitulacion_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INDICADORTRABAJOTITULACION_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "\"criterioTrabajoTitulacion\"", nullable = false)
	private CriterioTrabajoTitulacion criterioTrabajoTitulacion;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "\"pesoInferior\"", nullable = false, precision = 4, scale = 2)
	private BigDecimal pesoInferior;

	@Column(name = "\"pesoSuperior\"", nullable = false, precision = 4, scale = 2)
	private BigDecimal pesoSuperior;

	@OneToMany(mappedBy = "indicadorTrabajoTitulacion", cascade = CascadeType.ALL)
	private List<CalificacionTrabajoTitulacion> calificacionesTrabajosTitulaciones;

}
