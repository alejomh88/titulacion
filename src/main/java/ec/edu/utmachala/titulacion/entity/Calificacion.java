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
@Table(schema = "exetasi", name = "calificaciones")
public class Calificacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "CALIFICACION_ID_GENERATOR", sequenceName = "calificaciones_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CALIFICACION_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "\"temaPractico\"", nullable = false)
	private EstudianteExamenComplexivoPP temaPractico;

	@ManyToOne
	@JoinColumn(name = "indicador", nullable = false)
	private Indicador indicador;

	@ManyToOne
	@JoinColumn(name = "especialista", nullable = false)
	private Usuario especialista;

	@ManyToOne
	@JoinColumn(name = "\"tipoExamen\"", nullable = false)
	private TipoExamen tipoExamen;

	@Column(name = "calificacion", precision = 4, scale = 2)
	private BigDecimal calificacion;

}
