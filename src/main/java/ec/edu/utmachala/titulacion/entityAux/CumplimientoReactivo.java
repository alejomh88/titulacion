package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CumplimientoReactivo {

	@Id
	private String id;
	private String unidadAcademica;
	private String carrera;
	private String asignatura;
	private String unidadCurricular;
	private String unidadDidactica;
	private String cedula;
	private String telefono;
	private String email;
	private String nombreDocente;
	private Long numeroPreguntasRealizadas;

	public CumplimientoReactivo() {

	}

	public CumplimientoReactivo(String id, String unidadAcademica, String carrera, String asignatura,
			String unidadCurricular, String unidadDidactica, String cedula, String telefono, String email,
			String nombreDocente, Long numeroPreguntasRealizadas) {
		super();
		this.id = id;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.asignatura = asignatura;
		this.unidadCurricular = unidadCurricular;
		this.unidadDidactica = unidadDidactica;
		this.cedula = cedula;
		this.telefono = telefono;
		this.email = email;
		this.nombreDocente = nombreDocente;
		this.numeroPreguntasRealizadas = numeroPreguntasRealizadas;
	}

	public String getAsignatura() {
		return asignatura;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getCedula() {
		return cedula;
	}

	public String getEmail() {
		return email;
	}

	public String getId() {
		return id;
	}

	public String getNombreDocente() {
		return nombreDocente;
	}

	public Long getNumeroPreguntasRealizadas() {
		return numeroPreguntasRealizadas;
	}

	public String getTelefono() {
		return telefono;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public String getUnidadCurricular() {
		return unidadCurricular;
	}

	public String getUnidadDidactica() {
		return unidadDidactica;
	}

	public void setAsignatura(String asignatura) {
		this.asignatura = asignatura;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNombreDocente(String nombreDocente) {
		this.nombreDocente = nombreDocente;
	}

	public void setNumeroPreguntasRealizadas(Long numeroPreguntasRealizadas) {
		this.numeroPreguntasRealizadas = numeroPreguntasRealizadas;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

	public void setUnidadCurricular(String unidadCurricular) {
		this.unidadCurricular = unidadCurricular;
	}

	public void setUnidadDidactica(String unidadDidactica) {
		this.unidadDidactica = unidadDidactica;
	}

}
