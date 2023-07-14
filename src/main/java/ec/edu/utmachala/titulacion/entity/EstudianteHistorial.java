package ec.edu.utmachala.titulacion.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class EstudianteHistorial {

	@Id
	private String identificador;

	private String cedula;

	private String apellidosNombres;

	private String carrera;

	private Integer carreraId;

	private String opcionTitulacion;

	private String proceso;

	private String periodoProceso;

	private Date fechaInicioProceso;

	private String enlaceCronograma;

	private String calificacionFinal;

	private String estado;

}