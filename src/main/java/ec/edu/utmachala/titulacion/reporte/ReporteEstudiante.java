package ec.edu.utmachala.titulacion.reporte;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.html2text;
import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;
import static ec.edu.utmachala.titulacion.utility.UtilsDate.fechaFormatoString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.MallaCurricular;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.Caratula;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.MallaCurricularService;
import ec.edu.utmachala.titulacion.utility.ReporteService;

@Controller
@Scope("session")
public class ReporteEstudiante {

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private EstudiantesExamenComplexivoPPService temaPracticoService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private MallaCurricularService mallaCurricularService;

	private String emailUsuario = "";
	private List<Carrera> listCarreraCaratulaComplexivo;
	private Integer carreraCaratulaComplexivo;
	private List<Carrera> listCarreraCaratulaTrabajoTitulacion;
	private Integer carreraCaratulaTrabajoTitulacion;

	public Integer getCarreraCaratulaComplexivo() {
		return carreraCaratulaComplexivo;
	}

	public Integer getCarreraCaratulaTrabajoTitulacion() {
		return carreraCaratulaTrabajoTitulacion;
	}

	public List<Carrera> getListCarreraCaratulaComplexivo() {
		return listCarreraCaratulaComplexivo;
	}

	public List<Carrera> getListCarreraCaratulaTrabajoTitulacion() {
		return listCarreraCaratulaTrabajoTitulacion;
	}

	public List<Carrera> getListCarreraComplexivo() {
		return listCarreraCaratulaComplexivo;
	}

	@PostConstruct
	public void init() {
		emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		listCarreraCaratulaComplexivo = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.peridosExamenes pe "
						+ "inner join pe.estudiantesPeriodos ep inner join ep.estudiante e "
						+ "where e.email=?1 order by c.nombre",
				new Object[] { emailUsuario }, 0, false, new Object[] {});
		if (listCarreraCaratulaComplexivo.size() == 1) {
			carreraCaratulaComplexivo = listCarreraCaratulaComplexivo.get(0).getId();
		}
		listCarreraCaratulaTrabajoTitulacion = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.estudiantesTrabajosTitulaciones ett "
						+ "inner join ett.estudiante e where e.email=?1 order by c.nombre",
				new Object[] { emailUsuario }, 0, false, new Object[] {});
		if (listCarreraCaratulaTrabajoTitulacion.size() == 1) {
			carreraCaratulaTrabajoTitulacion = listCarreraCaratulaTrabajoTitulacion.get(0).getId();
		}

	}

	public void reporteCaratulaComplexivo() {
		List<Caratula> list = new ArrayList<Caratula>();
		EstudianteExamenComplexivoPP tp = temaPracticoService.obtenerObjeto(
				"select tp from TemaPractico tp inner join fetch tp.estudiantePeriodo ep "
						+ "inner join ep.estudiante u inner join ep.periodoExamen pe inner join pe.carrera c "
						+ "where u.email=?1 and c.id=?2 order by ep.id desc",
				new Object[] { emailUsuario, carreraCaratulaComplexivo }, false, new Object[] {});
		String nombre = "";
		if (tp == null || tp.getId() == null) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene tema práctico");
		} else if (tp.getFechaResolucion() == null || tp.getResolucion() == null) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR,
					"No tiene una resolución de Consejo Directivo, por favor acérquese a la UMMOG");
		} else if (tp.getTituloInvestigacion() == null || tp.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene el título del caso práctico");
		} else {
			Caratula c = new Caratula();
			// c.setUnidadAcademica(
			// tp.getEstudiantePeriodo().getPeriodoExamen().getCarrera().getUnidadAcademica().getNombre());
			// c.setCarrera(tp.getEstudiantePeriodo().getPeriodoExamen().getCarrera().getNombre());
			c.setTitulo(tp.getTituloInvestigacion().toUpperCase());
			// Usuario estu = tp.getEstudiantePeriodo().getEstudiante();
			Usuario estu = null;
			c.setGenero(estu.getGenero().compareToIgnoreCase("M") == 0 ? true : false);
			MallaCurricular mc = mallaCurricularService.obtenerVigentePorAlumno(carreraCaratulaComplexivo,
					tp.getFechaInicioClase());
			Carrera ca = carreraService.obtenerObjeto("select c from Carrera where c.id=?1",
					new Object[] { carreraCaratulaComplexivo }, false, new Object[0]);
			c.setGradoAcademico("TRABAJO PRÁCTICO DEL EXAMEN COMPLEXIVO PREVIO A LA OBTENCIÓN DEL TÍTULO DE "
					+ (c.isGenero() == true
							? ca.getNomenclaturaTituloMasculino() + " " + ca.getTipoTitulo() + " "
									+ ca.getDetalleTitulo()
							: ca.getNomenclaturaTituloFemenino() + " " + ca.getTipoTitulo() + " "
									+ ca.getDetalleTitulo()));

			// c.setAutor1(estu.getId() + "¬"
			// + (estu.getApellidoPaterno() == null ? "" :
			// estu.getApellidoPaterno().toUpperCase()) + " "
			// + (estu.getApellidoMaterno() == null ? "" :
			// estu.getApellidoMaterno().toUpperCase()) + " "
			// + estu.getNombre().toUpperCase());

			c.setLugarMesAnio("MACHALA - EL ORO");

			c.setFechaSustentacion("Machala, " + fechaFormatoString(new Date(), "dd 'de' MMMM 'de' yyyy"));
			Usuario tutor1 = tp.getEspecialista1();
			Usuario tutor2 = tp.getEspecialista2();
			Usuario tutor3 = tp.getEspecialista3();
			// c.setTutor1(tutor1.getId() + "¬"
			// + (tutor1.getApellidoPaterno() == null ? "" :
			// tutor1.getApellidoPaterno().toUpperCase()) + " "
			// + (tutor1.getApellidoMaterno() == null ? "" :
			// tutor1.getApellidoMaterno().toUpperCase()) + " "
			// + tutor1.getNombre().toUpperCase());
			// c.setTutor2(tutor2.getId() + "¬"
			// + (tutor2.getApellidoPaterno() == null ? "" :
			// tutor2.getApellidoPaterno().toUpperCase()) + " "
			// + (tutor2.getApellidoMaterno() == null ? "" :
			// tutor2.getApellidoMaterno().toUpperCase()) + " "
			// + tutor2.getNombre().toUpperCase());
			// c.setTutor3(tutor3.getId() + "¬"
			// + (tutor3.getApellidoPaterno() == null ? "" :
			// tutor3.getApellidoPaterno().toUpperCase()) + " "
			// + (tutor3.getApellidoMaterno() == null ? "" :
			// tutor3.getApellidoMaterno().toUpperCase()) + " "
			// + tutor3.getNombre().toUpperCase());

			list.add(c);
			Map<String, Object> parametro = new HashMap<String, Object>();
			nombre = "CaratulaComplexivo-" + estu.getId();
			File pdfCaratula = reporteService.generarReportePDFFile(list, parametro, "CaratulaComplexivo", nombre);
			nombre = "ActaCesión-" + estu.getId();
			File pdfActaCesion = reporteService.generarReportePDFFile(list, parametro, "ActaCesion", nombre);
			File file = null;
			nombre = "DocumentosComplexivo-" + estu.getId();
			try {
				file = File.createTempFile(nombre, ".pdf");
				PdfCopyFields concat = new PdfCopyFields(new FileOutputStream(file));
				PdfReader reader1 = new PdfReader(new FileInputStream(pdfCaratula));
				PdfReader reader2 = new PdfReader(new FileInputStream(pdfActaCesion));
				concat.addDocument(reader1);
				concat.addDocument(reader2);
				concat.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			reporteService.responderServidor(file, nombre);
		}
	}

	public void reporteCaratulaTrabajoTitulacion() {
		List<Caratula> list = new ArrayList<Caratula>();
		List<EstudianteTrabajoTitulacion> listETT = estudianteTrabajoTitulacionService.obtenerPorSql(
				"SELECT ett.* FROM exetasi.\"estudiantesTrabajosTitulacion\" ett "
						+ "inner join exetasi.usuarios u on (ett.estudiante=u.id) "
						+ "where (SELECT ett1.\"seminarioTrabajoTitulacion\" "
						+ "FROM exetasi.\"estudiantesTrabajosTitulacion\" ett1 "
						+ "inner join exetasi.carreras c on (ett1.carrera=c.id) "
						+ "inner join exetasi.usuarios e on (ett1.estudiante=e.id) where e.email='" + emailUsuario
						+ "' and c.id=" + carreraCaratulaTrabajoTitulacion + ")=ett.\"seminarioTrabajoTitulacion\" "
						+ "order by u.\"apellidoPaterno\", u.\"apellidoMaterno\", u.nombre;",
				EstudianteTrabajoTitulacion.class);
		String nombre = "";
		if (listETT.size() == 1) {
			EstudianteTrabajoTitulacion ett = listETT.get(0);
			if (ett.getFechaResolucion() == null || ett.getResolucion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene una resolución");
			} else if (ett.getSeminarioTrabajoTitulacion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No esta en un seminario");
			} else {
				Caratula c = new Caratula();
				c.setUnidadAcademica(ett.getCarrera().getUnidadAcademica().getNombre());
				c.setCarrera(ett.getCarrera().getNombre());
				c.setTitulo(ett.getTituloInvestigacion().toUpperCase());
				Usuario estu = ett.getEstudiante();
				c.setGenero(estu.getGenero().compareToIgnoreCase("M") == 0 ? true : false);
				MallaCurricular mc = mallaCurricularService.obtenerVigentePorAlumno(carreraCaratulaTrabajoTitulacion,
						ett.getFechaInicioClase());
				Carrera ca = carreraService.obtenerObjeto("select c from Carrera where c.id=?1",
						new Object[] { carreraCaratulaComplexivo }, false, new Object[0]);
				c.setGradoAcademico("TRABAJO DE TITULACIÓN PREVIO A LA OBTENCIÓN DEL TÍTULO DE " + (c.isGenero() == true
						? ca.getNomenclaturaTituloMasculino() + " " + ca.getTipoTitulo() + " " + ca.getDetalleTitulo()
						: ca.getNomenclaturaTituloFemenino() + " " + ca.getTipoTitulo() + " " + ca.getDetalleTitulo()));

				// c.setAutor1(estu.getId() + "¬"
				// + (estu.getApellidoPaterno() == null ? "" :
				// estu.getApellidoPaterno().toUpperCase()) + " "
				// + (estu.getApellidoMaterno() == null ? "" :
				// estu.getApellidoMaterno().toUpperCase()) + " "
				// + estu.getNombre().toUpperCase() + "¬" + estu.getEmail());

				Usuario tutor = ett.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente();
				// c.setTutor1(tutor.getId() + "¬"
				// + (tutor.getApellidoPaterno() == null ? "" :
				// tutor.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor.getApellidoMaterno() == null ? "" :
				// tutor.getApellidoMaterno().toUpperCase()) + " "
				// + tutor.getNombre().toUpperCase() + "¬" + tutor.getEmail());

				c.setLugarMesAnio("MACHALA - EL ORO");

				c.setFechaSustentacion(
						"Machala, " + fechaFormatoString(ett.getFechaSustentacion(), "dd 'de' MMMM 'de' yyyy"));

				list.add(c);
				Map<String, Object> parametro = new HashMap<String, Object>();
				nombre = "CaratulaTrabajoTitulación-" + estu.getId();
				File pdfCaratula = reporteService.generarReportePDFFile(list, parametro, "CaratulaTrabajoTitulacion",
						nombre);
				nombre = "ActaCesión-" + estu.getId();
				File pdfActaCesion = reporteService.generarReportePDFFile(list, parametro, "ActaCesion", nombre);
				nombre = "Frontispicio-" + estu.getId();
				File pdfFrontispicio = reporteService.generarReportePDFFile(list, parametro, "Frontispicio", nombre);

				Usuario esp1 = ett.getEspecialista1();
				Usuario esp2 = ett.getEspecialista2();
				Usuario esp3 = ett.getEspecialista3();
				Usuario espSupl1 = ett.getEspecialistaSuplente1();
				Usuario espSupl2 = ett.getEspecialistaSuplente2();
				// c.setTutor1(esp1.getId() + "¬"
				// + (esp1.getApellidoPaterno() == null ? "" :
				// esp1.getApellidoPaterno().toUpperCase()) + " "
				// + (esp1.getApellidoMaterno() == null ? "" :
				// esp1.getApellidoMaterno().toUpperCase()) + " "
				// + esp1.getNombre().toUpperCase());
				// c.setTutor2(esp2.getId() + "¬"
				// + (esp2.getApellidoPaterno() == null ? "" :
				// esp2.getApellidoPaterno().toUpperCase()) + " "
				// + (esp2.getApellidoMaterno() == null ? "" :
				// esp2.getApellidoMaterno().toUpperCase()) + " "
				// + esp2.getNombre().toUpperCase());
				// c.setTutor3(esp3.getId() + "¬"
				// + (esp3.getApellidoPaterno() == null ? "" :
				// esp3.getApellidoPaterno().toUpperCase()) + " "
				// + (esp3.getApellidoMaterno() == null ? "" :
				// esp3.getApellidoMaterno().toUpperCase()) + " "
				// + esp3.getNombre().toUpperCase());
				// c.setTutorSuplente1(
				// espSupl1.getId() + "¬"
				// + (espSupl1.getApellidoPaterno() == null ? ""
				// : espSupl1.getApellidoPaterno().toUpperCase())
				// + " "
				// + (espSupl1.getApellidoMaterno() == null ? ""
				// : espSupl1.getApellidoMaterno().toUpperCase())
				// + " " + espSupl1.getNombre().toUpperCase());
				// c.setTutorSuplente2(
				// espSupl2.getId() + "¬"
				// + (espSupl2.getApellidoPaterno() == null ? ""
				// : espSupl2.getApellidoPaterno().toUpperCase())
				// + " "
				// + (espSupl2.getApellidoMaterno() == null ? ""
				// : espSupl2.getApellidoMaterno().toUpperCase())
				// + " " + espSupl2.getNombre().toUpperCase());
				nombre = "ActaComiteEvaluadorTrabajoTitulacion-" + estu.getId();
				File pdfActaComiteEvaluadorTrabajoTitulacion = reporteService.generarReportePDFFile(list, parametro,
						"ActaComiteEvaluadorTrabajoTitulacion", nombre);

				File file = null;
				nombre = "DocumentosTrabajoTitulación-" + estu.getId();
				try {
					file = File.createTempFile(nombre, ".pdf");
					PdfCopyFields concat = new PdfCopyFields(new FileOutputStream(file));
					PdfReader reader1 = new PdfReader(new FileInputStream(pdfCaratula));
					PdfReader reader2 = new PdfReader(new FileInputStream(pdfActaCesion));
					PdfReader reader3 = new PdfReader(new FileInputStream(pdfFrontispicio));
					PdfReader reader4 = new PdfReader(new FileInputStream(pdfActaComiteEvaluadorTrabajoTitulacion));
					concat.addDocument(reader1);
					concat.addDocument(reader2);
					concat.addDocument(reader3);
					concat.addDocument(reader4);
					concat.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				reporteService.responderServidor(file, nombre);
			}
		} else if (listETT.size() == 2) {
			EstudianteTrabajoTitulacion ett = listETT.get(0);
			EstudianteTrabajoTitulacion ett1 = listETT.get(1);
			if (ett.getFechaResolucion() == null || ett.getResolucion() == null || ett1.getFechaResolucion() == null
					|| ett1.getResolucion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene una resolución");
			} else if (ett.getSeminarioTrabajoTitulacion() == null || ett1.getSeminarioTrabajoTitulacion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No esta en un seminario");
			} else {
				boolean bnTitulos = false;
				Caratula c = new Caratula();
				c.setUnidadAcademica(ett.getCarrera().getUnidadAcademica().getNombre());
				c.setCarrera(ett.getCarrera().getNombre());
				c.setTitulo(ett.getTituloInvestigacion().toUpperCase());
				Usuario estu = ett.getEstudiante();
				Usuario estu1 = ett1.getEstudiante();
				if (ett.getEstudiante().getGenero().compareToIgnoreCase("M") == 0
						&& ett1.getEstudiante().getGenero().compareToIgnoreCase("F") == 0) {
					estu = ett1.getEstudiante();
					estu1 = ett.getEstudiante();
					bnTitulos = true;
				} else if (ett.getEstudiante().getGenero().compareToIgnoreCase("F") == 0
						&& ett1.getEstudiante().getGenero().compareToIgnoreCase("M") == 0) {
					bnTitulos = true;
				}
				c.setGenero(estu.getGenero().compareToIgnoreCase("M") == 0 ? true : false);
				MallaCurricular mc = mallaCurricularService.obtenerVigentePorAlumno(carreraCaratulaTrabajoTitulacion,
						ett.getFechaInicioClase());
				Carrera ca = carreraService.obtenerObjeto("select c from Carrera where c.id=?1",
						new Object[] { carreraCaratulaComplexivo }, false, new Object[0]);
				c.setGradoAcademico("TRABAJO DE TITULACIÓN PREVIO A LA OBTENCIÓN DEL TÍTULO DE " + (c.isGenero() == true
						? ca.getNomenclaturaTituloMasculino() + " " + ca.getTipoTitulo() + " " + ca.getDetalleTitulo()
						: ca.getNomenclaturaTituloFemenino() + " " + ca.getTipoTitulo() + " " + ca.getDetalleTitulo()));
				if (bnTitulos)
					c.setGradoAcademico("TRABAJO DE TITULACIÓN PREVIO A LA OBTENCIÓN DEL TÍTULO DE "
							+ ca.getNomenclaturaTituloFemenino() + " " + ca.getTipoTitulo() + " "
							+ ca.getDetalleTitulo() + " Y/E " + ca.getNomenclaturaTituloMasculino() + " "
							+ ca.getTipoTitulo() + " " + ca.getDetalleTitulo());

				// c.setAutor1(estu.getId() + "¬"
				// + (estu.getApellidoPaterno() == null ? "" :
				// estu.getApellidoPaterno().toUpperCase()) + " "
				// + (estu.getApellidoMaterno() == null ? "" :
				// estu.getApellidoMaterno().toUpperCase()) + " "
				// + estu.getNombre().toUpperCase() + "¬" + estu.getEmail());
				// c.setAutor2(estu1.getId() + "¬"
				// + (estu1.getApellidoPaterno() == null ? "" :
				// estu1.getApellidoPaterno().toUpperCase()) + " "
				// + (estu1.getApellidoMaterno() == null ? "" :
				// estu1.getApellidoMaterno().toUpperCase()) + " "
				// + estu1.getNombre().toUpperCase() + "¬" + estu1.getEmail());

				Usuario tutor = ett.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente();
				// c.setTutor1(tutor.getId() + "¬"
				// + (tutor.getApellidoPaterno() == null ? "" :
				// tutor.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor.getApellidoMaterno() == null ? "" :
				// tutor.getApellidoMaterno().toUpperCase()) + " "
				// + tutor.getNombre().toUpperCase() + "¬" + tutor.getEmail());

				c.setLugarMesAnio("MACHALA - EL ORO");

				c.setFechaSustentacion(
						"Machala, " + fechaFormatoString(ett.getFechaSustentacion(), "dd 'de' MMMM 'de' yyyy"));

				list.add(c);
				Map<String, Object> parametro = new HashMap<String, Object>();
				nombre = "CaratulaTrabajoTitulación-" + estu.getId() + "-" + estu1.getId();
				File pdfCaratula = reporteService.generarReportePDFFile(list, parametro, "CaratulaTrabajoTitulacion",
						nombre);
				nombre = "ActaCesión-" + estu.getId() + "-" + estu1.getId();
				File pdfActaCesion = reporteService.generarReportePDFFile(list, parametro, "ActaCesion1", nombre);
				nombre = "Frontispicio-" + estu.getId() + "-" + estu1.getId();
				File pdfFrontispicio = reporteService.generarReportePDFFile(list, parametro, "Frontispicio", nombre);

				Usuario esp1 = ett.getEspecialista1();
				Usuario esp2 = ett.getEspecialista2();
				Usuario esp3 = ett.getEspecialista3();
				Usuario espSupl1 = ett.getEspecialistaSuplente1();
				Usuario espSupl2 = ett.getEspecialistaSuplente2();
				// c.setTutor1(esp1.getId() + "¬"
				// + (esp1.getApellidoPaterno() == null ? "" :
				// esp1.getApellidoPaterno().toUpperCase()) + " "
				// + (esp1.getApellidoMaterno() == null ? "" :
				// esp1.getApellidoMaterno().toUpperCase()) + " "
				// + esp1.getNombre().toUpperCase());
				// c.setTutor2(esp2.getId() + "¬"
				// + (esp2.getApellidoPaterno() == null ? "" :
				// esp2.getApellidoPaterno().toUpperCase()) + " "
				// + (esp2.getApellidoMaterno() == null ? "" :
				// esp2.getApellidoMaterno().toUpperCase()) + " "
				// + esp2.getNombre().toUpperCase());
				// c.setTutor3(esp3.getId() + "¬"
				// + (esp3.getApellidoPaterno() == null ? "" :
				// esp3.getApellidoPaterno().toUpperCase()) + " "
				// + (esp3.getApellidoMaterno() == null ? "" :
				// esp3.getApellidoMaterno().toUpperCase()) + " "
				// + esp3.getNombre().toUpperCase());
				// c.setTutorSuplente1(
				// espSupl1.getId() + "¬"
				// + (espSupl1.getApellidoPaterno() == null ? ""
				// : espSupl1.getApellidoPaterno().toUpperCase())
				// + " "
				// + (espSupl1.getApellidoMaterno() == null ? ""
				// : espSupl1.getApellidoMaterno().toUpperCase())
				// + " " + espSupl1.getNombre().toUpperCase());
				// c.setTutorSuplente2(
				// espSupl2.getId() + "¬"
				// + (espSupl2.getApellidoPaterno() == null ? ""
				// : espSupl2.getApellidoPaterno().toUpperCase())
				// + " "
				// + (espSupl2.getApellidoMaterno() == null ? ""
				// : espSupl2.getApellidoMaterno().toUpperCase())
				// + " " + espSupl2.getNombre().toUpperCase());
				nombre = "ActaComiteEvaluadorTrabajoTitulacion1-" + estu.getId() + "-" + estu1.getId();
				File pdfActaComiteEvaluadorTrabajoTitulacion = reporteService.generarReportePDFFile(list, parametro,
						"ActaComiteEvaluadorTrabajoTitulacion1", nombre);

				File file = null;
				nombre = "DocumentosTrabajoTitulación-" + estu.getId() + "-" + estu1.getId();
				try {
					file = File.createTempFile(nombre, ".pdf");
					PdfCopyFields concat = new PdfCopyFields(new FileOutputStream(file));
					PdfReader reader1 = new PdfReader(new FileInputStream(pdfCaratula));
					PdfReader reader2 = new PdfReader(new FileInputStream(pdfActaCesion));
					PdfReader reader3 = new PdfReader(new FileInputStream(pdfFrontispicio));
					PdfReader reader4 = new PdfReader(new FileInputStream(pdfActaComiteEvaluadorTrabajoTitulacion));
					concat.addDocument(reader1);
					concat.addDocument(reader2);
					concat.addDocument(reader3);
					concat.addDocument(reader4);
					concat.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				reporteService.responderServidor(file, nombre);
			}
		}
	}

	public void reporteConstanciaEntregaComplexivo() {
		List<Caratula> list = new ArrayList<Caratula>();
		EstudianteExamenComplexivoPP tp = temaPracticoService.obtenerObjeto(
				"select tp from TemaPractico tp inner join fetch tp.estudiantePeriodo ep "
						+ "inner join ep.estudiante u inner join ep.periodoExamen pe inner join pe.carrera c "
						+ "where u.email=?1 and c.id=?2 order by ep.id desc",
				new Object[] { emailUsuario, carreraCaratulaComplexivo }, false, new Object[] {});
		String nombre = "";
		if (tp == null || tp.getId() == null) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene tema práctico");
		} else if (tp.getFechaResolucion() == null || tp.getResolucion() == null) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene una resolución");
		} else if (tp.getTituloInvestigacion() == null || tp.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene el título del caso práctico");
		} else {
			Caratula c = new Caratula();
			// c.setUnidadAcademica(tp.getEstudiantePeriodo().getPeriodoExamen().getCarrera().getUnidadAcademica().getId()
			// + "-" +
			// tp.getEstudiantePeriodo().getPeriodoExamen().getCarrera().getUnidadAcademica().getNombre());
			// c.setCarrera(tp.getEstudiantePeriodo().getPeriodoExamen().getCarrera().getNombre());
			c.setTitulo(tp.getTituloInvestigacion().toUpperCase());
			// c.setGradoAcademico(tp.getEstudiantePeriodo().getPeriodoExamen().getProceso().getId());
			c.setAutor2(tp.getResolucion());
			c.setAutor3(fechaFormatoString(tp.getFechaResolucion()));
			// Usuario estu = tp.getEstudiantePeriodo().getEstudiante();
			Usuario estu = null;
			// c.setAutor1(estu.getId() + "¬"
			// + (estu.getApellidoPaterno() == null ? "" :
			// estu.getApellidoPaterno().toUpperCase()) + " "
			// + (estu.getApellidoMaterno() == null ? "" :
			// estu.getApellidoMaterno().toUpperCase()) + " "
			// + estu.getNombre().toUpperCase());

			c.setFechaSustentacion(
					fechaFormatoString(tp.getFechaSustentacionOrdinaria(), "dd 'de' MMMM 'de' yyyy HH:mm"));
			if (tp.getFechaSustentacionGracia() != null)
				c.setFechaSustentacion(
						fechaFormatoString(tp.getFechaSustentacionGracia(), "dd 'de' MMMM 'de' yyyy HH:mm"));

			Usuario tutor1 = tp.getEspecialista1();
			Usuario tutor2 = tp.getEspecialista2();
			Usuario tutor3 = tp.getEspecialista3();
			// c.setTutor1(tutor1.getId() + "¬"
			// + (tutor1.getApellidoPaterno() == null ? "" :
			// tutor1.getApellidoPaterno().toUpperCase()) + " "
			// + (tutor1.getApellidoMaterno() == null ? "" :
			// tutor1.getApellidoMaterno().toUpperCase()) + " "
			// + tutor1.getNombre().toUpperCase());
			// c.setTutor2(tutor2.getId() + "¬"
			// + (tutor2.getApellidoPaterno() == null ? "" :
			// tutor2.getApellidoPaterno().toUpperCase()) + " "
			// + (tutor2.getApellidoMaterno() == null ? "" :
			// tutor2.getApellidoMaterno().toUpperCase()) + " "
			// + tutor2.getNombre().toUpperCase());
			// c.setTutor3(tutor3.getId() + "¬"
			// + (tutor3.getApellidoPaterno() == null ? "" :
			// tutor3.getApellidoPaterno().toUpperCase()) + " "
			// + (tutor3.getApellidoMaterno() == null ? "" :
			// tutor3.getApellidoMaterno().toUpperCase()) + " "
			// + tutor3.getNombre().toUpperCase());

			list.add(c);
			Map<String, Object> parametro = new HashMap<String, Object>();
			nombre = "DocumentoEntrega-Recepción-" + estu.getId();
			parametro.put("logoUnidadAcademica", 5);
			// parametro.put("logoUnidadAcademica",
			// tp.getEstudiantePeriodo().getPeriodoExamen().getCarrera().getUnidadAcademica().getId());
			reporteService.generarReportePDF(list, parametro, "ConstanciaDeEntregaUMMOGComplexivo", nombre);
		}
	}

	public void reporteConstanciaEntregaTrabajoTitulacion() {
		List<Caratula> list = new ArrayList<Caratula>();
		List<EstudianteTrabajoTitulacion> listETT = estudianteTrabajoTitulacionService.obtenerPorSql(
				"SELECT ett.* FROM exetasi.\"estudiantesTrabajosTitulacion\" ett "
						+ "inner join exetasi.usuarios u on (ett.estudiante=u.id) "
						+ "where (SELECT ett1.\"seminarioTrabajoTitulacion\" "
						+ "FROM exetasi.\"estudiantesTrabajosTitulacion\" ett1 "
						+ "inner join exetasi.carreras c on (ett1.carrera=c.id) "
						+ "inner join exetasi.usuarios e on (ett1.estudiante=e.id) where e.email='" + emailUsuario
						+ "' and c.id=" + carreraCaratulaTrabajoTitulacion + ")=ett.\"seminarioTrabajoTitulacion\" "
						+ "order by u.\"apellidoPaterno\", u.\"apellidoMaterno\", u.nombre;",
				EstudianteTrabajoTitulacion.class);
		String nombre = "";
		if (listETT.size() == 1) {
			EstudianteTrabajoTitulacion ett = listETT.get(0);
			if (ett.getFechaResolucion() == null || ett.getResolucion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene una resolución");
			} else if (ett.getSeminarioTrabajoTitulacion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No esta en un seminario");
			} else {
				Caratula c = new Caratula();
				c.setUnidadAcademica(ett.getCarrera().getUnidadAcademica().getId() + "-"
						+ ett.getCarrera().getUnidadAcademica().getNombre());
				c.setCarrera(ett.getCarrera().getNombre());
				c.setTitulo(html2text(ett.getTituloInvestigacion()).toUpperCase());
				c.setGradoAcademico(ett.getSeminarioTrabajoTitulacion().getSeminario().getProceso().getId());
				c.setAutor2(ett.getResolucion());
				c.setAutor3(fechaFormatoString(ett.getFechaResolucion()));
				Usuario estu = ett.getEstudiante();
				// c.setAutor1(estu.getId() + "¬"
				// + (estu.getApellidoPaterno() == null ? "" :
				// estu.getApellidoPaterno().toUpperCase()) + " "
				// + (estu.getApellidoMaterno() == null ? "" :
				// estu.getApellidoMaterno().toUpperCase()) + " "
				// + estu.getNombre().toUpperCase());

				c.setFechaSustentacion(fechaFormatoString(ett.getFechaSustentacion(), "dd 'de' MMMM 'de' yyyy HH:mm"));
				Usuario tutor1 = ett.getEspecialista1();
				Usuario tutor2 = ett.getEspecialista2();
				Usuario tutor3 = ett.getEspecialista3();
				Usuario tutorS1 = ett.getEspecialistaSuplente1();
				Usuario tutorS2 = ett.getEspecialistaSuplente2();
				// c.setTutor1(tutor1.getId() + "¬"
				// + (tutor1.getApellidoPaterno() == null ? "" :
				// tutor1.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor1.getApellidoMaterno() == null ? "" :
				// tutor1.getApellidoMaterno().toUpperCase()) + " "
				// + tutor1.getNombre().toUpperCase());
				// c.setTutor2(tutor2.getId() + "¬"
				// + (tutor2.getApellidoPaterno() == null ? "" :
				// tutor2.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor2.getApellidoMaterno() == null ? "" :
				// tutor2.getApellidoMaterno().toUpperCase()) + " "
				// + tutor2.getNombre().toUpperCase());
				// c.setTutor3(tutor3.getId() + "¬"
				// + (tutor3.getApellidoPaterno() == null ? "" :
				// tutor3.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor3.getApellidoMaterno() == null ? "" :
				// tutor3.getApellidoMaterno().toUpperCase()) + " "
				// + tutor3.getNombre().toUpperCase());
				// c.setTutorSuplente1(tutorS1.getId() + "¬"
				// + (tutorS1.getApellidoPaterno() == null ? "" :
				// tutorS1.getApellidoPaterno().toUpperCase()) + " "
				// + (tutorS1.getApellidoMaterno() == null ? "" :
				// tutorS1.getApellidoMaterno().toUpperCase()) + " "
				// + tutorS1.getNombre().toUpperCase());
				// c.setTutorSuplente2(tutorS2.getId() + "¬"
				// + (tutorS2.getApellidoPaterno() == null ? "" :
				// tutorS2.getApellidoPaterno().toUpperCase()) + " "
				// + (tutorS2.getApellidoMaterno() == null ? "" :
				// tutorS2.getApellidoMaterno().toUpperCase()) + " "
				// + tutorS2.getNombre().toUpperCase());

				list.add(c);
				Map<String, Object> parametro = new HashMap<String, Object>();
				nombre = "DocumentoEntrega-Recepción-" + estu.getId();
				parametro.put("logoUnidadAcademica", ett.getCarrera().getUnidadAcademica().getId());
				reporteService.generarReportePDF(list, parametro, "ConstanciaDeEntregaUMMOGTrabajoTitulacion", nombre);
			}
		} else if (listETT.size() == 2) {
			EstudianteTrabajoTitulacion ett = listETT.get(0);
			EstudianteTrabajoTitulacion ett1 = listETT.get(1);
			if (ett.getFechaResolucion() == null || ett.getResolucion() == null || ett1.getFechaResolucion() == null
					|| ett1.getResolucion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene una resolución");
			} else if (ett.getSeminarioTrabajoTitulacion() == null || ett1.getSeminarioTrabajoTitulacion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No esta en un seminario");
			} else {
				Caratula c = new Caratula();
				c.setUnidadAcademica(ett.getCarrera().getUnidadAcademica().getId() + "-"
						+ ett.getCarrera().getUnidadAcademica().getNombre());
				c.setCarrera(ett.getCarrera().getNombre());
				c.setTitulo(html2text(ett.getTituloInvestigacion()).toUpperCase());
				c.setGradoAcademico(ett.getSeminarioTrabajoTitulacion().getSeminario().getProceso().getId());
				c.setResolucion(ett.getResolucion());
				c.setFechaResolucion(fechaFormatoString(ett.getFechaResolucion()));
				Usuario estu = ett.getEstudiante();
				Usuario estu1 = ett1.getEstudiante();
				// c.setAutor1(estu.getId() + "¬"
				// + (estu.getApellidoPaterno() == null ? "" :
				// estu.getApellidoPaterno().toUpperCase()) + " "
				// + (estu.getApellidoMaterno() == null ? "" :
				// estu.getApellidoMaterno().toUpperCase()) + " "
				// + estu.getNombre().toUpperCase());
				// c.setAutor2(estu1.getId() + "¬"
				// + (estu1.getApellidoPaterno() == null ? "" :
				// estu1.getApellidoPaterno().toUpperCase()) + " "
				// + (estu1.getApellidoMaterno() == null ? "" :
				// estu1.getApellidoMaterno().toUpperCase()) + " "
				// + estu1.getNombre().toUpperCase());

				c.setFechaSustentacion(fechaFormatoString(ett.getFechaSustentacion(), "dd 'de' MMMM 'de' yyyy HH:mm"));
				Usuario tutor1 = ett.getEspecialista1();
				Usuario tutor2 = ett.getEspecialista2();
				Usuario tutor3 = ett.getEspecialista3();
				Usuario tutorS1 = ett.getEspecialistaSuplente1();
				Usuario tutorS2 = ett.getEspecialistaSuplente2();
				// c.setTutor1(tutor1.getId() + "¬"
				// + (tutor1.getApellidoPaterno() == null ? "" :
				// tutor1.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor1.getApellidoMaterno() == null ? "" :
				// tutor1.getApellidoMaterno().toUpperCase()) + " "
				// + tutor1.getNombre().toUpperCase());
				// c.setTutor2(tutor2.getId() + "¬"
				// + (tutor2.getApellidoPaterno() == null ? "" :
				// tutor2.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor2.getApellidoMaterno() == null ? "" :
				// tutor2.getApellidoMaterno().toUpperCase()) + " "
				// + tutor2.getNombre().toUpperCase());
				// c.setTutor3(tutor3.getId() + "¬"
				// + (tutor3.getApellidoPaterno() == null ? "" :
				// tutor3.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor3.getApellidoMaterno() == null ? "" :
				// tutor3.getApellidoMaterno().toUpperCase()) + " "
				// + tutor3.getNombre().toUpperCase());
				// c.setTutorSuplente1(tutorS1.getId() + "¬"
				// + (tutorS1.getApellidoPaterno() == null ? "" :
				// tutorS1.getApellidoPaterno().toUpperCase()) + " "
				// + (tutorS1.getApellidoMaterno() == null ? "" :
				// tutorS1.getApellidoMaterno().toUpperCase()) + " "
				// + tutorS1.getNombre().toUpperCase());
				// c.setTutorSuplente2(tutorS2.getId() + "¬"
				// + (tutorS2.getApellidoPaterno() == null ? "" :
				// tutorS2.getApellidoPaterno().toUpperCase()) + " "
				// + (tutorS2.getApellidoMaterno() == null ? "" :
				// tutorS2.getApellidoMaterno().toUpperCase()) + " "
				// + tutorS2.getNombre().toUpperCase());

				list.add(c);
				Map<String, Object> parametro = new HashMap<String, Object>();
				nombre = "DocumentoEntrega-Recepción-" + estu.getId() + "-" + estu1.getId();
				parametro.put("logoUnidadAcademica", ett.getCarrera().getUnidadAcademica().getId());
				reporteService.generarReportePDF(list, parametro, "ConstanciaDeEntregaUMMOGTrabajoTitulacion1", nombre);
			}
		}
	}

	public void reportePoratadaContraportadaComplexivo() {
		List<Caratula> list = new ArrayList<Caratula>();
		EstudianteExamenComplexivoPP tp = temaPracticoService.obtenerObjeto(
				"select tp from TemaPractico tp inner join fetch tp.estudiantePeriodo ep "
						+ "inner join ep.estudiante u inner join ep.periodoExamen pe inner join pe.carrera c "
						+ "where u.email=?1 and c.id=?2 order by ep.id desc",
				new Object[] { emailUsuario, carreraCaratulaComplexivo }, false, new Object[] {});
		if (tp == null || tp.getId() == null) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene tema práctico");
		} else if (tp.getFechaResolucion() == null || tp.getResolucion() == null) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR,
					"No tiene una resolución de Consejo Directivo, por favor acérquese a la UMMOG");
		} else if (tp.getTituloInvestigacion() == null || tp.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene el título del caso práctico");
		} else {
			Caratula c = new Caratula();
			// c.setUnidadAcademica(
			// tp.getEstudiantePeriodo().getPeriodoExamen().getCarrera().getUnidadAcademica().getNombre());
			// c.setCarrera(tp.getEstudiantePeriodo().getPeriodoExamen().getCarrera().getNombre());
			c.setTitulo(tp.getTituloInvestigacion().toUpperCase());
			Usuario estu = null;
			// Usuario estu = tp.getEstudiantePeriodo().getEstudiante();
			c.setGenero(estu.getGenero().compareToIgnoreCase("M") == 0 ? true : false);
			MallaCurricular mc = mallaCurricularService.obtenerVigentePorAlumno(carreraCaratulaComplexivo,
					tp.getFechaInicioClase());
			Carrera ca = carreraService.obtenerObjeto("select c from Carrera where c.id=?1",
					new Object[] { carreraCaratulaComplexivo }, false, new Object[0]);
			c.setGradoAcademico("TRABAJO PRÁCTICO DEL EXAMEN COMPLEXIVO PREVIO A LA OBTENCIÓN DEL TÍTULO DE "
					+ (c.isGenero() == true
							? ca.getNomenclaturaTituloMasculino() + " " + ca.getTipoTitulo() + " "
									+ ca.getDetalleTitulo()
							: ca.getNomenclaturaTituloFemenino() + " " + ca.getTipoTitulo() + " "
									+ ca.getDetalleTitulo()));

			// c.setAutor1(estu.getId() + "¬"
			// + (estu.getApellidoPaterno() == null ? "" :
			// estu.getApellidoPaterno().toUpperCase()) + " "
			// + (estu.getApellidoMaterno() == null ? "" :
			// estu.getApellidoMaterno().toUpperCase()) + " "
			// + estu.getNombre().toUpperCase());

			c.setLugarMesAnio("MACHALA - EL ORO");

			c.setFechaSustentacion("Machala, " + fechaFormatoString(new Date(), "dd 'de' MMMM 'de' yyyy"));

			list.add(c);
			Map<String, Object> parametro = new HashMap<String, Object>();
			reporteService.generarReportePDF(list, parametro, "PortadaComplexivo", "PortadaComplexivo-" + estu.getId());
		}
	}

	public void reportePoratadaContraportadaTrabajoTitulacion() {
		List<Caratula> list = new ArrayList<Caratula>();
		List<EstudianteTrabajoTitulacion> listETT = estudianteTrabajoTitulacionService.obtenerPorSql(
				"SELECT ett.* FROM exetasi.\"estudiantesTrabajosTitulacion\" ett "
						+ "inner join exetasi.usuarios u on (ett.estudiante=u.id) "
						+ "where (SELECT ett1.\"seminarioTrabajoTitulacion\" "
						+ "FROM exetasi.\"estudiantesTrabajosTitulacion\" ett1 "
						+ "inner join exetasi.carreras c on (ett1.carrera=c.id) "
						+ "inner join exetasi.usuarios e on (ett1.estudiante=e.id) where e.email='" + emailUsuario
						+ "' and c.id=" + carreraCaratulaTrabajoTitulacion + ")=ett.\"seminarioTrabajoTitulacion\" "
						+ "order by u.\"apellidoPaterno\", u.\"apellidoMaterno\", u.nombre;",
				EstudianteTrabajoTitulacion.class);
		if (listETT.size() == 1) {
			EstudianteTrabajoTitulacion ett = listETT.get(0);
			if (ett.getFechaResolucion() == null || ett.getResolucion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene una resolución");
			} else if (ett.getSeminarioTrabajoTitulacion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No esta en un seminario");
			} else {
				Caratula c = new Caratula();
				c.setUnidadAcademica(ett.getCarrera().getUnidadAcademica().getNombre());
				c.setCarrera(ett.getCarrera().getNombre());
				c.setTitulo(ett.getTituloInvestigacion().toUpperCase());
				Usuario estu = ett.getEstudiante();
				c.setGenero(estu.getGenero().compareToIgnoreCase("M") == 0 ? true : false);
				MallaCurricular mc = mallaCurricularService.obtenerVigentePorAlumno(carreraCaratulaTrabajoTitulacion,
						ett.getFechaInicioClase());
				Carrera ca = carreraService.obtenerObjeto("select c from Carrera where c.id=?1",
						new Object[] { carreraCaratulaComplexivo }, false, new Object[0]);
				c.setGradoAcademico("TRABAJO DE TITULACIÓN PREVIO A LA OBTENCIÓN DEL TÍTULO DE " + (c.isGenero() == true
						? ca.getNomenclaturaTituloMasculino() + " " + ca.getTipoTitulo() + " " + ca.getDetalleTitulo()
						: ca.getNomenclaturaTituloFemenino() + " " + ca.getTipoTitulo() + " " + ca.getDetalleTitulo()));

				// c.setAutor1(estu.getId() + "¬"
				// + (estu.getApellidoPaterno() == null ? "" :
				// estu.getApellidoPaterno().toUpperCase()) + " "
				// + (estu.getApellidoMaterno() == null ? "" :
				// estu.getApellidoMaterno().toUpperCase()) + " "
				// + estu.getNombre().toUpperCase() + "¬" + estu.getEmail());

				Usuario tutor = ett.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente();
				// c.setTutor1(tutor.getId() + "¬"
				// + (tutor.getApellidoPaterno() == null ? "" :
				// tutor.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor.getApellidoMaterno() == null ? "" :
				// tutor.getApellidoMaterno().toUpperCase()) + " "
				// + tutor.getNombre().toUpperCase() + "¬" + tutor.getEmail());

				c.setLugarMesAnio("MACHALA - EL ORO");

				c.setFechaSustentacion(
						"Machala, " + fechaFormatoString(ett.getFechaSustentacion(), "dd 'de' MMMM 'de' yyyy"));

				list.add(c);
				Map<String, Object> parametro = new HashMap<String, Object>();
				reporteService.generarReportePDF(list, parametro, "PortadaTT", "PortadaTT-" + estu.getId());
			}
		} else if (listETT.size() == 2) {
			EstudianteTrabajoTitulacion ett = listETT.get(0);
			EstudianteTrabajoTitulacion ett1 = listETT.get(1);
			if (ett.getFechaResolucion() == null || ett.getResolucion() == null || ett1.getFechaResolucion() == null
					|| ett1.getResolucion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene una resolución");
			} else if (ett.getSeminarioTrabajoTitulacion() == null || ett1.getSeminarioTrabajoTitulacion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No esta en un seminario");
			} else {
				boolean bnTitulos = false;
				Caratula c = new Caratula();
				c.setUnidadAcademica(ett.getCarrera().getUnidadAcademica().getNombre());
				c.setCarrera(ett.getCarrera().getNombre());
				c.setTitulo(ett.getTituloInvestigacion().toUpperCase());
				Usuario estu = ett.getEstudiante();
				Usuario estu1 = ett1.getEstudiante();
				if (ett.getEstudiante().getGenero().compareToIgnoreCase("M") == 0
						&& ett1.getEstudiante().getGenero().compareToIgnoreCase("F") == 0) {
					estu = ett1.getEstudiante();
					estu1 = ett.getEstudiante();
					bnTitulos = true;
				} else if (ett.getEstudiante().getGenero().compareToIgnoreCase("F") == 0
						&& ett1.getEstudiante().getGenero().compareToIgnoreCase("M") == 0) {
					bnTitulos = true;
				}
				c.setGenero(estu.getGenero().compareToIgnoreCase("M") == 0 ? true : false);
				MallaCurricular mc = mallaCurricularService.obtenerVigentePorAlumno(carreraCaratulaTrabajoTitulacion,
						ett.getFechaInicioClase());
				Carrera ca = carreraService.obtenerObjeto("select c from Carrera where c.id=?1",
						new Object[] { carreraCaratulaComplexivo }, false, new Object[0]);
				c.setGradoAcademico("TRABAJO DE TITULACIÓN PREVIO A LA OBTENCIÓN DEL TÍTULO DE " + (c.isGenero() == true
						? ca.getNomenclaturaTituloMasculino() + " " + ca.getTipoTitulo() + " " + ca.getDetalleTitulo()
						: ca.getNomenclaturaTituloFemenino() + " " + ca.getTipoTitulo() + " " + ca.getDetalleTitulo()));
				if (bnTitulos)
					c.setGradoAcademico("TRABAJO DE TITULACIÓN PREVIO A LA OBTENCIÓN DEL TÍTULO DE "
							+ ca.getNomenclaturaTituloFemenino() + " " + ca.getTipoTitulo() + " "
							+ ca.getDetalleTitulo() + " Y/E " + ca.getNomenclaturaTituloMasculino() + " "
							+ ca.getTipoTitulo() + " " + ca.getDetalleTitulo());

				// c.setAutor1(estu.getId() + "¬"
				// + (estu.getApellidoPaterno() == null ? "" :
				// estu.getApellidoPaterno().toUpperCase()) + " "
				// + (estu.getApellidoMaterno() == null ? "" :
				// estu.getApellidoMaterno().toUpperCase()) + " "
				// + estu.getNombre().toUpperCase() + "¬" + estu.getEmail());
				// c.setAutor2(estu1.getId() + "¬"
				// + (estu1.getApellidoPaterno() == null ? "" :
				// estu1.getApellidoPaterno().toUpperCase()) + " "
				// + (estu1.getApellidoMaterno() == null ? "" :
				// estu1.getApellidoMaterno().toUpperCase()) + " "
				// + estu1.getNombre().toUpperCase() + "¬" + estu1.getEmail());

				Usuario tutor = ett.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente();
				// c.setTutor1(tutor.getId() + "¬"
				// + (tutor.getApellidoPaterno() == null ? "" :
				// tutor.getApellidoPaterno().toUpperCase()) + " "
				// + (tutor.getApellidoMaterno() == null ? "" :
				// tutor.getApellidoMaterno().toUpperCase()) + " "
				// + tutor.getNombre().toUpperCase() + "¬" + tutor.getEmail());

				c.setLugarMesAnio("MACHALA - EL ORO");

				c.setFechaSustentacion(
						"Machala, " + fechaFormatoString(ett.getFechaSustentacion(), "dd 'de' MMMM 'de' yyyy"));

				list.add(c);
				Map<String, Object> parametro = new HashMap<String, Object>();
				reporteService.generarReportePDF(list, parametro, "PortadaTT1",
						"PortadaTT-" + estu.getId() + "-" + estu1.getId());
			}
		}
	}

	public void setCarreraCaratulaComplexivo(Integer carreraCaratulaComplexivo) {
		this.carreraCaratulaComplexivo = carreraCaratulaComplexivo;
	}

	public void setCarreraCaratulaTrabajoTitulacion(Integer carreraCaratulaTrabajoTitulacion) {
		this.carreraCaratulaTrabajoTitulacion = carreraCaratulaTrabajoTitulacion;
	}

	public void setListCarreraCaratulaComplexivo(List<Carrera> listCarreraCaratulaComplexivo) {
		this.listCarreraCaratulaComplexivo = listCarreraCaratulaComplexivo;
	}

	public void setListCarreraCaratulaTrabajoTitulacion(List<Carrera> listCarreraCaratulaTrabajoTitulacion) {
		this.listCarreraCaratulaTrabajoTitulacion = listCarreraCaratulaTrabajoTitulacion;
	}

	public void setListCarreraComplexivo(List<Carrera> listCarreraCaratulaComplexivo) {
		this.listCarreraCaratulaComplexivo = listCarreraCaratulaComplexivo;
	}

}
