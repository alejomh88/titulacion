package ec.edu.utmachala.titulacion.entityAux;

import java.math.BigDecimal;

public class CartillaAlumno {

	private String nombre;
	private BigDecimal ord;
	private BigDecimal gra;
	private BigDecimal calFinal;

	public CartillaAlumno() {
	}

	public CartillaAlumno(String nombre, BigDecimal ord, BigDecimal gra, BigDecimal calFinal) {
		super();
		this.nombre = nombre;
		this.ord = ord;
		this.gra = gra;
		this.calFinal = calFinal;
	}

	public BigDecimal getCalFinal() {
		return calFinal;
	}

	public BigDecimal getGra() {
		return gra;
	}

	public String getNombre() {
		return nombre;
	}

	public BigDecimal getOrd() {
		return ord;
	}

	public void setCalFinal(BigDecimal calFinal) {
		this.calFinal = calFinal;
	}

	public void setGra(BigDecimal gra) {
		this.gra = gra;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setOrd(BigDecimal ord) {
		this.ord = ord;
	}

}
