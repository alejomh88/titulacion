package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.Utils.getText;
import static ec.edu.utmachala.titulacion.utility.UtilsArchivos.determinarExtension;
import static ec.edu.utmachala.titulacion.utility.UtilsArchivos.getRutaImagen;
import static ec.edu.utmachala.titulacion.utility.UtilsArchivos.guardarImagen;
import static ec.edu.utmachala.titulacion.utility.UtilsArchivos.leerImagen;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Asignatura;
import ec.edu.utmachala.titulacion.entity.NivelDificultad;
import ec.edu.utmachala.titulacion.entity.PosibleRespuesta;
import ec.edu.utmachala.titulacion.entity.Pregunta;
import ec.edu.utmachala.titulacion.entity.UnidadDidactica;
import ec.edu.utmachala.titulacion.service.AsignaturaService;
import ec.edu.utmachala.titulacion.service.NivelDificultadService;
import ec.edu.utmachala.titulacion.service.PreguntaService;
import ec.edu.utmachala.titulacion.service.UnidadDidacticaService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class AdministrarPreguntasIdBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private AsignaturaService asignaturaService;

	@Autowired
	private UnidadDidacticaService unidadDidacticaService;

	@Autowired
	private PreguntaService preguntaService;

	@Autowired
	private NivelDificultadService nivelDificultadService;

	private List<Asignatura> listAsignaturas;
	private int asignatura;
	private Asignatura asignaturaSeleccionada;

	private List<UnidadDidactica> listUnidadesDidacticas;
	private int unidadDidactica;
	private UnidadDidactica unidadDidacticaSeleccionada;

	private List<Pregunta> listaPreguntas;

	private Pregunta pregunta;

	private List<NivelDificultad> listNivelDificultad;

	private StreamedContent imagenEnunciado;
	private StreamedContent imagenOpcionA;
	private StreamedContent imagenOpcionB;
	private StreamedContent imagenOpcionC;
	private StreamedContent imagenOpcionD;

	private Integer idPregunta;

	public void actualizar() {
		if (pregunta.getNivelDificultad().getId().compareTo("0") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el nivel de dificultad");
		} else if (pregunta.getEjeTematico().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el eje temÃ¡tico");
		} else if (pregunta.getBibliografia().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la bibliografÃ­a");
		} else if (pregunta.getJustificacion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la justificaciÃ³n");
		} else if (pregunta.getEnunciado().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el enunciado");
		} else if (pregunta.getPosiblesRespuestas().get(0).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opciÃ³n A");
		} else if (pregunta.getPosiblesRespuestas().get(1).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opciÃ³n B");
		} else if (pregunta.getPosiblesRespuestas().get(2).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opciÃ³n C");
		} else if (pregunta.getPosiblesRespuestas().get(3).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opciÃ³n D");
		} else if ((pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() != null
				&& (pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() == null
						|| pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() == null
						|| pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() == null))
				|| (pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() != null
						&& (pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() == null))
				|| (pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() != null
						&& (pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() == null))
				|| (pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() != null
						&& (pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() == null))) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Si va a usar imagenes en las opciones de respuesta, es obligatorio hacerlo en las 4 opciones");
		} else if (pregunta.getSostiApantisi().compareTo("--") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Escoja la respuesta correcta");
		} else {
			pregunta.setSostiApantisi(new ShaPasswordEncoder().encodePassword(pregunta.getSostiApantisi(), null));
			preguntaService.actualizar(pregunta);
			buscarReactivosTeoricos();
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("administrarPreguntasId.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void buscarReactivosTeoricos() {
		if (idPregunta == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese un id para buscar");
		} else {
			listaPreguntas = preguntaService.obtenerLista(
					"select distinct p from Pregunta p inner join fetch p.posiblesRespuestas pr "
							+ "inner join p.unidadDidactica ud inner join ud.asignatura a "
							+ "inner join a.mallaCurricular mc inner join mc.carrera c "
							+ "inner join c.permisosCarreras pc inner join pc.usuario u "
							+ "where u.email=?1 and p.id=?2 order by p.id, pr.literal",
					new Object[] {
							SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
							idPregunta },
					0, false, new Object[0]);
			if (listaPreguntas.isEmpty())
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No existe el ID de la pregunta en la carrera");
		}
	}

	public void buscarUnidadDidactica() {
		unidadDidactica = 0;
		listUnidadesDidacticas = unidadDidacticaService.obtenerLista(
				"select ud from UnidadDidactica ud inner join ud.asignatura a where a.id=?1 and ud.activo=true",
				new Object[] { asignatura }, 0, false, new Object[0]);
		if (listUnidadesDidacticas.size() == 1) {
			unidadDidactica = listUnidadesDidacticas.get(0).getId();
		}
	}

	public void cargarEditar() {
		if (pregunta.getImagenEnunciado() != null)
			imagenEnunciado = new DefaultStreamedContent(leerImagen(pregunta.getImagenEnunciado()),
					"image/" + determinarExtension(pregunta.getImagenEnunciado()));

		if (pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() != null)
			imagenOpcionA = new DefaultStreamedContent(
					leerImagen(pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion()),
					"image/" + determinarExtension(pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion()));

		if (pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() != null)
			imagenOpcionB = new DefaultStreamedContent(
					leerImagen(pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion()),
					"image/" + determinarExtension(pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion()));

		if (pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() != null)
			imagenOpcionC = new DefaultStreamedContent(
					leerImagen(pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion()),
					"image/" + determinarExtension(pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion()));

		if (pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() != null)
			imagenOpcionD = new DefaultStreamedContent(
					leerImagen(pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion()),
					"image/" + determinarExtension(pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion()));

		if (pregunta.getSostiApantisi().compareTo(new ShaPasswordEncoder().encodePassword("A", null)) == 0)
			pregunta.setSostiApantisi("A");
		else if (pregunta.getSostiApantisi().compareTo(new ShaPasswordEncoder().encodePassword("B", null)) == 0)
			pregunta.setSostiApantisi("B");
		else if (pregunta.getSostiApantisi().compareTo(new ShaPasswordEncoder().encodePassword("C", null)) == 0)
			pregunta.setSostiApantisi("C");
		else if (pregunta.getSostiApantisi().compareTo(new ShaPasswordEncoder().encodePassword("D", null)) == 0)
			pregunta.setSostiApantisi("D");

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("editarReactivosTeoricosId.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 13 ENTER
	// 10 AVANCE DE CARRO
	// 32 ESPACIO
	public void cargarVisualizar() {
		String cadena = pregunta.getEnunciado();
		System.out.println("--ORIGINAL--");
		System.out.println(cadena);

		for (int i = 0; i < cadena.length(); i++) {
			char cc = cadena.charAt(i);
			if (cc == 10) {
				String cadenaInsertar = "<br>";
				String principioCadena = pregunta.getEnunciado().substring(0, i);
				String finCadena = pregunta.getEnunciado().substring(i);

				pregunta.setEnunciado(principioCadena + cadenaInsertar + finCadena);

				System.out.println("PRINCIPO DE LA CADENA");
				System.out.println(principioCadena);

				System.out.println("FINAL DE LA CADENA");
				System.out.println(finCadena);
				cadena = pregunta.getEnunciado();
				i = i + 5;
				// break;
			}
		}
		String espaciado = "<span class=\"Apple-tab-span\" style=\"white-space:pre\">	</span>";
		pregunta.setEnunciado(pregunta.getEnunciado().replace("<esp>", espaciado));

		System.out.println("CADENA RESULTANTE");
		System.out.println(pregunta.getEnunciado());

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("examenVisualizar.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void eliminarImagenEnunciado() {
		pregunta.setImagenEnunciado(null);
		imagenEnunciado = null;
	}

	public void eliminarImagenOpcionA() {
		pregunta.getPosiblesRespuestas().get(0).setImagenDescripcion(null);
		imagenOpcionA = null;
	}

	public void eliminarImagenOpcionB() {
		pregunta.getPosiblesRespuestas().get(1).setImagenDescripcion(null);
		imagenOpcionB = null;
	}

	public void eliminarImagenOpcionC() {
		pregunta.getPosiblesRespuestas().get(2).setImagenDescripcion(null);
		imagenOpcionC = null;
	}

	public void eliminarImagenOpcionD() {
		pregunta.getPosiblesRespuestas().get(3).setImagenDescripcion(null);
		imagenOpcionD = null;
	}

	public void eliminarPregunta(Pregunta pregunta) {
		if (pregunta.getActivo())
			pregunta.setActivo(false);
		else
			pregunta.setActivo(true);
		preguntaService.actualizar(pregunta);
		buscarReactivosTeoricos();
	}

	public void FormatearTagHtml() {
		quitarTagHtml();
		// preguntaService.actualizar(pregunta);
	}

	public int getAsignatura() {
		return asignatura;
	}

	public Asignatura getAsignaturaSeleccionada() {
		return asignaturaSeleccionada;
	}

	public Integer getIdPregunta() {
		return idPregunta;
	}

	public StreamedContent getImagenEnunciado() {
		return imagenEnunciado;
	}

	public StreamedContent getImagenOpcionA() {
		return imagenOpcionA;
	}

	public StreamedContent getImagenOpcionB() {
		return imagenOpcionB;
	}

	public StreamedContent getImagenOpcionC() {
		return imagenOpcionC;
	}

	public StreamedContent getImagenOpcionD() {
		return imagenOpcionD;
	}

	public List<Pregunta> getListaPreguntas() {
		return listaPreguntas;
	}

	public List<Asignatura> getListAsignaturas() {
		return listAsignaturas;
	}

	public List<NivelDificultad> getListNivelDificultad() {
		return listNivelDificultad;
	}

	public List<UnidadDidactica> getListUnidadesDidacticas() {
		return listUnidadesDidacticas;
	}

	public Pregunta getPregunta() {
		return pregunta;
	}

	public int getUnidadDidactica() {
		return unidadDidactica;
	}

	public UnidadDidactica getUnidadDidacticaSeleccionada() {
		return unidadDidacticaSeleccionada;
	}

	@PostConstruct
	public void init() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		listAsignaturas = asignaturaService.obtenerLista(
				"select a from Asignatura a inner join a.docentesAsignaturas da "
						+ "inner join da.docente d where d.email=?1 and a.activo=true",
				new Object[] { email }, 0, false, new Object[0]);
		listNivelDificultad = nivelDificultadService.obtenerTodos();
	}

	public void insertar() {
		if (pregunta.getNivelDificultad().getId().compareTo("0") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el nivel de dificultad");
		} else if (pregunta.getEjeTematico().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el eje temÃ¡tico");
		} else if (pregunta.getBibliografia().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la bibliografÃ­a");
		} else if (pregunta.getJustificacion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la justificaciÃ³n");
		} else if (pregunta.getEnunciado().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el enunciado");
		} else if (pregunta.getPosiblesRespuestas().get(0).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opciÃ³n A");
		} else if (pregunta.getPosiblesRespuestas().get(1).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opciÃ³n B");
		} else if (pregunta.getPosiblesRespuestas().get(2).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opciÃ³n C");
		} else if (pregunta.getPosiblesRespuestas().get(3).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opciÃ³n D");
		} else if ((pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() != null
				&& (pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() == null
						|| pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() == null
						|| pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() == null))
				|| (pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() != null
						&& (pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() == null))
				|| (pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() != null
						&& (pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() == null))
				|| (pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() != null
						&& (pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() == null
								|| pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() == null))) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Si va a usar imagenes en las opciones de respuesta, es obligatorio hacerlo en las 4 opciones");
		} else if (pregunta.getSostiApantisi().compareTo("--") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Escoja la respuesta correcta");
		} else {
			pregunta.setUnidadDidactica(unidadDidacticaSeleccionada);
			pregunta.setActivo(true);
			pregunta.setSostiApantisi(new ShaPasswordEncoder().encodePassword(pregunta.getSostiApantisi(), null));
			preguntaService.insertar(pregunta);
			buscarReactivosTeoricos();
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("administrarPreguntas.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void limpiar() {
		pregunta = new Pregunta();
		pregunta.setNivelDificultad(new NivelDificultad());
		pregunta.setPosiblesRespuestas(new ArrayList<PosibleRespuesta>());

		PosibleRespuesta pr1 = new PosibleRespuesta();
		pr1.setPregunta(pregunta);
		pr1.setLiteral("A");
		pregunta.getPosiblesRespuestas().add(pr1);

		PosibleRespuesta pr2 = new PosibleRespuesta();
		pr2.setPregunta(pregunta);
		pr2.setLiteral("B");
		pregunta.getPosiblesRespuestas().add(pr2);

		PosibleRespuesta pr3 = new PosibleRespuesta();
		pr3.setPregunta(pregunta);
		pr3.setLiteral("C");
		pregunta.getPosiblesRespuestas().add(pr3);

		PosibleRespuesta pr4 = new PosibleRespuesta();
		pr4.setPregunta(pregunta);
		pr4.setLiteral("D");
		pregunta.getPosiblesRespuestas().add(pr4);

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("insertarReactivosTeoricos.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onRowSelect(SelectEvent event) {
		asignatura = asignaturaSeleccionada.getId();
		buscarUnidadDidactica();
	}

	public void onRowSelectUnidadDidactica(SelectEvent event) {
		unidadDidactica = unidadDidacticaSeleccionada.getId();
		buscarReactivosTeoricos();
	}

	public void quitarTagHtml() {
		List<Pregunta> pre = preguntaService.obtenerLista(
				"select p from Pregunta p inner join fetch p.posiblesRespuestas pr where p.id>=30000 and p.id<=100000 order by p.id, pr.literal",
				new Object[] {}, 0, false, new Object[0]);

		for (Pregunta pr : pre) {

			// <=26881
			// if (pr.getId() > 26890 && pr.getId() != 26890) {
			try {
				pr.setEnunciado(getText(pr.getEnunciado(), "<", ">"));
				if (pr.getEnunciado().isEmpty())
					pr.setEnunciado(pr.getId().toString());

				pr.getPosiblesRespuestas().get(0)
						.setDescripcion(getText(pr.getPosiblesRespuestas().get(0).getDescripcion(), "<", ">"));
				if (pr.getPosiblesRespuestas().get(0).getDescripcion().isEmpty())
					pr.getPosiblesRespuestas().get(0)
							.setDescripcion(pr.getId().toString() + pr.getPosiblesRespuestas().get(0).getLiteral());

				pr.getPosiblesRespuestas().get(1)
						.setDescripcion(getText(pr.getPosiblesRespuestas().get(1).getDescripcion(), "<", ">"));
				if (pr.getPosiblesRespuestas().get(1).getDescripcion().isEmpty())
					pr.getPosiblesRespuestas().get(1)
							.setDescripcion(pr.getId().toString() + pr.getPosiblesRespuestas().get(1).getLiteral());

				pr.getPosiblesRespuestas().get(2)
						.setDescripcion(getText(pr.getPosiblesRespuestas().get(2).getDescripcion(), "<", ">"));
				if (pr.getPosiblesRespuestas().get(2).getDescripcion().isEmpty())
					pr.getPosiblesRespuestas().get(2)
							.setDescripcion(pr.getId().toString() + pr.getPosiblesRespuestas().get(2).getLiteral());

				pr.getPosiblesRespuestas().get(3)
						.setDescripcion(getText(pr.getPosiblesRespuestas().get(3).getDescripcion(), "<", ">"));
				if (pr.getPosiblesRespuestas().get(3).getDescripcion().isEmpty())
					pr.getPosiblesRespuestas().get(3)
							.setDescripcion(pr.getId().toString() + pr.getPosiblesRespuestas().get(3).getLiteral());

				preguntaService.actualizar(pr);
			} catch (Exception e) {

			}
			// }
		}
	}

	public void regresarReactivosTeoricos() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("administrarPreguntasId.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setAsignatura(int asignatura) {
		this.asignatura = asignatura;
	}

	public void setAsignaturaSeleccionada(Asignatura asignaturaSeleccionada) {
		this.asignaturaSeleccionada = asignaturaSeleccionada;
	}

	public void setIdPregunta(Integer idPregunta) {
		this.idPregunta = idPregunta;
	}

	public void setImagenEnunciado(StreamedContent imagenEnunciado) {
		this.imagenEnunciado = imagenEnunciado;
	}

	public void setImagenOpcionA(StreamedContent imagenOpcionA) {
		this.imagenOpcionA = imagenOpcionA;
	}

	public void setImagenOpcionB(StreamedContent imagenOpcionB) {
		this.imagenOpcionB = imagenOpcionB;
	}

	public void setImagenOpcionC(StreamedContent imagenOpcionC) {
		this.imagenOpcionC = imagenOpcionC;
	}

	public void setImagenOpcionD(StreamedContent imagenOpcionD) {
		this.imagenOpcionD = imagenOpcionD;
	}

	public void setListaPreguntas(List<Pregunta> listaPreguntas) {
		this.listaPreguntas = listaPreguntas;
	}

	public void setListAsignaturas(List<Asignatura> listAsignaturas) {
		this.listAsignaturas = listAsignaturas;
	}

	public void setListNivelDificultad(List<NivelDificultad> listNivelDificultad) {
		this.listNivelDificultad = listNivelDificultad;
	}

	public void setListUnidadesDidacticas(List<UnidadDidactica> listUnidadesDidacticas) {
		this.listUnidadesDidacticas = listUnidadesDidacticas;
	}

	public void setPregunta(Pregunta pregunta) {
		this.pregunta = pregunta;
	}

	public void setUnidadDidactica(int unidadDidactica) {
		this.unidadDidactica = unidadDidactica;
	}

	public void setUnidadDidacticaSeleccionada(UnidadDidactica unidadDidacticaSeleccionada) {
		this.unidadDidacticaSeleccionada = unidadDidacticaSeleccionada;
	}

	public void subirImagenEnunciado(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.setImagenEnunciado(
					guardarImagen(getRutaImagen(), leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenEnunciado = new DefaultStreamedContent(leerImagen(pregunta.getImagenEnunciado()),
					"image/" + determinarExtension(nombreImagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirImagenOpcionA(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.getPosiblesRespuestas().get(0).setImagenDescripcion(
					guardarImagen(getRutaImagen(), leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenOpcionA = new DefaultStreamedContent(
					leerImagen(pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion()),
					"image/" + determinarExtension(nombreImagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirImagenOpcionB(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.getPosiblesRespuestas().get(1).setImagenDescripcion(
					guardarImagen(getRutaImagen(), leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenOpcionB = new DefaultStreamedContent(
					leerImagen(pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion()),
					"image/" + determinarExtension(nombreImagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirImagenOpcionC(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.getPosiblesRespuestas().get(2).setImagenDescripcion(
					guardarImagen(getRutaImagen(), leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenOpcionC = new DefaultStreamedContent(
					leerImagen(pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion()),
					"image/" + determinarExtension(nombreImagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirImagenOpcionD(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.getPosiblesRespuestas().get(3).setImagenDescripcion(
					guardarImagen(getRutaImagen(), leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenOpcionD = new DefaultStreamedContent(
					leerImagen(pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion()),
					"image/" + determinarExtension(nombreImagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
