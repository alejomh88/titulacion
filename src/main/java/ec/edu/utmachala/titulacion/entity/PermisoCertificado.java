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
@Table(schema = "exetasi", name = "\"permisosCertificados\"")
public class PermisoCertificado {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "PERMISOSCERTIFICADOS_ID_GENERATOR", sequenceName = "PERMISOSCERTIFICADOS_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERMISOSCERTIFICADOS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "usuario")
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "dependencia")
	private Dependencia dependencia;

}