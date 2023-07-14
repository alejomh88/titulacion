package ec.edu.utmachala.titulacion.entityAux;

import java.util.List;

//@Entity
public class EntidadGenerica {

	// @Id
	private String id;

	private String atributoString1;
	private String atributoString2;
	private String atributoString3;
	private String atributoString4;
	private String atributoString5;
	private String atributoString6;
	private String atributoString7;
	private String atributoString8;
	private String atributoString9;
	private String atributoString10;

	// @OneToMany(mappedBy = "entidadGenerica")
	private List<DetalleEntidadGenerica> listaDetalleEntidadGenerica;

	public EntidadGenerica() {
	}

	public EntidadGenerica(String id, String atributoString1, String atributoString2, String atributoString3,
			String atributoString4, String atributoString5, String atributoString6, String atributoString7,
			String atributoString8, String atributoString9, String atributoString10,
			List<DetalleEntidadGenerica> listaDetalleEntidadGenerica) {
		super();
		this.id = id;
		this.atributoString1 = atributoString1;
		this.atributoString2 = atributoString2;
		this.atributoString3 = atributoString3;
		this.atributoString4 = atributoString4;
		this.atributoString5 = atributoString5;
		this.atributoString6 = atributoString6;
		this.atributoString7 = atributoString7;
		this.atributoString8 = atributoString8;
		this.atributoString9 = atributoString9;
		this.atributoString10 = atributoString10;
		this.listaDetalleEntidadGenerica = listaDetalleEntidadGenerica;
	}

	public String getAtributoString1() {
		return atributoString1;
	}

	public String getAtributoString10() {
		return atributoString10;
	}

	public String getAtributoString2() {
		return atributoString2;
	}

	public String getAtributoString3() {
		return atributoString3;
	}

	public String getAtributoString4() {
		return atributoString4;
	}

	public String getAtributoString5() {
		return atributoString5;
	}

	public String getAtributoString6() {
		return atributoString6;
	}

	public String getAtributoString7() {
		return atributoString7;
	}

	public String getAtributoString8() {
		return atributoString8;
	}

	public String getAtributoString9() {
		return atributoString9;
	}

	public String getId() {
		return id;
	}

	public List<DetalleEntidadGenerica> getListaDetalleEntidadGenerica() {
		return listaDetalleEntidadGenerica;
	}

	public void setAtributoString1(String atributoString1) {
		this.atributoString1 = atributoString1;
	}

	public void setAtributoString10(String atributoString10) {
		this.atributoString10 = atributoString10;
	}

	public void setAtributoString2(String atributoString2) {
		this.atributoString2 = atributoString2;
	}

	public void setAtributoString3(String atributoString3) {
		this.atributoString3 = atributoString3;
	}

	public void setAtributoString4(String atributoString4) {
		this.atributoString4 = atributoString4;
	}

	public void setAtributoString5(String atributoString5) {
		this.atributoString5 = atributoString5;
	}

	public void setAtributoString6(String atributoString6) {
		this.atributoString6 = atributoString6;
	}

	public void setAtributoString7(String atributoString7) {
		this.atributoString7 = atributoString7;
	}

	public void setAtributoString8(String atributoString8) {
		this.atributoString8 = atributoString8;
	}

	public void setAtributoString9(String atributoString9) {
		this.atributoString9 = atributoString9;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setListaDetalleEntidadGenerica(List<DetalleEntidadGenerica> listaDetalleEntidadGenerica) {
		this.listaDetalleEntidadGenerica = listaDetalleEntidadGenerica;
	}

}
