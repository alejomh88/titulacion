package ec.edu.utmachala.titulacion.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "\"trabajosTitulacion\"")
public class TrabajoTitulacion {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "TRABAJOSTITULACION_ID_GENERATOR", sequenceName = "\"trabajosTitulacion_id_seq\"")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRABAJOSTITULACION_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(name = "\"casoInvestigacion\"", nullable = false)
	private String casoInvestigacion;

	@OneToMany(mappedBy = "trabajoTitulacion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SeminarioTrabajoTitulacion> seminariosTrabajosTitulacion;

}