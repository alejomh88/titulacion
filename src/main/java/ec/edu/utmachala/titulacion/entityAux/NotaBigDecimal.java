package ec.edu.utmachala.titulacion.entityAux;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class NotaBigDecimal {

	@Id
	private BigDecimal nota;

	public BigDecimal getNota() {
		return nota;
	}

	public void setNota(BigDecimal nota) {
		this.nota = nota;
	}

}
