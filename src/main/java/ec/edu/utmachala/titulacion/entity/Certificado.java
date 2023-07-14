package ec.edu.utmachala.titulacion.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "certificados")
public class Certificado {

	@Id
	@Column(unique = true, nullable = false, length = 10)
	private String id;

	@ManyToOne
	@JoinColumn(name = "carrera")
	private Carrera carrera;

	@ManyToOne
	@JoinColumn(name = "dependencia")
	private Dependencia dependencia;

	@OneToMany(mappedBy = "certificado")
	private List<CertificadoEstudiante> certificadosEstudiantes;

}