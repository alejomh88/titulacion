package ec.edu.utmachala.titulacion.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class EstudianteBiblioteca {

	@Id
	private String id;

	private String modalidad;

	private Integer carrera;

	private String carreraNombre;

	private String estudiante;

	private String proceso;

	private String apellidoNombre;

	private String email;

	private String telefono;

	private String archivo;

	private Boolean validarArchivo;

	private String resumen;

	private String abstract1;

	private String palabrasClaves;

	private String tituloInvestigacion;

	private String urlBiblioteca;

	private Boolean validadoBiblioteca;

	private String opcion;

}