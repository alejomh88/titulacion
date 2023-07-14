package ec.edu.utmachala.titulacion.entity;

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
@Table(schema = "exetasi", name = "\"certificadosEstudiantes\"")
public class CertificadoEstudiante {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "CERTIFICADOSESTUDIANTES_ID_GENERATOR", sequenceName = "CERTIFICADOSESTUDIANTES_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CERTIFICADOSESTUDIANTES_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "epp")
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

	@ManyToOne
	@JoinColumn(name = "ett")
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	@Column(name = "adeuda")
	private Boolean adeuda;

	@ManyToOne
	@JoinColumn(name = "certificado")
	private Certificado certificado;

	@Column(name = "\"descripcionAdeuda\"", length = 500)
	private String descripcionAdeuda;

	@Column
	private Boolean devolvio;

	@Column(name = "\"descripcionDevolvio\"", length = 500)
	private String descripcionDevolvio;

}