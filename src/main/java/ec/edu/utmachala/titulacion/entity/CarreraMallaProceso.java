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
@Table(schema = "exetasi", name = "\"carrerasMallasProcesos\"")
public class CarreraMallaProceso {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "CARRERASMALLASPROCESOS_ID_GENERATOR", sequenceName = "CARRERASMALLASPROCESOS_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CARRERASMALLASPROCESOS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "\"mallaCurricular\"", nullable = false)
	private MallaCurricular mallaCurricular;

	@ManyToOne
	@JoinColumn(name = "carrera", nullable = false)
	private Carrera carrera;

	@ManyToOne
	@JoinColumn(name = "proceso", nullable = false)
	private Proceso proceso;

}