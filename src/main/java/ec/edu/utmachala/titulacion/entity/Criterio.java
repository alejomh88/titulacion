package ec.edu.utmachala.titulacion.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "criterios")
public class Criterio {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "CRITERIO_ID_GENERATOR", sequenceName = "criterio_id_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRITERIO_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "\"formaPresentacion\"", nullable = false)
	private FormaPresentacion formaPresentacion;

	@ManyToOne
	@JoinColumn(name = "proceso", nullable = false)
	private Proceso proceso;

	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	@OneToMany(mappedBy = "criterio", cascade = CascadeType.ALL)
	private List<Indicador> indicadores;

}
