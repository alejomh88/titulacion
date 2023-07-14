package ec.edu.utmachala.titulacion.entityAux;

public class DetalleActaTemasTitulosResolucion {

	private int no;
	private String egresado;
	private String cedula;
	private String tutor;
	private String titulo;
	private String casoPractico;
	private String fechaSustentacion;
	private String especialista1;
	private String especialista2;
	private String especialista3;
	private String especialistaSuplente1;
	private String especialistaSuplente2;

	public DetalleActaTemasTitulosResolucion() {
	}

	public DetalleActaTemasTitulosResolucion(int no, String egresado, String cedula, String tutor, String titulo,
			String casoPractico, String fechaSustentacion, String especialista1, String especialista2,
			String especialista3, String especialistaSuplente1, String especialistaSuplente2) {
		this.no = no;
		this.egresado = egresado;
		this.cedula = cedula;
		this.tutor = tutor;
		this.titulo = titulo;
		this.casoPractico = casoPractico;
		this.fechaSustentacion = fechaSustentacion;
		this.especialista1 = especialista1;
		this.especialista2 = especialista2;
		this.especialista3 = especialista3;
		this.especialistaSuplente1 = especialistaSuplente1;
		this.especialistaSuplente2 = especialistaSuplente2;
	}

	public String getCasoPractico() {
		return casoPractico;
	}

	public String getCedula() {
		return cedula;
	}

	public String getEgresado() {
		return egresado;
	}

	public String getEspecialista1() {
		return especialista1;
	}

	public String getEspecialista2() {
		return especialista2;
	}

	public String getEspecialista3() {
		return especialista3;
	}

	public String getEspecialistaSuplente1() {
		return especialistaSuplente1;
	}

	public String getEspecialistaSuplente2() {
		return especialistaSuplente2;
	}

	public String getFechaSustentacion() {
		return fechaSustentacion;
	}

	public int getNo() {
		return no;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getTutor() {
		return tutor;
	}

	public void setCasoPractico(String casoPractico) {
		this.casoPractico = casoPractico;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public void setEgresado(String egresado) {
		this.egresado = egresado;
	}

	public void setEspecialista1(String especialista1) {
		this.especialista1 = especialista1;
	}

	public void setEspecialista2(String especialista2) {
		this.especialista2 = especialista2;
	}

	public void setEspecialista3(String especialista3) {
		this.especialista3 = especialista3;
	}

	public void setEspecialistaSuplente1(String especialistaSuplente1) {
		this.especialistaSuplente1 = especialistaSuplente1;
	}

	public void setEspecialistaSuplente2(String especialistaSuplente2) {
		this.especialistaSuplente2 = especialistaSuplente2;
	}

	public void setFechaSustentacion(String fechaSustentacion) {
		this.fechaSustentacion = fechaSustentacion;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

}
