package ec.edu.utmachala.titulacion.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.gdata.client.GoogleService;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Examen;
import ec.edu.utmachala.titulacion.entity.NivelDificultad;
import ec.edu.utmachala.titulacion.entity.Permiso;
import ec.edu.utmachala.titulacion.entity.PosibleRespuesta;
import ec.edu.utmachala.titulacion.entity.Pregunta;
import ec.edu.utmachala.titulacion.entity.PreguntaExamen;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Rol;
import ec.edu.utmachala.titulacion.entity.UnidadDidactica;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPTService;
import ec.edu.utmachala.titulacion.service.ExamenService;
import ec.edu.utmachala.titulacion.service.GrupoService;
import ec.edu.utmachala.titulacion.service.NivelDificultadService;
import ec.edu.utmachala.titulacion.service.PreguntaExamenService;
import ec.edu.utmachala.titulacion.service.PreguntaService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.RolService;
import ec.edu.utmachala.titulacion.service.TipoExamenService;
import ec.edu.utmachala.titulacion.service.UnidadDidacticaService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;

@Controller
@Scope("session")
public class GoogleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	EstudiantesExamenComplexivoPPService temaPracticoService;

	@Autowired
	UsuarioService usuarioService;

	@Autowired
	UnidadDidacticaService unidadDidacticaService;

	@Autowired
	NivelDificultadService nivelDificultadService;

	@Autowired
	PreguntaService preguntaService;

	@Autowired
	CarreraService carreraService;

	@Autowired
	ProcesoService procesoService;

	@Autowired
	RolService rolService;

	@Autowired
	ExamenService examenService;

	@Autowired
	TipoExamenService tipoExamenService;

	@Autowired
	GrupoService grupoService;

	@Autowired
	EstudiantesExamenComplexivoPTService estudiantesExamenComplexivoPTService;

	@Autowired
	EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	PreguntaExamenService preguntaExamenService;

	GoogleService googleService = null;

	@PostConstruct
	public void a() throws Exception {
		String APPLICATION_NAME = "CaneBrothers-SpreadsheetSample/3.0";
		List<String> SCOPES = Arrays.asList("https://spreadsheets.google.com/feeds");
		File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/oauth2_3_sample");
		String CLIENT_SECRETS = "/client_secrets.json";
		FileDataStoreFactory dataStoreFactory;
		HttpTransport httpTransport;
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		GoogleClientSecrets clientSecrets;

		clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(GoogleBean.class.getResourceAsStream(CLIENT_SECRETS)));

		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
				|| clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
					+ "into client_secrets.json");
			System.exit(1);
		}

		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(dataStoreFactory).build();

		AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver());

		Credential credential = app.authorize("user");
		googleService = new SpreadsheetService(APPLICATION_NAME);
		googleService.setOAuth2Credentials(credential);

	}

	public void anadirPreguntasEstudiante() {
		EstudianteExamenComplexivoPT eecpt = estudiantesExamenComplexivoPTService.obtenerObjeto(
				"select ept from EstudianteExamenComplexivoPT ept inner join fetch ept.examenes ex inner join ex.tipoExamen te inner join ept.proceso p "
						+ "inner join ept.carrera c where ept.id=?1 and te.id=?2",
				new Object[] { 1156, "GR" }, false, new Object[] {});

		List<PreguntaExamen> preexaEPT = preguntaExamenService.obtenerLista(
				"select preexa from PreguntaExamen preexa inner join preexa.examen ex inner join preexa.pregunta pre where ex.id=?1",
				new Object[] { eecpt.getExamenes().get(0).getId() }, 0, false, new Object[] {});

		Proceso proceso = eecpt.getProceso();
		Integer carrera = eecpt.getCarrera().getId();
		Examen examenSeleccionado = eecpt.getExamenes().get(0);
		System.out.println("Proceso: " + proceso.getId());
		System.out.println("Carrera: " + carrera);

		System.out.println("======================");

		List<Pregunta> preguntas = new ArrayList<Pregunta>();
		List<Pregunta> preguntasHumano = new ArrayList<Pregunta>();
		List<Pregunta> preguntasBasico = new ArrayList<Pregunta>();
		List<Pregunta> preguntasProfesional = new ArrayList<Pregunta>();

		preguntasHumano = preguntaService.obtener(carrera, "HUMANO", proceso.getPreguntaHumano());
		if (preguntasHumano.size() != proceso.getPreguntaHumano()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No existen suficientes preguntas del eje humano");
		} else {
			preguntasBasico = preguntaService.obtener(carrera, "BÁSICO", proceso.getPreguntaBasico());
			if (preguntasBasico.size() != proceso.getPreguntaBasico()) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No existen suficientes preguntas del eje básico");
			} else {
				preguntasProfesional = preguntaService.obtener(carrera, "PROFESIONAL",
						proceso.getPreguntaProfesional());
				if (preguntasProfesional.size() != proceso.getPreguntaProfesional()) {
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"No existen suficientes preguntas del eje profesional");
				} else {
					preguntas.addAll(preguntasHumano);
					preguntas.addAll(preguntasBasico);
					preguntas.addAll(preguntasProfesional);
				}
			}
		}

		if (preguntas.size() == (proceso.getPreguntaBasico() + proceso.getPreguntaHumano()
				+ proceso.getPreguntaProfesional())) {
			PreguntaExamen pe = null;
			List<PreguntaExamen> preguntasExamenes = new ArrayList<PreguntaExamen>();
			for (Pregunta p : preguntas) {
				pe = new PreguntaExamen();
				pe.setExamen(examenSeleccionado);
				pe.setPregunta(p);
				preguntasExamenes.add(pe);
			}
			examenSeleccionado.setPreguntasExamenes(preguntasExamenes);
			examenService.actualizar(examenSeleccionado);
		}

		preexaEPT = preguntaExamenService.obtenerLista(
				"select preexa from PreguntaExamen preexa inner join preexa.examen ex inner join preexa.pregunta pre where ex.id=?1",
				new Object[] { eecpt.getExamenes().get(0).getId() }, 0, false, new Object[] {});

		int faltantes = 20;

		for (int j = 0; j < preexaEPT.size(); j++) {
			if (j < faltantes) {
				PreguntaExamen pe = preexaEPT.get(j);
				Pregunta pre = pe.getPregunta();
				pe.setRespuesta(pre.getSostiApantisi());
				preguntaExamenService.actualizar(pe);

			} else {
				PreguntaExamen pe = preexaEPT.get(j);
				Pregunta pre = preguntaService.obtenerObjeto("select pre from Pregunta pre where pre.id=?1",
						new Object[] { pe.getPregunta().getId() }, false, new Object[] {});
				pe.setRespuesta(
						(pre.getSostiApantisi().compareTo(new ShaPasswordEncoder().encodePassword("A", null)) == 0
								? new ShaPasswordEncoder().encodePassword("B", null)
								: (pre.getSostiApantisi()
										.compareTo(new ShaPasswordEncoder().encodePassword("B", null)) == 0
												? new ShaPasswordEncoder().encodePassword("C", null)
												: (pre.getSostiApantisi().compareTo(
														new ShaPasswordEncoder().encodePassword("C", null)) == 0
																? new ShaPasswordEncoder().encodePassword("D", null)
																: (pre.getSostiApantisi()
																		.compareTo(new ShaPasswordEncoder()
																				.encodePassword("D", null)) == 0
																						? new ShaPasswordEncoder()
																								.encodePassword("A",
																										null)
																						: "")))));
				preguntaExamenService.actualizar(pe);
			}

		}

	}

	public void arreglarNotas() {
		SpreadsheetEntry spreadsheet;
		try {
			spreadsheet = googleService.getEntry(new URL(
					"https://spreadsheets.google.com/feeds/spreadsheets/private/full/160i8is6zkX6WT3LrbD9QTsLh6C2R5U_44yhF5sZE1C4"),
					SpreadsheetEntry.class);
			URL listFeedUrl = spreadsheet.getWorksheets().get(2).getListFeedUrl();
			ListFeed feed = googleService.getFeed(listFeedUrl, ListFeed.class);
			List<ListEntry> e = feed.getEntries();

			int contadorDeCambios = 0;

			for (int i = 0; i < e.size(); i++) {

				int ept = Integer.parseInt(e.get(i).getCustomElements().getValue("EPTID").trim());

				System.out.println("ept: " + ept);

				Integer falta = (e.get(i).getCustomElements().getValue("FALTA") == null
						|| e.get(i).getCustomElements().getValue("FALTA").compareTo("#N/A") == 0 ? null
								: Integer.parseInt(e.get(i).getCustomElements().getValue("FALTA")));

				System.out.println("falta: " + falta);

				System.out.println("tipo_examen: " + e.get(i).getCustomElements().getValue("TIPOEXAMEN"));

				String tipoExamen = (e.get(i).getCustomElements().getValue("TIPOEXAMEN").compareTo("ORDINARIO") == 0
						? "OR"
						: "GR");

				// Integer teoricaExamen =
				// (Integer.parseInt(e.get(i).getCustomElements().getValue("TEORICAEXAMEN")));

				System.out.println("Id del estudiante examen complexivo pt: " + ept);

				System.out.println("Valor de la columna de falta: " + falta);

				if (falta != null) {
					EstudianteExamenComplexivoPT eecpt = estudiantesExamenComplexivoPTService.obtenerObjeto(
							"select ept from EstudianteExamenComplexivoPT ept inner join fetch ept.examenes ex inner join ex.tipoExamen te where ept.id=?1 and te.id=?2",
							new Object[] { ept, tipoExamen }, false, new Object[] {});

					List<PreguntaExamen> preexaEPT = preguntaExamenService.obtenerLista(
							"select preexa from PreguntaExamen preexa inner join preexa.examen ex inner join preexa.pregunta pre where ex.id=?1",
							new Object[] { eecpt.getExamenes().get(0).getId() }, 0, false, new Object[] {});

					int contestadasCorrectas = 0;
					for (PreguntaExamen pe : preexaEPT) {
						// System.out.println("id de la pregunta: " +
						// pe.getPregunta().getId());
						Pregunta pre = preguntaService.obtenerObjeto("select pre from Pregunta pre where pre.id=?1",
								new Object[] { pe.getPregunta().getId() }, false, new Object[] {});
						if (pe.getRespuesta() != null)
							if (pe.getRespuesta().compareTo(pre.getSostiApantisi()) == 0)
								contestadasCorrectas++;
					}

					System.out.println("Preguntas correctas: " + contestadasCorrectas);

					if (falta >= 1) {

						preexaEPT = preguntaExamenService.obtenerLista(
								"select preexa from PreguntaExamen preexa inner join preexa.examen ex inner join preexa.pregunta pre where ex.id=?1 and preexa.respuesta <> pre.sostiApantisi",
								new Object[] { eecpt.getExamenes().get(0).getId() }, 0, false, new Object[] {});

						System.out.println("Preguntas incorrectas: " + preexaEPT.size());

						int faltantes = falta;
						for (int j = 0; j < faltantes; j++) {
							PreguntaExamen pe = preexaEPT.get(j);
							Pregunta pre = pe.getPregunta();
							pe.setRespuesta(pre.getSostiApantisi());
							preguntaExamenService.actualizar(pe);
						}
						contadorDeCambios++;
						// break;
					} else {

						preexaEPT = preguntaExamenService.obtenerLista(
								"select preexa from PreguntaExamen preexa inner join preexa.examen ex inner join preexa.pregunta pre where ex.id=?1 and preexa.respuesta=pre.sostiApantisi",
								new Object[] { eecpt.getExamenes().get(0).getId() }, 0, false, new Object[] {});

						System.out.println("Preguntas correctas" + preexaEPT.size());

						int faltantes = falta * (-1);
						for (int j = 0; j < faltantes; j++) {
							PreguntaExamen pe = preexaEPT.get(j);
							pe.setRespuesta((pe.getRespuesta()
									.compareTo(new ShaPasswordEncoder().encodePassword("A", null)) == 0
											? new ShaPasswordEncoder().encodePassword("B", null)
											: (pe.getRespuesta()
													.compareTo(new ShaPasswordEncoder().encodePassword("B", null)) == 0
															? new ShaPasswordEncoder().encodePassword("C", null)
															: (pe.getRespuesta().compareTo(new ShaPasswordEncoder()
																	.encodePassword("C", null)) == 0
																			? new ShaPasswordEncoder()
																					.encodePassword("D", null)
																			: (pe.getRespuesta()
																					.compareTo(new ShaPasswordEncoder()
																							.encodePassword("D",
																									null)) == 0
																											? new ShaPasswordEncoder()
																													.encodePassword(
																															"A",
																															null)
																											: "")))));
							preguntaExamenService.actualizar(pe);
						}
						contadorDeCambios++;
					}

					// e.get(i).getCustomElements().setValueLocal("FALTA", "0");
					// e.get(i).update();

				}

			}

			System.out.println("seguimos, número de cambios: " + contadorDeCambios);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void cambiarRol() {
		List<EstudianteTrabajoTitulacion> ett = new ArrayList<EstudianteTrabajoTitulacion>();
		ett = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett order by ett.id", new Object[] {}, 0, false,
				new Object[] {});
		Rol r = rolService.obtenerPorNombre("ESTT");
		int sw;
		for (EstudianteTrabajoTitulacion e : ett) {
			System.out.println("e: " + e.getId());
			sw = 1;
			Usuario usuario = usuarioService.obtenerObjeto(
					"select u from Usuario u left join fetch u.permisos p where u.id=?1",
					new Object[] { e.getEstudiante().getId() }, false, new Object[] {});
			for (Permiso p : usuario.getPermisos()) {
				if (p.getRol().getId().compareTo("ESTT") == 0) {
					sw = 0;
					break;
				}
			}
			if (sw == 1) {
				System.out.println("id: " + usuario.getId());
				Permiso per = new Permiso();
				per.setRol(r);
				per.setUsuario(usuario);
				usuario.setPermisos(new ArrayList<Permiso>());
				usuario.getPermisos().add(per);
				usuarioService.actualizar(usuario);
			}
		}
	}

	public void comprobarEstudiantes() {
		SpreadsheetEntry spreadsheet;
		try {
			spreadsheet = googleService.getEntry(new URL(
					"https://spreadsheets.google.com/feeds/spreadsheets/private/full/160i8is6zkX6WT3LrbD9QTsLh6C2R5U_44yhF5sZE1C4"),
					SpreadsheetEntry.class);
			URL listFeedUrl = spreadsheet.getWorksheets().get(0).getListFeedUrl();
			ListFeed feed = googleService.getFeed(listFeedUrl, ListFeed.class);
			List<ListEntry> e = feed.getEntries();

			int cont = 0;
			int cone = 0;
			int conn = 0;

			for (int i = 0; i < e.size(); i++) {

				Usuario usuario = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { e.get(i).getCustomElements().getValue("DOCUMENTO").trim() }, false,
						new Object[] {});

				if (usuario == null) {
					System.out.println("No existe");
					System.out.println(e.get(i).getCustomElements().getValue("DOCUMENTO").trim());
					conn++;
				} else {
					// EstudianteExamenComplexivoPT est =
					// estudiantesExamenComplexivoPTService.obtenerObjeto(
					// "select e from EstudianteExamenComplexivoPT e inner join
					// e.estudiante u inner join e.carrera c "
					// + "where u.id=?1 and
					// TRANSLATE(c.nombre,'ÁÉÍÓÚ','AEIOU')=?2",
					// new Object[] {
					// e.get(i).getCustomElements().getValue("DOCUMENTO").trim(),
					// e.get(i).getCustomElements().getValue("CARRERA").trim()
					// },
					// false, new Object[] {});

					EstudianteExamenComplexivoPT est = estudiantesExamenComplexivoPTService.obtenerObjeto(
							"select e from EstudianteExamenComplexivoPT e inner join e.estudiante u where u.id=?1",
							new Object[] { e.get(i).getCustomElements().getValue("DOCUMENTO").trim() }, false,
							new Object[] {});
					estudiantesExamenComplexivoPTService.actualizar(est);
					System.out.println("Si existe");
					System.out.println(e.get(i).getCustomElements().getValue("DOCUMENTO").trim());
					cone++;
				}
				cont++;
			}
			System.out.println(conn);
			System.out.println(cone);
			System.out.println(cont);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void crearPreguntasExamenes() {

		SpreadsheetEntry spreadsheet;
		try {
			spreadsheet = googleService.getEntry(new URL(
					"https://spreadsheets.google.com/feeds/spreadsheets/private/full/160i8is6zkX6WT3LrbD9QTsLh6C2R5U_44yhF5sZE1C4"),
					SpreadsheetEntry.class);
			URL listFeedUrl = spreadsheet.getWorksheets().get(2).getListFeedUrl();
			ListFeed feed = googleService.getFeed(listFeedUrl, ListFeed.class);
			List<ListEntry> e = feed.getEntries();

			int contadorDeCambios = 0;

			for (int i = 0; i < e.size(); i++) {

				int ept = Integer.parseInt(e.get(i).getCustomElements().getValue("EPTID").trim());

				System.out.println("ept: " + ept);

				Integer falta = (e.get(i).getCustomElements().getValue("FALTA") == null
						|| e.get(i).getCustomElements().getValue("FALTA").compareTo("#N/A") == 0 ? null
								: Integer.parseInt(e.get(i).getCustomElements().getValue("FALTA")));

				System.out.println("falta: " + falta);

				System.out.println("tipo_examen: " + e.get(i).getCustomElements().getValue("TIPOEXAMEN"));

				String tipoExamen = (e.get(i).getCustomElements().getValue("TIPOEXAMEN").compareTo("ORDINARIO") == 0
						? "OR"
						: "GR");

				Integer teoricaExamen = (Integer.parseInt(e.get(i).getCustomElements().getValue("TEORICAEXAMEN")));

				System.out.println("Id del estudiante examen complexivo pt: " + ept);

				System.out.println("Valor de la columna de falta: " + falta);

				EstudianteExamenComplexivoPT eecpt = estudiantesExamenComplexivoPTService.obtenerObjeto(
						"select ept from EstudianteExamenComplexivoPT ept inner join fetch ept.examenes ex inner join ex.tipoExamen te inner join ept.proceso p "
								+ "inner join ept.carrera c where ept.id=?1 and te.id=?2",
						new Object[] { ept, tipoExamen }, false, new Object[] {});

				List<PreguntaExamen> preexaEPT = preguntaExamenService.obtenerLista(
						"select preexa from PreguntaExamen preexa inner join preexa.examen ex inner join preexa.pregunta pre where ex.id=?1",
						new Object[] { eecpt.getExamenes().get(0).getId() }, 0, false, new Object[] {});

				/*
				 * Proceso proceso = eecpt.getProceso(); Integer carrera =
				 * eecpt.getCarrera().getId(); Examen examenSeleccionado =
				 * eecpt.getExamenes().get(0); System.out.println("Proceso: " +
				 * proceso.getId()); System.out.println("Carrera: " + carrera); if (preexaEPT !=
				 * null && preexaEPT.size() != 0) { System.out.println("Listado preguntas: " +
				 * preexaEPT.size()); break; }
				 * 
				 * System.out.println("======================");
				 * 
				 * List<Pregunta> preguntas = new ArrayList<Pregunta>(); List<Pregunta>
				 * preguntasHumano = new ArrayList<Pregunta>(); List<Pregunta> preguntasBasico =
				 * new ArrayList<Pregunta>(); List<Pregunta> preguntasProfesional = new
				 * ArrayList<Pregunta>();
				 * 
				 * preguntasHumano = preguntaService.obtener(carrera, "HUMANO",
				 * proceso.getPreguntaHumano()); if (preguntasHumano.size() !=
				 * proceso.getPreguntaHumano()) {
				 * UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
				 * "No existen suficientes preguntas del eje humano"); } else { preguntasBasico
				 * = preguntaService.obtener(carrera, "BÁSICO", proceso.getPreguntaBasico()); if
				 * (preguntasBasico.size() != proceso.getPreguntaBasico()) {
				 * UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
				 * "No existen suficientes preguntas del eje básico"); } else {
				 * preguntasProfesional = preguntaService.obtener(carrera, "PROFESIONAL",
				 * proceso.getPreguntaProfesional()); if (preguntasProfesional.size() !=
				 * proceso.getPreguntaProfesional()) {
				 * UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
				 * "No existen suficientes preguntas del eje profesional"); } else {
				 * preguntas.addAll(preguntasHumano); preguntas.addAll(preguntasBasico);
				 * preguntas.addAll(preguntasProfesional); } } }
				 * 
				 * if (preguntas.size() == (proceso.getPreguntaBasico() +
				 * proceso.getPreguntaHumano() + proceso.getPreguntaProfesional())) {
				 * PreguntaExamen pe = null; List<PreguntaExamen> preguntasExamenes = new
				 * ArrayList<PreguntaExamen>(); for (Pregunta p : preguntas) { pe = new
				 * PreguntaExamen(); pe.setExamen(examenSeleccionado); pe.setPregunta(p);
				 * preguntasExamenes.add(pe); }
				 * examenSeleccionado.setPreguntasExamenes(preguntasExamenes);
				 * examenService.actualizar(examenSeleccionado); }
				 * 
				 * eecpt = estudiantesExamenComplexivoPTService.obtenerObjeto(
				 * "select ept from EstudianteExamenComplexivoPT ept inner join fetch ept.examenes ex inner join ex.tipoExamen te inner join ept.proceso p "
				 * + "inner join ept.carrera c where ept.id=?1 and te.id=?2", new Object[] {
				 * ept, tipoExamen }, false, new Object[] {});
				 * 
				 * preexaEPT = preguntaExamenService.obtenerLista(
				 * "select preexa from PreguntaExamen preexa inner join preexa.examen ex inner join preexa.pregunta pre where ex.id=?1"
				 * , new Object[] { eecpt.getExamenes().get(0).getId() }, 0, false, new Object[]
				 * {});
				 * 
				 * System.out.println("Preguntas del examen " +
				 * eecpt.getExamenes().get(0).getId() + ", con preguntas: " + preexaEPT.size());
				 */

				int faltantes = falta;

				for (int j = 0; j < preexaEPT.size(); j++) {
					if (j < faltantes) {
						PreguntaExamen pe = preexaEPT.get(j);
						Pregunta pre = pe.getPregunta();
						pe.setRespuesta(pre.getSostiApantisi());
						preguntaExamenService.actualizar(pe);

					} else {
						PreguntaExamen pe = preexaEPT.get(j);
						Pregunta pre = preguntaService.obtenerObjeto("select pre from Pregunta pre where pre.id=?1",
								new Object[] { pe.getPregunta().getId() }, false, new Object[] {});
						pe.setRespuesta((pre.getSostiApantisi()
								.compareTo(new ShaPasswordEncoder().encodePassword("A", null)) == 0
										? new ShaPasswordEncoder().encodePassword("B", null)
										: (pre.getSostiApantisi()
												.compareTo(new ShaPasswordEncoder().encodePassword("B", null)) == 0
														? new ShaPasswordEncoder().encodePassword("C", null)
														: (pre.getSostiApantisi().compareTo(
																new ShaPasswordEncoder().encodePassword("C", null)) == 0
																		? new ShaPasswordEncoder().encodePassword("D",
																				null)
																		: (pre.getSostiApantisi()
																				.compareTo(new ShaPasswordEncoder()
																						.encodePassword("D", null)) == 0
																								? new ShaPasswordEncoder()
																										.encodePassword(
																												"A",
																												null)
																								: "")))));
						preguntaExamenService.actualizar(pe);
					}

				}
				contadorDeCambios++;
			}

			System.out.println("seguimos, número de cambios: " + contadorDeCambios);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	public void crearRegistroExamen() {
		examenService.crearRegistroExamen();
	}

	public void generarPassword(String proceso) {
		List<Usuario> usuarios = usuarioService
				.obtenerLista(
						"select u from Usuario u inner join u.estudiantesExamenComplexivoPT pt "
								+ "inner join pt.proceso p where p.id=?1",
						new Object[] { proceso }, 0, false, new Object[] {});
		int c = 1;
		for (Usuario u : usuarios) {
			System.out.println(c++);
			u.setPassword(UtilSeguridad.generarClave(u.getId().trim()));
			usuarioService.actualizar(u);
		}
	}

	public void ingresarActualizarEstudiantes(String proceso) {
		SpreadsheetEntry spreadsheet;
		try {
			spreadsheet = googleService.getEntry(new URL(
					"https://spreadsheets.google.com/feeds/spreadsheets/private/full/160i8is6zkX6WT3LrbD9QTsLh6C2R5U_44yhF5sZE1C4"),
					SpreadsheetEntry.class);
			URL listFeedUrl = spreadsheet.getWorksheets().get(0).getListFeedUrl();
			ListFeed feed = googleService.getFeed(listFeedUrl, ListFeed.class);
			List<ListEntry> e = feed.getEntries();

			boolean sw = false;

			for (int i = 0; i < e.size(); i++) {

				sw = false;

				System.out.println((i + 1) + ": " + e.get(i).getCustomElements().getValue("DOCUMENTO").trim());

				Usuario usuario = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { e.get(i).getCustomElements().getValue("DOCUMENTO").trim() }, false,
						new Object[] {});

				if (usuario == null) {
					System.out.println("usuario nuevo");
					sw = true;
					usuario = new Usuario();
					usuario.setId(e.get(i).getCustomElements().getValue("DOCUMENTO").trim());
					usuario.setApellidoNombre(e.get(i).getCustomElements().getValue("NOMBRES").trim());
					usuario.setEmail(e.get(i).getCustomElements().getValue("EMAILINST").trim());
					usuario.setEmailPersonal(e.get(i).getCustomElements().getValue("EMAIL").trim());
					usuario.setTelefono(e.get(i).getCustomElements().getValue("CELULAR").trim());
					usuario.setPassword(
							UtilSeguridad.generarClave(e.get(i).getCustomElements().getValue("DOCUMENTO").trim()));
					usuario.setNacionalidad(e.get(i).getCustomElements().getValue("NACIONALIDAD").trim());
					usuario.setActivo(true);

					if (e.get(i).getCustomElements().getValue("GÉNERO").trim().compareTo("MASCULINO") == 0)
						usuario.setGenero("M");
					else if (e.get(i).getCustomElements().getValue("GÉNERO").trim().compareTo("FEMENINO") == 0)
						usuario.setGenero("F");

				} else {
					usuario.setEmail(e.get(i).getCustomElements().getValue("EMAILINST").trim());
					usuario.setEmailPersonal(e.get(i).getCustomElements().getValue("EMAIL").trim());
					usuario.setTelefono(e.get(i).getCustomElements().getValue("CELULAR").trim());
					usuario.setNacionalidad(e.get(i).getCustomElements().getValue("NACIONALIDAD").trim());

					if (e.get(i).getCustomElements().getValue("GÉNERO").trim().compareTo("MASCULINO") == 0)
						usuario.setGenero("M");
					else if (e.get(i).getCustomElements().getValue("GÉNERO").trim().compareTo("FEMENINO") == 0)
						usuario.setGenero("F");

					usuarioService.actualizar(usuario);
				}

				Proceso proceso1 = procesoService.obtenerObjeto("select p from Proceso p where p.id=?1",
						new Object[] { proceso }, false, new Object[] {});

				Carrera carrera = carreraService.obtenerObjeto(
						"select c from Carrera c where TRANSLATE(c.nombre,'ÁÉÍÓÚ','AEIOU')=TRANSLATE(?1,'ÁÉÍÓÚ','AEIOU')",
						new Object[] { e.get(i).getCustomElements().getValue("CARRERA").trim() }, false,
						new Object[] {});

				if (e.get(i).getCustomElements().getValue("FORMATITULACION").trim()
						.compareTo("EXAMEN COMPLEXIVO") == 0) {

					EstudianteExamenComplexivoPT eecpt = estudiantesExamenComplexivoPTService.obtenerObjeto(
							"select eecpt from EstudianteExamenComplexivoPT eecpt "
									+ "inner join eecpt.estudiante e inner join eecpt.carrera c "
									+ "inner join eecpt.proceso p where e.id=?1 and c.id=?2 and p.id=?3",
							new Object[] { usuario.getId(), carrera.getId(), proceso1.getId() }, false,
							new Object[] {});

					if (eecpt == null) {

						Usuario usuarioRol = usuarioService.obtenerObjeto(
								"select u from Usuario u inner join u.permisos p inner join p.rol r where u.id=?1 and r.id='ESEC'",
								new Object[] { e.get(i).getCustomElements().getValue("DOCUMENTO").trim() }, false,
								new Object[] {});

						if (usuarioRol == null) {
							Rol rol = rolService.obtenerPorNombre("ESEC");

							Permiso permiso = new Permiso();
							permiso.setRol(rol);
							permiso.setUsuario(usuario);

							List<Permiso> permisos = new ArrayList<Permiso>();
							permisos.add(permiso);

							usuario.setPermisos(permisos);
						}

						EstudianteExamenComplexivoPT estudianteExamenComplexivoPT = new EstudianteExamenComplexivoPT();
						estudianteExamenComplexivoPT.setEstudiante(usuario);
						estudianteExamenComplexivoPT.setProceso(proceso1);
						estudianteExamenComplexivoPT.setCarrera(carrera);

						estudianteExamenComplexivoPT.setEsProfesional(
								(e.get(i).getCustomElements().getValue("TITULOOBTENER").compareTo("1ER Título") == 0
										? false
										: e.get(i).getCustomElements().getValue("TITULOOBTENER")
												.compareTo(">= 2DO Título") == 0 ? true : false));

						estudianteExamenComplexivoPT.setCreditosTotalesReprobados(
								(e.get(i).getCustomElements().getValue("CREDITOSREPROBADOS").compareTo("NO") == 0
										? false
										: e.get(i).getCustomElements().getValue("CREDITOSREPROBADOS")
												.compareTo("SI") == 0 ? true : false));

						List<EstudianteExamenComplexivoPT> l = new ArrayList<EstudianteExamenComplexivoPT>();
						l.add(estudianteExamenComplexivoPT);
						usuario.setEstudiantesExamenComplexivoPT(l);

						if (sw)
							usuarioService.insertar(usuario);
						else
							usuarioService.actualizar(usuario);

					} else {
						eecpt.setEsProfesional(
								(e.get(i).getCustomElements().getValue("TITULOOBTENER").compareTo("1ER Título") == 0
										? false
										: e.get(i).getCustomElements().getValue("TITULOOBTENER")
												.compareTo(">= 2DO Título") == 0 ? true : false));

						eecpt.setCreditosTotalesReprobados(
								(e.get(i).getCustomElements().getValue("CREDITOSREPROBADOS").compareTo("NO") == 0
										? false
										: e.get(i).getCustomElements().getValue("CREDITOSREPROBADOS")
												.compareTo("SI") == 0 ? true : false));

						estudiantesExamenComplexivoPTService.actualizar(eecpt);
					}
				} else {

					EstudianteTrabajoTitulacion ett = estudianteTrabajoTitulacionService.obtenerObjeto(
							"select ett from EstudianteTrabajoTitulacion ett "
									+ "inner join ett.estudiante e inner join ett.carrera c "
									+ "inner join ett.proceso p where e.id=?1 and c.id=?2 and p.id=?3",
							new Object[] { usuario.getId(), carrera.getId(), proceso1.getId() }, false,
							new Object[] {});

					if (ett == null) {

						Usuario usuarioRol = usuarioService.obtenerObjeto(
								"select u from Usuario u inner join u.permisos p inner join p.rol r where u.id=?1 and r.id='ESTT'",
								new Object[] { e.get(i).getCustomElements().getValue("DOCUMENTO").trim() }, false,
								new Object[] {});

						if (usuarioRol == null) {
							Rol rol = rolService.obtenerPorNombre("ESTT");

							Permiso permiso = new Permiso();
							permiso.setRol(rol);
							permiso.setUsuario(usuario);

							List<Permiso> permisos = new ArrayList<Permiso>();
							permisos.add(permiso);

							usuario.setPermisos(permisos);
						}

						EstudianteTrabajoTitulacion estudianteTrabajoTitulacion = new EstudianteTrabajoTitulacion();
						estudianteTrabajoTitulacion.setEstudiante(usuario);
						estudianteTrabajoTitulacion.setProceso(proceso1);
						estudianteTrabajoTitulacion.setCarrera(carrera);

						estudianteTrabajoTitulacion.setEsProfesional(
								(e.get(i).getCustomElements().getValue("TITULOOBTENER").compareTo("1ER Título") == 0
										? false
										: e.get(i).getCustomElements().getValue("TITULOOBTENER")
												.compareTo(">= 2DO Título") == 0 ? true : false));

						estudianteTrabajoTitulacion.setCreditosTotalesReprobados(
								(e.get(i).getCustomElements().getValue("CREDITOSREPROBADOS").compareTo("NO") == 0
										? false
										: e.get(i).getCustomElements().getValue("CREDITOSREPROBADOS")
												.compareTo("SI") == 0 ? true : false));

						List<EstudianteTrabajoTitulacion> l = new ArrayList<EstudianteTrabajoTitulacion>();
						l.add(estudianteTrabajoTitulacion);
						usuario.setEstudiantesTrabajosTitulaciones(l);

						if (sw)
							usuarioService.insertar(usuario);
						else
							usuarioService.actualizar(usuario);
					} else {
						ett.setEsProfesional(
								(e.get(i).getCustomElements().getValue("TITULOOBTENER").compareTo("1ER Título") == 0
										? false
										: e.get(i).getCustomElements().getValue("TITULOOBTENER")
												.compareTo(">= 2DO Título") == 0 ? true : false));

						ett.setCreditosTotalesReprobados(
								(e.get(i).getCustomElements().getValue("CREDITOSREPROBADOS").compareTo("NO") == 0
										? false
										: e.get(i).getCustomElements().getValue("CREDITOSREPROBADOS")
												.compareTo("SI") == 0 ? true : false));
						estudianteTrabajoTitulacionService.actualizar(ett);
					}
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void main() {
		String proceso = "PT-200720";

		UrlGoogleDrive(proceso);

		// ingresarActualizarEstudiantes(proceso); // (1)
		// examenService.crearRegistroExamen(); // (2)
		// examenService.asignarGrupo(); // (3)

		// actualizarEstudiantesProfesionalesCreditosPerdidos();
		// actualizarEstudiantesNacionalidad();// actualizar usuarios con
	}

	public void proceso2015() {
		try {

			System.out.println("Ruta raiz: " + UtilsArchivos.getRutaRaiz());

			File f = new File(UtilsArchivos.getRutaRaiz() + "/proceso2015.csv");

			int numeroPreguntasRevisadas = 0;
			int numeroPreguntasInsertadas = 0;
			int numeroPreguntasNoInsertadas = 0;
			String preguntasExamenesNulos = "";

			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (null != line) {
				String[] fields = line.split(";");

				Pregunta pregunta = preguntaService.obtenerObjeto("select pre from Pregunta pre where pre.id=?1",
						new Object[] { fields[1] }, false, new Object[] {});

				Examen examen = examenService.obtenerObjeto("select exa from Examen exa where exa.id=?1",
						new Object[] { fields[2] }, false, new Object[] {});

				String respuesta = fields[3];

				if (pregunta == null || examen == null) {
					preguntasExamenesNulos += "id Pregunta faltante: " + fields[1] + ", en el examen: " + fields[2]
							+ "\n";
					numeroPreguntasNoInsertadas++;
				} else {
					PreguntaExamen preguntaExamen = new PreguntaExamen();
					preguntaExamen.setPregunta(pregunta);
					preguntaExamen.setExamen(examen);
					preguntaExamen.setRespuesta(respuesta);

					preguntaExamenService.insertar(preguntaExamen);
					numeroPreguntasInsertadas++;
				}

				line = br.readLine();
				numeroPreguntasRevisadas++;
			}

			System.out.println("Numero de preguntas iteradas: " + numeroPreguntasRevisadas);
			System.out.println("Numero de preguntas insertadas: " + numeroPreguntasInsertadas);
			System.out.println("Numero de preguntas no insertadas: " + numeroPreguntasNoInsertadas);
			System.out.println("Id de preguntas faltantes con sus respectivos examenes: \n" + preguntasExamenesNulos);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void subir() {
		SpreadsheetEntry spreadsheet;
		try {

			spreadsheet = googleService.getEntry(new URL(
					"https://spreadsheets.google.com/feeds/spreadsheets/private/full/160i8is6zkX6WT3LrbD9QTsLh6C2R5U_44yhF5sZE1C4"),
					SpreadsheetEntry.class);
			URL listFeedUrl = spreadsheet.getWorksheets().get(1).getListFeedUrl();
			ListFeed feed = googleService.getFeed(listFeedUrl, ListFeed.class);
			List<ListEntry> e = feed.getEntries();

			for (int i = 0, j = 1; i < e.size(); i++) {
				System.out.println(e.get(i).getCustomElements().getValue("cedula").trim());

				EstudianteExamenComplexivoPT eec = estudiantesExamenComplexivoPTService.obtenerObjeto(
						"select eec from EstudianteExamenComplexivoPT eec "
								+ "inner join eec.estudiante e where e.id=?1",
						new Object[] { e.get(i).getCustomElements().getValue("cedula").trim() }, false,
						new Object[] {});

				if (eec != null) {
					System.out.println(j++);

					String car = e.get(i).getCustomElements().getValue("cf").trim();
					if (e.get(i).getCustomElements().getValue("modalidad") != null && e.get(i).getCustomElements()
							.getValue("modalidad").trim().compareTo("SEMIPRESENCIAL") == 0)
						car = car.concat(" SEMIPRESENCIAL");

					Carrera c = carreraService.obtenerObjeto("select c from Carrera c where c.nombre=?1",
							new Object[] { car }, false, new Object[] {});

					eec.setCarrera(c);
					estudiantesExamenComplexivoPTService.actualizar(eec);
				}
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void subirPreguntas() {
		SpreadsheetEntry spreadsheet;
		try {
			spreadsheet = googleService.getEntry(new URL(
					"https://spreadsheets.google.com/feeds/spreadsheets/private/full/160i8is6zkX6WT3LrbD9QTsLh6C2R5U_44yhF5sZE1C4"),
					SpreadsheetEntry.class);
			URL listFeedUrl = spreadsheet.getWorksheets().get(1).getListFeedUrl();
			ListFeed feed = googleService.getFeed(listFeedUrl, ListFeed.class);
			List<ListEntry> e = feed.getEntries();

			UnidadDidactica ud = unidadDidacticaService.obtenerObjeto("select ud from UnidadDidactica ud where ud.id=1",
					new Object[] {}, false, new Object[] {});

			NivelDificultad nd = nivelDificultadService.obtenerObjeto(
					"select nd from NivelDificultad nd where nd.id='ME'", new Object[] {}, false, new Object[] {});

			int i = 0, t = 0;
			for (i = 0; i < e.size(); i++) {

				Pregunta pregunta = new Pregunta();
				pregunta.setUnidadDidactica(ud);
				pregunta.setNivelDificultad(nd);

				pregunta.setEnunciado(e.get(i).getCustomElements().getValue("e").trim());

				pregunta.setSostiApantisi(e.get(i).getCustomElements().getValue("r").trim());

				if (e.get(i).getCustomElements().getValue("j") != null)
					pregunta.setJustificacion(e.get(i).getCustomElements().getValue("j").trim());

				if (e.get(i).getCustomElements().getValue("b") != null)
					pregunta.setBibliografia(e.get(i).getCustomElements().getValue("b").trim());

				pregunta.setActivo(true);
				pregunta.setSeleccion(true);
				pregunta.setRevisado(true);

				List<PosibleRespuesta> lpr = new ArrayList<PosibleRespuesta>();

				for (t = i; t <= i + 3; t++) {
					PosibleRespuesta pr = new PosibleRespuesta();
					pr.setDescripcion(e.get(t).getCustomElements().getValue("pr").trim());
					pr.setLiteral(e.get(t).getCustomElements().getValue("l").trim());
					pr.setPregunta(pregunta);
					lpr.add(pr);
				}
				i = t - 1;

				pregunta.setPosiblesRespuestas(new ArrayList<PosibleRespuesta>());
				pregunta.getPosiblesRespuestas().addAll(lpr);

				preguntaService.insertar(pregunta);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void UrlGoogleDrive(String proceso) {
		List<EstudianteTrabajoTitulacion> ettList = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett "
						+ "inner join ett.estudiante e inner join ett.proceso p where p.id=?1",
				new Object[] { proceso }, 0, false, new Object[] {});

		int c = 1;
		for (EstudianteTrabajoTitulacion ett : ettList) {
			System.out.println(c++ + ". " + ett.getEstudiante().getId());
		}

	}
}