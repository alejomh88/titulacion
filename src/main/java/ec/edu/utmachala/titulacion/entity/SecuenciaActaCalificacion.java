package ec.edu.utmachala.titulacion.entity;

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
@Table(schema = "exetasi", name = "\"secuenciasActasCalificaciones\"")
public class SecuenciaActaCalificacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "SECUENCIASACTASCALIFICACIONES_ID_GENERATOR", sequenceName = "\"secuenciasActasCalificaciones_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIASACTASCALIFICACIONES_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(length = 10)
	private String proceso;

	private Integer idt;

	@Column(length = 2)
	private String titulacion;

}