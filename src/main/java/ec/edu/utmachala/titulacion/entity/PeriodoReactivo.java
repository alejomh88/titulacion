package ec.edu.utmachala.titulacion.entity;

import java.sql.Timestamp;

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
@Table(schema = "exetasi", name = "\"periodosReactivos\"")
public class PeriodoReactivo {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "PERIODOSREACTIVOS_ID_GENERATOR", sequenceName = "PERIODOSREACTIVOS_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERIODOSREACTIVOS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(name = "\"fechaCierre\"", nullable = false)
	private Timestamp fechaCierre;

	@Column(name = "\"fechaInicio\"", nullable = false)
	private Timestamp fechaInicio;

	@ManyToOne
	@JoinColumn(name = "carrera", nullable = false)
	private Carrera carrera;

}