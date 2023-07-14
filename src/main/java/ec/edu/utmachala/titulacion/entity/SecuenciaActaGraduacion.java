package ec.edu.utmachala.titulacion.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "\"secuenciasActasGraduaciones\"")
public class SecuenciaActaGraduacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "SECUENCIASACTASGRADUACIONES_ID_GENERATOR", sequenceName = "\"secuenciasActasGraduaciones_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIASACTASGRADUACIONES_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(length = 10)
	private String proceso;

	private Integer carrera;

	@Column(length = 2)
	private String genero;

	@Column(name = "\"numeroActaGraduacion\"", length = 10)
	private String numeroActaGraduacion;

	@Column(name = "\"fechaActaGraduacion\"")
	private Date fechaActaGraduacion;

}