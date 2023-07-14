package ec.edu.utmachala.titulacion.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "dependencias")
public class Dependencia {

	@Id
	@Column(unique = true, nullable = false, length = 10)
	private String id;

	@Column(length = 100)
	private String nombre;

	@Column(length = 100)
	private String observacion;

	@OneToMany(mappedBy = "dependencia")
	private List<Certificado> certificados;

	@OneToMany(mappedBy = "dependencia")
	private List<PermisoCertificado> permisoscertificados;

}