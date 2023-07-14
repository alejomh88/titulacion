package ec.edu.utmachala.titulacion.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "\"calificacionesTrabajosTitulacion\"")
public class CalificacionTrabajoTitulacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "CALIFICACIONTRABAJOTITULACION_ID_GENERATOR", sequenceName = "\"calificacionesTrabajosTitulacion_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CALIFICACIONTRABAJOTITULACION_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "\"estudianteTrabajoTitulacion\"", nullable = false)
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	@ManyToOne
	@JoinColumn(name = "\"indicadorTrabajoTitulacion\"", nullable = false)
	private IndicadorTrabajoTitulacion indicadorTrabajoTitulacion;

	@ManyToOne
	@JoinColumn(name = "especialista", nullable = false)
	private Usuario especialista;

	@Column(name = "calificacion", precision = 4, scale = 2)
	private BigDecimal calificacion;

}
