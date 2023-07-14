package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CumplimientoTutoria {

	@Id
	private String estudiante;
	private String seminario;
	private String docente;
	private String opcionTitulacion;
	private Integer cantidadTutorias;

	public CumplimientoTutoria() {

	}

	public CumplimientoTutoria(String estudiante, String seminario, String docente, String opcionTitulacion,
			Integer cantidadTutorias) {
		super();
		this.estudiante = estudiante;
		this.seminario = seminario;
		this.docente = docente;
		this.opcionTitulacion = opcionTitulacion;
		this.cantidadTutorias = cantidadTutorias;
	}

	public Integer getCantidadTutorias() {
		return cantidadTutorias;
	}

	public String getDocente() {
		return docente;
	}

	public String getEstudiante() {
		return estudiante;
	}

	public String getOpcionTitulacion() {
		return opcionTitulacion;
	}

	public String getSeminario() {
		return seminario;
	}

	public void setCantidadTutorias(Integer cantidadTutorias) {
		this.cantidadTutorias = cantidadTutorias;
	}

	public void setDocente(String docente) {
		this.docente = docente;
	}

	public void setEstudiante(String estudiante) {
		this.estudiante = estudiante;
	}

	public void setOpcionTitulacion(String opcionTitulacion) {
		this.opcionTitulacion = opcionTitulacion;
	}

	public void setSeminario(String seminario) {
		this.seminario = seminario;
	}

}
