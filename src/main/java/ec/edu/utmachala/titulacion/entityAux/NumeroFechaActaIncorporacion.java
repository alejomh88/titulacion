package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class NumeroFechaActaIncorporacion {

	@Id
	private String id;
	private String numeroActa;
	private String fechaActa;

	public NumeroFechaActaIncorporacion() {
	}

	public NumeroFechaActaIncorporacion(String id, String numeroActa, String fechaActa) {
		this.id = id;
		this.numeroActa = numeroActa;
		this.fechaActa = fechaActa;
	}

	public String getFechaActa() {
		return fechaActa;
	}

	public String getId() {
		return id;
	}

	public String getNumeroActa() {
		return numeroActa;
	}

	public void setFechaActa(String fechaActa) {
		this.fechaActa = fechaActa;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNumeroActa(String numeroActa) {
		this.numeroActa = numeroActa;
	}

}
