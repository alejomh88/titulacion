package ec.edu.utmachala.titulacion.entity;

import java.util.Date;

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
@Table(schema = "exetasi", name = "\"fechasProcesos\"")
public class FechaProceso {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "FECHASPROCESOS_ID_GENERATOR", sequenceName = "\"fechaProceso_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FECHASPROCESOS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "proceso")
	private Proceso proceso;

	@Column(length = 2)
	private String modalidad;

	@Column(length = 500)
	private String fase;

	@Column
	private boolean activo;

	@Column(name = "\"fechaInicio\"")
	private Date fechaInicio;

	@Column(name = "\"fechaFinal\"")
	private Date fechaFinal;

	@Column(length = 100)
	private String nombre;

	@Column()
	private String descripcion;

	@Column(length = 500)
	private String comentario;

	@ManyToOne
	@JoinColumn(name = "carrera")
	private Carrera carrera;

}