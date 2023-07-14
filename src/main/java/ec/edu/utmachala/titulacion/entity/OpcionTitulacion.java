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
@Table(schema = "exetasi", name = "\"opcionesTitulacion\"")
public class OpcionTitulacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "OPCIONESTITULACION_ID_GENERATOR", sequenceName = "\"opcionesTitulacion_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OPCIONESTITULACION_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false, length = 200)
	private String nombre;

}