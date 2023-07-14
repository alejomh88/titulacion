package ec.edu.utmachala.titulacion.entityAux;

public class Caratula {

	private String unidadAcademica;
	private String carrera;
	private String titulo;
	private String gradoAcademico;
	private String autor1;
	private String autor2;
	private String autor3;
	private String tutor1;
	private String tutor2;
	private String tutor3;
	private String tutorSuplente1;
	private String tutorSuplente2;
	private String secretarioAbogado;
	private String lugarMesAnio;
	private boolean genero;
	private String fechaSustentacion;
	private String resolucion;
	private String fechaResolucion;

	public Caratula() {

	}

	public Caratula(String unidadAcademica, String carrera, String titulo, String gradoAcademico, String autor1,
			String autor2, String autor3, String tutor1, String tutor2, String secretarioAbogado, String tutor3,
			String tutorSuplente1, String tutorSuplente2, String lugarMesAnio, boolean genero, String fechaSustentacion,
			String resolucion, String fechaResolucion) {
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.titulo = titulo;
		this.gradoAcademico = gradoAcademico;
		this.autor1 = autor1;
		this.autor2 = autor2;
		this.autor3 = autor3;
		this.tutor1 = tutor1;
		this.tutor2 = tutor2;
		this.tutor3 = tutor3;
		this.tutorSuplente1 = tutorSuplente1;
		this.tutorSuplente2 = tutorSuplente2;
		this.secretarioAbogado = secretarioAbogado;
		this.lugarMesAnio = lugarMesAnio;
		this.genero = genero;
		this.fechaSustentacion = fechaSustentacion;
		this.resolucion = resolucion;
		this.fechaResolucion = fechaResolucion;
	}

	public String getAutor1() {
		return autor1;
	}

	public String getAutor2() {
		return autor2;
	}

	public String getAutor3() {
		return autor3;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getFechaResolucion() {
		return fechaResolucion;
	}

	public String getFechaSustentacion() {
		return fechaSustentacion;
	}

	public String getGradoAcademico() {
		return gradoAcademico;
	}

	public String getLugarMesAnio() {
		return lugarMesAnio;
	}

	public String getResolucion() {
		return resolucion;
	}

	public String getSecretarioAbogado() {
		return secretarioAbogado;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getTutor1() {
		return tutor1;
	}

	public String getTutor2() {
		return tutor2;
	}

	public String getTutor3() {
		return tutor3;
	}

	public String getTutorSuplente1() {
		return tutorSuplente1;
	}

	public String getTutorSuplente2() {
		return tutorSuplente2;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public boolean isGenero() {
		return genero;
	}

	public void setAutor1(String autor1) {
		this.autor1 = autor1;
	}

	public void setAutor2(String autor2) {
		this.autor2 = autor2;
	}

	public void setAutor3(String autor3) {
		this.autor3 = autor3;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setFechaResolucion(String fechaResolucion) {
		this.fechaResolucion = fechaResolucion;
	}

	public void setFechaSustentacion(String fechaSustentacion) {
		this.fechaSustentacion = fechaSustentacion;
	}

	public void setGenero(boolean genero) {
		this.genero = genero;
	}

	public void setGradoAcademico(String gradoAcademico) {
		this.gradoAcademico = gradoAcademico;
	}

	public void setLugarMesAnio(String lugarMesAnio) {
		this.lugarMesAnio = lugarMesAnio;
	}

	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}

	public void setSecretarioAbogado(String secretarioAbogado) {
		this.secretarioAbogado = secretarioAbogado;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setTutor1(String tutor1) {
		this.tutor1 = tutor1;
	}

	public void setTutor2(String tutor2) {
		this.tutor2 = tutor2;
	}

	public void setTutor3(String tutor3) {
		this.tutor3 = tutor3;
	}

	public void setTutorSuplente1(String tutorSuplente1) {
		this.tutorSuplente1 = tutorSuplente1;
	}

	public void setTutorSuplente2(String tutorSuplente2) {
		this.tutorSuplente2 = tutorSuplente2;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
