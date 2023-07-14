package ec.edu.utmachala.titulacion.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.entity.NivelDificultad;
import ec.edu.utmachala.titulacion.entity.PosibleRespuesta;
import ec.edu.utmachala.titulacion.entity.Pregunta;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.UnidadDidactica;
import ec.edu.utmachala.titulacion.entityAux.CarreraMallaProcesoAux;
import ec.edu.utmachala.titulacion.service.AsignaturaService;
import ec.edu.utmachala.titulacion.service.CarreraMallaProcesoAuxService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.service.NivelDificultadService;
import ec.edu.utmachala.titulacion.service.PreguntaService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.UnidadDidacticaService;
import ec.edu.utmachala.titulacion.utility.Utils;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;

@Controller
@Scope("session")
public class AdministrarPreguntasBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private AsignaturaService asignaturaService;

	@Autowired
	private UnidadDidacticaService unidadDidacticaService;

	@Autowired
	private PreguntaService preguntaService;

	@Autowired
	private NivelDificultadService nivelDificultadService;

	@Autowired
	private CarreraMallaProcesoAuxService carreraMallaProcesoAuxService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	@Autowired
	private ProcesoService procesoService;

	private List<Asignatura> listAsignaturas;
	private int asignatura;
	private Asignatura asignaturaSeleccionada;

	private List<CarreraMallaProcesoAux> listCMPA;
	private CarreraMallaProcesoAux carreraMallaProcesoAux;

	private List<UnidadDidactica> listUnidadesDidacticas;
	private UnidadDidactica unidadDidacticaIngresar;
	private int unidadDidactica;
	private UnidadDidactica unidadDidacticaSeleccionada;

	private List<Pregunta> listPregunta;

	private Pregunta pregunta;

	private List<NivelDificultad> listNivelDificultad;

	private StreamedContent imagenEnunciado;
	private StreamedContent imagenEnunciadoPre;
	private StreamedContent imagenOpcionA;
	private StreamedContent imagenOpcionB;
	private StreamedContent imagenOpcionC;
	private StreamedContent imagenOpcionD;

	private boolean muestraImagenEnunciado = false;

	private String cadenaOriginal;

	private boolean activarPreguntas;

	@PostConstruct
	public void a() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		listCMPA = carreraMallaProcesoAuxService.obtenerPorSql(
				"select distinct a.id as id, mc.id as \"mallaCurricular\", (select string_agg(distinct ps.id||' ('||ps.observacion||')',', ')from procesos ps inner join \"carrerasMallasProcesos\" cmps on (cmps.proceso=ps.id) inner join \"mallasCurriculares\" mcs on(cmps.\"mallaCurricular\"=mcs.id) inner join asignaturas asig on (asig.\"mallaCurricular\"=mcs.id) where asig.id=a.id) as proceso, (select string_agg(distinct cs.nombre||' ('||cs.id||')',', ')from procesos ps inner join \"carrerasMallasProcesos\" cmps on (cmps.proceso=ps.id) inner join \"mallasCurriculares\" mcs on(cmps.\"mallaCurricular\"=mcs.id) inner join asignaturas asig on (asig.\"mallaCurricular\"=mcs.id) inner join	carreras cs on (cmps.carrera=cs.id)	where asig.id=a.id) as carrera, a.nombre||'-'||a.id as asignatura from exetasi.\"carrerasMallasProcesos\" cmp inner join exetasi.procesos p on (p.id=cmp.proceso) inner join carreras c on (c.id=cmp.carrera) inner join \"mallasCurriculares\" mc on (mc.id=cmp.\"mallaCurricular\") inner join asignaturas a on (a.\"mallaCurricular\"=mc.id) inner join \"docentesAsignaturas\" da on (da.asignatura=a.id) inner join usuarios d on(da.docente=d.id) where d.email='"
						+ email
						+ "' and da.activo=true and (mc.id>40 or mc.id=14 or mc.id=10 or mc.id=12 or mc.id=25 or mc.id=30) and mc.id<>54 order by \"mallaCurricular\";",
				CarreraMallaProcesoAux.class);
		unidadDidacticaIngresar = new UnidadDidactica();
		activarPreguntas = false;
	}

	public void actualizar() {
		// quitarTagHtml();|
		// if (pregunta.getNivelDificultad().getId().compareTo("0") == 0) {
		// UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese
		// el nivel de dificultad");
		// } else

		NivelDificultad nd = nivelDificultadService.obtenerObjeto("select nd from NivelDificultad nd where nd.id='ME'",
				new Object[] {}, false, new Object[] {});

		System.out.println("Nivel de dificultad encontrado: " + nd.getNombre() + ", con el id: " + nd.getId());

		pregunta.setNivelDificultad(nd);

		if (pregunta.getEjeTematico().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el eje temático");
		} else if (pregunta.getBibliografia().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la bibliografía");
		} else if (pregunta.getJustificacion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la justificación");
		} else if (pregunta.getEnunciado().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el enunciado");
		} else if (pregunta.getPosiblesRespuestas().get(0).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opción A");
		} else if (pregunta.getPosiblesRespuestas().get(1).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opción B");
		} else if (pregunta.getPosiblesRespuestas().get(2).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opción C");
		} else if (pregunta.getPosiblesRespuestas().get(3).getDescripcion().isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opción D");
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
				System.out.println("Error en insertar pregunta: ");
				FacesContext.getCurrentInstance().getExternalContext().redirect("administrarPreguntas.jsf");
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Ha editado la pregunta de forma satisfactoria.");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error en insertar pregunta: ");
			}
		}
	}

	public void actualizarUD() {
		unidadDidacticaService.actualizar(unidadDidacticaSeleccionada);
		UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_ERROR,
				"Se ha actualizado la unidad didáctica de forma correcta.");
		unidadDidactica = unidadDidacticaSeleccionada.getId();
		buscarReactivosTeoricos();
	}

	public void buscarReactivosTeoricos() {
		listPregunta = preguntaService.obtenerLista(
				"select distinct p from Pregunta p inner join fetch p.posiblesRespuestas pr inner join p.unidadDidactica ud where ud.id=?1 order by p.id, pr.literal",
				new Object[] { unidadDidactica }, 0, false, new Object[0]);
	}

	public void buscarUnidadDidactica() {
		unidadDidactica = 0;
		listUnidadesDidacticas = unidadDidacticaService.obtenerLista(
				"select ud from UnidadDidactica ud inner join ud.asignatura a where a.id=?1 and ud.activo=true order by ud.nombre",
				new Object[] { asignatura }, 0, false, new Object[0]);
		if (listUnidadesDidacticas.size() == 1) {
			unidadDidactica = listUnidadesDidacticas.get(0).getId();
		}
	}

	public void cargarEditar() {
		if (pregunta.getImagenEnunciado() != null) {
			imagenEnunciado = new DefaultStreamedContent(UtilsArchivos.leerImagen(pregunta.getImagenEnunciado()),
					"image/" + UtilsArchivos.determinarExtension(pregunta.getImagenEnunciado()));
			imagenEnunciadoPre = new DefaultStreamedContent(UtilsArchivos.leerImagen(pregunta.getImagenEnunciado()),
					"image/" + UtilsArchivos.determinarExtension(pregunta.getImagenEnunciado()));
			muestraImagenEnunciado = true;
		} else
			muestraImagenEnunciado = false;

		if (pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion() != null)
			imagenOpcionA = new DefaultStreamedContent(
					UtilsArchivos.leerImagen(pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion()),
					"image/" + UtilsArchivos
							.determinarExtension(pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion()));

		if (pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion() != null)
			imagenOpcionB = new DefaultStreamedContent(
					UtilsArchivos.leerImagen(pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion()),
					"image/" + UtilsArchivos
							.determinarExtension(pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion()));

		if (pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion() != null)
			imagenOpcionC = new DefaultStreamedContent(
					UtilsArchivos.leerImagen(pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion()),
					"image/" + UtilsArchivos
							.determinarExtension(pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion()));

		if (pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion() != null)
			imagenOpcionD = new DefaultStreamedContent(
					UtilsArchivos.leerImagen(pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion()),
					"image/" + UtilsArchivos
							.determinarExtension(pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion()));

		if (pregunta.getSostiApantisi().compareTo(new ShaPasswordEncoder().encodePassword("A", null)) == 0)
			pregunta.setSostiApantisi("A");
		else if (pregunta.getSostiApantisi().compareTo(new ShaPasswordEncoder().encodePassword("B", null)) == 0)
			pregunta.setSostiApantisi("B");
		else if (pregunta.getSostiApantisi().compareTo(new ShaPasswordEncoder().encodePassword("C", null)) == 0)
			pregunta.setSostiApantisi("C");
		else if (pregunta.getSostiApantisi().compareTo(new ShaPasswordEncoder().encodePassword("D", null)) == 0)
			pregunta.setSostiApantisi("D");

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("editarReactivosTeoricos.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 13 ENTER
	// 10 AVANCE DE CARRO
	// 32 ESPACIO
	public void cargarVisualizar() {
		cadenaOriginal = pregunta.getEnunciado();
		String cadena = cadenaOriginal;

		for (int i = 0; i < cadena.length(); i++) {
			char cc = cadena.charAt(i);
			if (cc == 10) {
				String cadenaInsertar = "<br>";
				String principioCadena = cadenaOriginal.substring(0, i);
				String finCadena = cadenaOriginal.substring(i);

				cadenaOriginal = principioCadena + cadenaInsertar + finCadena;
				cadena = cadenaOriginal;
				i = i + 5;
				// break;
			}
		}
		String espaciado = "<span class=\"Apple-tab-span\" style=\"white-space:pre\">	</span>";
		cadenaOriginal = cadenaOriginal.replace("<tab>", espaciado);

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("examenVisualizar.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void eliminarImagenEnunciado() {
		pregunta.setImagenEnunciado(null);
		imagenEnunciado = null;
		imagenEnunciadoPre = null;
		muestraImagenEnunciado = false;
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

	public void eliminarUD() {
		if (listPregunta != null && listPregunta.size() != 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede eliminar la unidad didáctica porque cuenta con reactivos teóricos ingresados.");
		} else {
			unidadDidacticaService.eliminar(unidadDidacticaSeleccionada);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Unidad didáctica eliminada satisfactoriamente.");
			buscarUnidadDidactica();
		}
	}

	public int getAsignatura() {
		return asignatura;
	}

	public Asignatura getAsignaturaSeleccionada() {
		return asignaturaSeleccionada;
	}

	public String getCadenaOriginal() {
		return cadenaOriginal;
	}

	public CarreraMallaProcesoAux getCarreraMallaProcesoAux() {
		return carreraMallaProcesoAux;
	}

	public StreamedContent getImagenEnunciado() {
		return imagenEnunciado;
	}

	public StreamedContent getImagenEnunciadoPre() {
		return imagenEnunciadoPre;
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

	public List<Asignatura> getListAsignaturas() {
		return listAsignaturas;
	}

	public List<CarreraMallaProcesoAux> getListCMPA() {
		return listCMPA;
	}

	public List<NivelDificultad> getListNivelDificultad() {
		return listNivelDificultad;
	}

	public List<Pregunta> getListPregunta() {
		return listPregunta;
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

	public UnidadDidactica getUnidadDidacticaIngresar() {
		return unidadDidacticaIngresar;
	}

	public UnidadDidactica getUnidadDidacticaSeleccionada() {
		return unidadDidacticaSeleccionada;
	}

	public void insertar() {
		try {
			// quitarTagHtml();
			if (pregunta.getEjeTematico().isEmpty()) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el eje temático");
			} else if (pregunta.getBibliografia().isEmpty()) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la bibliografía");
			} else if (pregunta.getJustificacion().isEmpty()) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la justificación");
			} else if (pregunta.getEnunciado().isEmpty()) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el enunciado");
			} else if (pregunta.getPosiblesRespuestas().get(0).getDescripcion().isEmpty()) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opción A");
			} else if (pregunta.getPosiblesRespuestas().get(1).getDescripcion().isEmpty()) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opción B");
			} else if (pregunta.getPosiblesRespuestas().get(2).getDescripcion().isEmpty()) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opción C");
			} else if (pregunta.getPosiblesRespuestas().get(3).getDescripcion().isEmpty()) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la opción D");
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
			} else if (pregunta.getPosiblesRespuestas().get(0).getDescripcion()
					.compareTo(pregunta.getPosiblesRespuestas().get(1).getDescripcion()) == 0
					|| pregunta.getPosiblesRespuestas().get(0).getDescripcion()
							.compareTo(pregunta.getPosiblesRespuestas().get(2).getDescripcion()) == 0
					|| pregunta.getPosiblesRespuestas().get(0).getDescripcion()
							.compareTo(pregunta.getPosiblesRespuestas().get(3).getDescripcion()) == 0
					|| pregunta.getPosiblesRespuestas().get(1).getDescripcion()
							.compareTo(pregunta.getPosiblesRespuestas().get(2).getDescripcion()) == 0
					|| pregunta.getPosiblesRespuestas().get(1).getDescripcion()
							.compareTo(pregunta.getPosiblesRespuestas().get(3).getDescripcion()) == 0
					|| pregunta.getPosiblesRespuestas().get(2).getDescripcion()
							.compareTo(pregunta.getPosiblesRespuestas().get(3).getDescripcion()) == 0) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Existen respuestas que se repiten, corrija por favor.");
			} else {
				NivelDificultad nd = nivelDificultadService.obtenerObjeto(
						"select nd from NivelDificultad nd where nd.id='ME'", new Object[] {}, false, new Object[] {});
				pregunta.setNivelDificultad(nd);
				pregunta.setUnidadDidactica(unidadDidacticaSeleccionada);
				pregunta.setActivo(true);
				pregunta.setSostiApantisi(new ShaPasswordEncoder().encodePassword(pregunta.getSostiApantisi(), null));
				preguntaService.insertar(pregunta);
				buscarReactivosTeoricos();
				try {
					FacesContext.getCurrentInstance().getExternalContext().redirect("administrarPreguntas.jsf");
				} catch (IOException e) {
					System.out.println("Error en insertar pregunta: ");
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.out.println("Error en insertar pregunta: ");
			e.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void insertarUD() {
		unidadDidacticaIngresar.setAsignatura(asignaturaService.obtenerObjeto(
				"select a from Asignatura a where a.id=?1", new Object[] { asignatura }, false, new Object[] {}));
		unidadDidacticaIngresar.setActivo(true);
		unidadDidacticaService.insertar(unidadDidacticaIngresar);
		unidadDidacticaIngresar = new UnidadDidactica();
		UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_ERROR,
				"Se ha ingresado la unidad didáctica de forma correcta.");
		buscarUnidadDidactica();
	}

	public boolean isActivarPreguntas() {
		return activarPreguntas;
	}

	public boolean isMuestraImagenEnunciado() {
		return muestraImagenEnunciado;
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
		asignatura = Integer.parseInt(carreraMallaProcesoAux.getAsignatura()
				.split("-")[carreraMallaProcesoAux.getAsignatura().split("-").length - 1]);
		asignaturaSeleccionada = asignaturaService.obtenerObjeto(
				"select a from Asignatura a where a.id=?1 order by a.nombre", new Object[] { asignatura }, false,
				new Object[] {});
		List<Proceso> procesos = procesoService.obtenerLista(
				"select distinct p from Proceso p inner join p.carreraMallaProceso cmp inner join cmp.mallaCurricular mc "
						+ "inner join mc.asignaturas a where a.id=?1 order by p.fechaInicio desc",
				new Object[] { asignatura }, 0, false, new Object[] {});

		Integer carrera = Integer.parseInt(carreraMallaProcesoAux.getCarrera().split("\\(")[1].split("\\)")[0]);

		if (procesos != null && procesos.size() >= 1) {

			List<FechaProceso> listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='EC' and fp.fase='REATEO' and fp.activo='true' order by fp.id asc",
					new Object[] { procesos.get(0).getId() }, 0, false, new Object[0]);

			if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
				if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
						&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
					activarPreguntas = true;
				} else {
					listadoFechasProcesos = fechaProcesoService.obtenerLista(
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='REATEO' and fp.activo='true' order by fp.id asc",
							new Object[] { procesos.get(0).getId(), carrera }, 0, false, new Object[0]);

					if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
						if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
								&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
							activarPreguntas = true;
						} else {
							activarPreguntas = false;
						}

					} else {
						activarPreguntas = false;
					}
				}

			} else
				activarPreguntas = false;
		}
		if (listPregunta != null)
			listPregunta.clear();
		buscarUnidadDidactica();
	}

	public void onRowSelectUnidadDidactica(SelectEvent event) {
		unidadDidactica = unidadDidacticaSeleccionada.getId();
		buscarReactivosTeoricos();
	}

	public void quitarTagHtml() {
		pregunta.setEnunciado(Utils.getText(pregunta.getPosiblesRespuestas().get(0).getDescripcion(), "<", ">"));
		pregunta.getPosiblesRespuestas().get(0)
				.setDescripcion(Utils.getText(pregunta.getPosiblesRespuestas().get(0).getDescripcion(), "<", ">"));
		pregunta.getPosiblesRespuestas().get(1)
				.setDescripcion(Utils.getText(pregunta.getPosiblesRespuestas().get(1).getDescripcion(), "<", ">"));
		pregunta.getPosiblesRespuestas().get(2)
				.setDescripcion(Utils.getText(pregunta.getPosiblesRespuestas().get(2).getDescripcion(), "<", ">"));
		pregunta.getPosiblesRespuestas().get(3)
				.setDescripcion(Utils.getText(pregunta.getPosiblesRespuestas().get(3).getDescripcion(), "<", ">"));
	}

	public void regresarReactivosTeoricos() {
		try {
			buscarReactivosTeoricos();
			FacesContext.getCurrentInstance().getExternalContext().redirect("administrarPreguntas.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setActivarPreguntas(boolean activarPreguntas) {
		this.activarPreguntas = activarPreguntas;
	}

	public void setAsignatura(int asignatura) {
		this.asignatura = asignatura;
	}

	public void setAsignaturaSeleccionada(Asignatura asignaturaSeleccionada) {
		this.asignaturaSeleccionada = asignaturaSeleccionada;
	}

	public void setCadenaOriginal(String cadenaOriginal) {
		this.cadenaOriginal = cadenaOriginal;
	}

	public void setCarreraMallaProcesoAux(CarreraMallaProcesoAux carreraMallaProcesoAux) {
		this.carreraMallaProcesoAux = carreraMallaProcesoAux;
	}

	public void setImagenEnunciado(StreamedContent imagenEnunciado) {
		this.imagenEnunciado = imagenEnunciado;
	}

	public void setImagenEnunciadoPre(StreamedContent imagenEnunciadoPre) {
		this.imagenEnunciadoPre = imagenEnunciadoPre;
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

	public void setListAsignaturas(List<Asignatura> listAsignaturas) {
		this.listAsignaturas = listAsignaturas;
	}

	public void setListCMPA(List<CarreraMallaProcesoAux> listCMPA) {
		this.listCMPA = listCMPA;
	}

	public void setListNivelDificultad(List<NivelDificultad> listNivelDificultad) {
		this.listNivelDificultad = listNivelDificultad;
	}

	public void setListPregunta(List<Pregunta> listPregunta) {
		this.listPregunta = listPregunta;
	}

	public void setListUnidadesDidacticas(List<UnidadDidactica> listUnidadesDidacticas) {
		this.listUnidadesDidacticas = listUnidadesDidacticas;
	}

	public void setMuestraImagenEnunciado(boolean muestraImagenEnunciado) {
		this.muestraImagenEnunciado = muestraImagenEnunciado;
	}

	public void setPregunta(Pregunta pregunta) {
		this.pregunta = pregunta;
	}

	public void setUnidadDidactica(int unidadDidactica) {
		this.unidadDidactica = unidadDidactica;
	}

	public void setUnidadDidacticaIngresar(UnidadDidactica unidadDidacticaIngresar) {
		this.unidadDidacticaIngresar = unidadDidacticaIngresar;
	}

	public void setUnidadDidacticaSeleccionada(UnidadDidactica unidadDidacticaSeleccionada) {
		this.unidadDidacticaSeleccionada = unidadDidacticaSeleccionada;
	}

	public void subirImagenEnunciado(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.setImagenEnunciado(UtilsArchivos.guardarImagen(UtilsArchivos.getRutaImagen(),
					UtilsArchivos.leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenEnunciado = new DefaultStreamedContent(UtilsArchivos.leerImagen(pregunta.getImagenEnunciado()),
					"image/" + UtilsArchivos.determinarExtension(nombreImagen));
			imagenEnunciadoPre = new DefaultStreamedContent(UtilsArchivos.leerImagen(pregunta.getImagenEnunciado()),
					"image/" + UtilsArchivos.determinarExtension(nombreImagen));
			muestraImagenEnunciado = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirImagenOpcionA(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.getPosiblesRespuestas().get(0)
					.setImagenDescripcion(UtilsArchivos.guardarImagen(UtilsArchivos.getRutaImagen(),
							UtilsArchivos.leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenOpcionA = new DefaultStreamedContent(
					UtilsArchivos.leerImagen(pregunta.getPosiblesRespuestas().get(0).getImagenDescripcion()),
					"image/" + UtilsArchivos.determinarExtension(nombreImagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirImagenOpcionB(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.getPosiblesRespuestas().get(1)
					.setImagenDescripcion(UtilsArchivos.guardarImagen(UtilsArchivos.getRutaImagen(),
							UtilsArchivos.leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenOpcionB = new DefaultStreamedContent(
					UtilsArchivos.leerImagen(pregunta.getPosiblesRespuestas().get(1).getImagenDescripcion()),
					"image/" + UtilsArchivos.determinarExtension(nombreImagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirImagenOpcionC(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.getPosiblesRespuestas().get(2)
					.setImagenDescripcion(UtilsArchivos.guardarImagen(UtilsArchivos.getRutaImagen(),
							UtilsArchivos.leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenOpcionC = new DefaultStreamedContent(
					UtilsArchivos.leerImagen(pregunta.getPosiblesRespuestas().get(2).getImagenDescripcion()),
					"image/" + UtilsArchivos.determinarExtension(nombreImagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirImagenOpcionD(FileUploadEvent event) {
		try {
			String nombreImagen = asignaturaSeleccionada.getId() + "-" + unidadDidacticaSeleccionada.getId() + "-"
					+ event.getFile().getFileName();
			pregunta.getPosiblesRespuestas().get(3)
					.setImagenDescripcion(UtilsArchivos.guardarImagen(UtilsArchivos.getRutaImagen(),
							UtilsArchivos.leerImagen(event.getFile().getInputstream(), null), nombreImagen));
			imagenOpcionD = new DefaultStreamedContent(
					UtilsArchivos.leerImagen(pregunta.getPosiblesRespuestas().get(3).getImagenDescripcion()),
					"image/" + UtilsArchivos.determinarExtension(nombreImagen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
