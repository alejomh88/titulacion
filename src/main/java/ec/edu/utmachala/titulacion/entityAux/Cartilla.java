package ec.edu.utmachala.titulacion.entityAux;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cartilla {

	@Id
	private Integer id;
	private String criterio;
	private BigDecimal esp1;
	private BigDecimal esp2;
	private BigDecimal esp3;
	private BigDecimal espSupl1;
	private BigDecimal espSupl2;
	private BigDecimal totalCriterio;

	public Cartilla() {
	}

	public Cartilla(Integer id, String criterio, BigDecimal esp1, BigDecimal esp2, BigDecimal esp3) {
		this.id = id;
		this.criterio = criterio;
		this.esp1 = esp1;
		this.esp2 = esp2;
		this.esp3 = esp3;
	}

	public Cartilla(Integer id, String criterio, BigDecimal esp1, BigDecimal esp2, BigDecimal esp3, BigDecimal espSupl1,
			BigDecimal espSupl2) {
		this.id = id;
		this.criterio = criterio;
		this.esp1 = esp1;
		this.esp2 = esp2;
		this.esp3 = esp3;
		this.espSupl1 = espSupl1;
		this.espSupl2 = espSupl2;
	}

	public String getCriterio() {
		return criterio;
	}

	public BigDecimal getEsp1() {
		return esp1;
	}

	public BigDecimal getEsp2() {
		return esp2;
	}

	public BigDecimal getEsp3() {
		return esp3;
	}

	public BigDecimal getEspSupl1() {
		return espSupl1;
	}

	public BigDecimal getEspSupl2() {
		return espSupl2;
	}

	public Integer getId() {
		return id;
	}

	public BigDecimal getTotalCriterio() {
		return totalCriterio;
	}

	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

	public void setEsp1(BigDecimal esp1) {
		this.esp1 = esp1;
	}

	public void setEsp2(BigDecimal esp2) {
		this.esp2 = esp2;
	}

	public void setEsp3(BigDecimal esp3) {
		this.esp3 = esp3;
	}

	public void setEspSupl1(BigDecimal espSupl1) {
		this.espSupl1 = espSupl1;
	}

	public void setEspSupl2(BigDecimal espSupl2) {
		this.espSupl2 = espSupl2;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTotalCriterio(BigDecimal totalCriterio) {
		this.totalCriterio = totalCriterio;
	}

}
