package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Texto {

	@Id
	private String id;

	public Texto() {
	}

	public Texto(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
