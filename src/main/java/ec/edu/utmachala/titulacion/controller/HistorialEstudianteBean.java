package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;
import ec.edu.utmachala.titulacion.entity.EstudianteHistorial;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Examen;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Tutoria;
import ec.edu.utmachala.titulacion.entity.TutoriaEC;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudianteHistorialService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPTService;
import ec.edu.utmachala.titulacion.service.ExamenService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.TutoriaECService;
import ec.edu.utmachala.titulacion.service.TutoriaService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Controller
@Scope("session")
public class HistorialEstudianteBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudianteHistorialService estudiantesHistorialService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EstudiantesExamenComplexivoPTService estudianteExamenComplexivoPTService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoPPService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private ExamenService examenService;

	@Autowired
	private TutoriaECService tutoriaECService;

	@Autowired
	private TutoriaService tutoriaTTService;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private List<EstudianteHistorial> listadoEstudiantesHistorial;
	private EstudianteHistorial estudianteHistorial;

	private List<EstudianteHistorial> listadoEstudiantesHistorialDetalle;
	private EstudianteHistorial estudianteHistorialDetalle;

	private EstudianteExamenComplexivoPT estudianteExamenComplexivoPT;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	private List<Examen> examenes;
	private List<TutoriaEC> tutoriasEC;
	private List<Tutoria> tutoriasTT;

	private boolean panelPrincipal;

	private String criterioBusquedaEstudiante;

	private boolean presentarTablaEstudianteEscogido;
	private boolean presentarTablaEC;
	private boolean presentarTablaTT;

	private Integer totalHoras;
	private Integer totalMinutos;
	private String numeroHorasTutorias;
	private Integer numeroHorasRestantes;
	private Integer numeroMinutosRestantes;

	private BigDecimal calificacionParteTeoricaFinal;

	private BigDecimal calificacionParteEscritaFinal;

	private BigDecimal calificacionParteOralFinal;

	private BigDecimal calificacionFinal;

	private String numeroTotalHorasMinutosRestantes;

	@PostConstruct
	public void a() {
		procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});

		panelPrincipal = true;

		estudianteExamenComplexivoPT = new EstudianteExamenComplexivoPT();
		estudianteExamenComplexivoPP = new EstudianteExamenComplexivoPP();
		estudianteTrabajoTitulacion = new EstudianteTrabajoTitulacion();

		presentarTablaEstudianteEscogido = false;
		presentarTablaEC = false;
		presentarTablaTT = false;

		examenes = new ArrayList<Examen>();
		tutoriasEC = new ArrayList<TutoriaEC>();
		tutoriasTT = new ArrayList<Tutoria>();

		encerar();

	}

	public void buscar() {
		if (criterioBusquedaEstudiante.isEmpty())
			System.out.println("nada");
		else
			listadoEstudiantesHistorial = estudiantesHistorialService.obtenerPorSql(
					"select distinct e.id as identificador, e.id as cedula, e.\"apellidoNombre\" as \"apellidosNombres\", c.nombre as carrera, c.id as \"carreraId\","
							+ " '' as \"opcionTitulacion\",  '' as proceso, '' as \"periodoProceso\", '2017-02-02' as \"fechaInicioProceso\","
							+ " '' as \"enlaceCronograma\", '' as \"calificacionFinal\", '' as estado from procesos p"
							+ " inner join \"estudiantesExamenComplexivoPT\" ept on (ept.proceso=p.id) inner join carreras c on (ept.carrera=c.id) inner join usuarios e on (ept.estudiante=e.id)"
							+ " inner join \"permisosCarreras\" pc on (pc.carrera=c.id)"
							+ " inner join usuarios a on (pc.usuario=a.id) where (e.id like '%"
							+ criterioBusquedaEstudiante
							+ "%' or translate (e.\"apellidoNombre\", 'ÁÉÍÓÚ', 'AEIOU')like translate('%'||upper('"
							+ criterioBusquedaEstudiante + "')||'%', 'ÁÉÍÓÚ', 'AEIOU')) and a.email='"
							+ UtilSeguridad.obtenerUsuario()
							+ "' union select distinct e.id as identificador, e.id as cedula, e.\"apellidoNombre\" as \"apellidosNombres\", c.nombre as carrera, c.id as \"carreraId\","
							+ " '' as \"opcionTitulacion\", '' as proceso, '' as \"periodoProceso\", '2017-02-02' as \"fechaInicioProceso\","
							+ " '' as \"enlaceCronograma\", '' as \"calificacionFinal\","
							+ " '' as estado from procesos p inner join \"estudiantesTrabajosTitulacion\" ett on (ett.proceso=p.id)"
							+ " inner join carreras c on (ett.carrera=c.id) inner join usuarios e on (ett.estudiante=e.id) inner join \"opcionesTitulacion\" ot on (ett.\"opcionTitulacion\"=ot.id) inner join \"permisosCarreras\" pc on (pc.carrera=c.id)"
							+ " inner join usuarios a on (pc.usuario=a.id) where(e.id like '%"
							+ criterioBusquedaEstudiante
							+ "%' or translate (e.\"apellidoNombre\", 'ÁÉÍÓÚ', 'AEIOU')like translate('%'||upper('"
							+ criterioBusquedaEstudiante + "')||'%', 'ÁÉÍÓÚ', 'AEIOU')) and a.email='"
							+ UtilSeguridad.obtenerUsuario() + "'"
							+ " order by \"apellidosNombres\", carrera, \"fechaInicioProceso\" asc",
					EstudianteHistorial.class);

		presentarTablaEstudianteEscogido = false;
		presentarTablaEC = false;
		presentarTablaTT = false;

		encerar();
	}

	public void cargarDatosEstudiante() {
		encerar();
		String opcionTitulacion = estudianteHistorialDetalle.getIdentificador().split("-")[0];
		int id = Integer.parseInt(estudianteHistorialDetalle.getIdentificador().split("-")[1]);
		if (opcionTitulacion.compareTo("EC") == 0) {
			estudianteExamenComplexivoPT = estudianteExamenComplexivoPTService.obtenerObjeto(
					"select ept from EstudianteExamenComplexivoPT ept inner join ept.carrera c inner join c.unidadAcademica ua inner join fetch ept.examenes ex inner join ept.estudiante e inner join ept.proceso p"
							+ " inner join ex.grupo gr inner join ex.tipoExamen te where ept.id=?1 ",
					new Object[] { id }, false, new Object[] {});

			examenes = new ArrayList<Examen>();
			for (Examen ex : estudianteExamenComplexivoPT.getExamenes()) {
				Examen exa = examenService.obtenerObjeto(
						"select ex from Examen ex inner join ex.grupo g inner join ex.tipoExamen te inner join fetch ex.preguntasExamenes preexa inner join preexa.pregunta pre where ex.id=?1 and preexa.respuesta=pre.sostiApantisi",
						new Object[] { ex.getId() }, false, new Object[] {});
				examenes.add(exa);
			}

			// ECPP
			String proceso = estudianteHistorialDetalle.getProceso();
			int carrera = estudianteHistorialDetalle.getCarreraId();
			String estudiante = estudianteHistorialDetalle.getCedula();
			// System.out.println("proceso: " + proceso + ", carrera: " +
			// carrera + ", estudiante: " + estudiante);
			estudianteExamenComplexivoPP = estudianteExamenComplexivoPPService.obtenerObjeto(
					"select epp from EstudianteExamenComplexivoPP epp left join epp.estudiante e inner join epp.proceso p inner join epp.carrera c left join epp.tutor t inner join epp.especialista1 e1 inner join epp.especialista2 e2 inner join epp.especialista3 e3 inner join epp.especialistaSuplente1 es1  where p.id=?1 and c.id=?2 and e.id=?3",
					new Object[] { proceso, carrera, estudiante }, false, new Object[] {});

			tutoriasEC = tutoriaECService.obtenerLista(
					"select distinct t from TutoriaEC t inner join t.estudianteExamenComplexivoPP epp where epp.id=?1",
					new Object[] { estudianteExamenComplexivoPP.getId() }, 0, false, new Object[] {});

			if (estudianteExamenComplexivoPP != null && estudianteExamenComplexivoPP.getTituloInvestigacion() != null)
				obtenerHorasTutoriasEC();

			try {
				String calificaciones = estudianteExamenComplexivoPPService
						.traerCalificaciones(estudianteExamenComplexivoPP);
				calificacionParteTeoricaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
				calificacionParteEscritaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
				calificacionParteOralFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);
				calificacionFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[3]);
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}

			presentarTablaEC = true;
			presentarTablaTT = false;

		} else {
			estudianteTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.carrera c inner join c.unidadAcademica ua inner join ett.especialista1 e1 inner join ett.especialista2 e2 inner join ett.especialista3 e3"
							+ " inner join ett.especialistaSuplente1 es1 inner join ett.opcionTitulacion ot inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt inner join stt.docenteSeminario ds"
							+ " inner join ds.docente d where ett.id=?1",
					new Object[] { id }, false, new Object[] {});

			tutoriasTT = tutoriaTTService.obtenerLista(
					"select t from Tutoria t inner join t.estudianteTrabajoTitulacion ett where ett.id=?1",
					new Object[] { id }, 0, false, new Object[] {});

			if (estudianteTrabajoTitulacion != null && estudianteTrabajoTitulacion.getTituloInvestigacion() != null)
				obtenerHorasTutoriasTT();

			try {
				String calificaciones = estudianteTrabajoTitulacionService
						.traerCalificaciones(estudianteTrabajoTitulacion);
				calificacionParteEscritaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
				calificacionParteOralFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
				calificacionFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}

			presentarTablaEC = false;
			presentarTablaTT = true;

		}

		presentarTablaEstudianteEscogido = true;

	}

	public void encerar() {
		numeroHorasTutorias = "";
		calificacionParteTeoricaFinal = new BigDecimal(0);
		calificacionParteEscritaFinal = new BigDecimal(0);
		calificacionParteOralFinal = new BigDecimal(0);
		calificacionFinal = new BigDecimal(0);
		tutoriasEC = new ArrayList<TutoriaEC>();
		tutoriasTT = new ArrayList<Tutoria>();
	}

	public BigDecimal getCalificacionFinal() {
		return calificacionFinal;
	}

	public BigDecimal getCalificacionParteEscritaFinal() {
		return calificacionParteEscritaFinal;
	}

	public BigDecimal getCalificacionParteOralFinal() {
		return calificacionParteOralFinal;
	}

	public BigDecimal getCalificacionParteTeoricaFinal() {
		return calificacionParteTeoricaFinal;
	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public String getCriterioBusquedaEstudiante() {
		return criterioBusquedaEstudiante;
	}

	public EstudianteExamenComplexivoPP getEstudianteExamenComplexivoPP() {
		return estudianteExamenComplexivoPP;
	}

	public EstudianteExamenComplexivoPT getEstudianteExamenComplexivoPT() {
		return estudianteExamenComplexivoPT;
	}

	public EstudianteHistorial getEstudianteHistorial() {
		return estudianteHistorial;
	}

	public EstudianteHistorial getEstudianteHistorialDetalle() {
		return estudianteHistorialDetalle;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public List<Examen> getExamenes() {
		return examenes;
	}

	public List<EstudianteHistorial> getListadoEstudiantesHistorial() {
		return listadoEstudiantesHistorial;
	}

	public List<EstudianteHistorial> getListadoEstudiantesHistorialDetalle() {
		return listadoEstudiantesHistorialDetalle;
	}

	public String getNumeroHorasTutorias() {
		return numeroHorasTutorias;
	}

	public String getProceso() {
		return proceso;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public List<TutoriaEC> getTutoriasEC() {
		return tutoriasEC;
	}

	public List<Tutoria> getTutoriasTT() {
		return tutoriasTT;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public boolean isPresentarTablaEC() {
		return presentarTablaEC;
	}

	public boolean isPresentarTablaEstudianteEscogido() {
		return presentarTablaEstudianteEscogido;
	}

	public boolean isPresentarTablaTT() {
		return presentarTablaTT;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			// System.out.println("Indica retorno de valor.");
		} else {
			// System.out.println("He ingresado desde un método get.");
			criterioBusquedaEstudiante = "";
			if (listadoEstudiantesHistorial != null)
				listadoEstudiantesHistorial.clear();
			if (listadoEstudiantesHistorialDetalle != null)
				listadoEstudiantesHistorialDetalle.clear();
			a();
		}
	}

	public void obtenerHorasTutoriasEC() {
		totalHoras = 0;
		totalMinutos = 0;
		Calendar f1 = Calendar.getInstance();
		Calendar f2 = Calendar.getInstance();
		if (tutoriasEC.size() == 0) {
			numeroHorasTutorias = "Aún no ha brindado tutorías." + 0;
			numeroTotalHorasMinutosRestantes = "12 horas y 0 minutos.";
		} else {
			for (int i = 0; i < tutoriasEC.size(); i++) {
				f1.setTime(tutoriasEC.get(i).getFecha());
				f2.setTime(tutoriasEC.get(i).getFechaFinTutoria());

				Integer minutosTI = 0;
				Integer minutosTF = 0;
				Integer horasTI = 0;
				Integer horasTF = 0;

				minutosTI = f1.get(Calendar.MINUTE);
				minutosTF = f2.get(Calendar.MINUTE);
				horasTI = f1.get(Calendar.HOUR_OF_DAY);
				horasTF = f2.get(Calendar.HOUR_OF_DAY);

				if (minutosTF >= minutosTI) {
					totalMinutos = totalMinutos + (minutosTF - minutosTI);
				} else {
					horasTF = horasTF - 1;
					minutosTF = minutosTF + 60;
					totalMinutos = totalMinutos + (minutosTF - minutosTI);
				}
				if (totalMinutos >= 60) {
					totalHoras = totalHoras + 1;
					totalMinutos = totalMinutos - 60;
				}
				totalHoras = totalHoras + (horasTF - horasTI);
				// System.out
				// .println((i + 1) + " pasada, total horas: " + totalHoras + "
				// y total minutos: " + totalMinutos);
			}
		}
		numeroHorasTutorias = totalHoras + " horas y " + totalMinutos + " minutos.";

		Integer ht = 12;
		Integer mt = 0;
		if (mt >= totalMinutos)
			numeroMinutosRestantes = mt - totalMinutos;
		else {
			ht = ht - 1;
			mt = mt + 60;
			numeroMinutosRestantes = mt - totalMinutos;
		}
		numeroHorasRestantes = ht - totalHoras;

		numeroTotalHorasMinutosRestantes = numeroHorasRestantes <= 0 && numeroMinutosRestantes <= 0
				? "Ha cumplido con las horas de tutorías establecidas."
				: numeroHorasRestantes + " horas y " + numeroMinutosRestantes + " minutos.";
	}

	public void obtenerHorasTutoriasTT() {
		totalHoras = 0;
		totalMinutos = 0;
		Calendar f1 = Calendar.getInstance();
		Calendar f2 = Calendar.getInstance();
		if (tutoriasTT.size() == 0) {
			numeroHorasTutorias = "Aún no han brindado tutorías.";
			numeroTotalHorasMinutosRestantes = "48 horas y 0 minutos.";
		} else {
			for (int i = 0; i < tutoriasTT.size(); i++) {
				f1.setTime(tutoriasTT.get(i).getFecha());
				f2.setTime(tutoriasTT.get(i).getFechaFinTutoria());

				Integer minutosTI = 0;
				Integer minutosTF = 0;
				Integer horasTI = 0;
				Integer horasTF = 0;

				minutosTI = f1.get(Calendar.MINUTE);
				minutosTF = f2.get(Calendar.MINUTE);
				horasTI = f1.get(Calendar.HOUR_OF_DAY);
				horasTF = f2.get(Calendar.HOUR_OF_DAY);

				if (minutosTF >= minutosTI) {
					totalMinutos = totalMinutos + (minutosTF - minutosTI);
				} else {
					horasTF = horasTF - 1;
					minutosTF = minutosTF + 60;
					totalMinutos = totalMinutos + (minutosTF - minutosTI);
				}
				if (totalMinutos >= 60) {
					totalHoras = totalHoras + 1;
					totalMinutos = totalMinutos - 60;
				}
				totalHoras = totalHoras + (horasTF - horasTI);
				// System.out.println("1. totalHoras: " + totalHoras + " -
				// totalMinutos: " + totalMinutos);
			}
		}
		numeroHorasTutorias = totalHoras + " horas y " + totalMinutos + " minutos.";

		Integer ht = 48;
		Integer mt = 0;
		if (mt >= totalMinutos)
			numeroMinutosRestantes = mt - totalMinutos;
		else {
			ht = ht - 1;
			mt = mt + 60;
			numeroMinutosRestantes = mt - totalMinutos;
		}
		numeroHorasRestantes = ht - totalHoras;

		// System.out.println("1. numerohorasrestantes: " + numeroHorasRestantes
		// + " - numeroMinutosRestantes: "
		// + numeroMinutosRestantes);

		numeroTotalHorasMinutosRestantes = numeroHorasRestantes <= 0 && numeroMinutosRestantes <= 0
				? "Ha cumplido con las horas de tutorías establecidas."
				: numeroHorasRestantes + " horas y " + numeroMinutosRestantes + " minutos.";
	}

	public void onRowSelect() {
		// System.out.println("Hay que buscar de nuevo");
		String cedula = estudianteHistorial.getCedula();
		int carrera = estudianteHistorial.getCarreraId();
		listadoEstudiantesHistorialDetalle = estudiantesHistorialService.obtenerPorSql(
				"select distinct 'EC-'||ept.id as identificador, e.id as cedula, e.\"apellidoNombre\" as \"apellidosNombres\", c.nombre as carrera, c.id as \"carreraId\","
						+ " 'EXAMEN COMPLEXIVO' as \"opcionTitulacion\",  p.id as proceso, p.observacion as \"periodoProceso\", p.\"fechaInicio\" as \"fechaInicioProceso\","
						+ " p.\"enlaceCronograma\" as \"enlaceCronograma\", (case when p.\"fechaInicio\" <= now() and p.\"fechaCierre\" >= now() then 'EN PROCESO' else"
						+ "	case when obtener_calificacion_ecpt(p.id, c.id, e.id)<=0 then '0.00' else case when obtener_calificacion_ecpt(p.id, c.id, e.id) < 20 then cast(obtener_calificacion_ecpt(p.id, c.id, e.id)as text) else cast(obtener_calificacion_ecpp(p.id, c.id, e.id) as text)end end END)"
						+ " as \"calificacionFinal\", (case when p.\"fechaInicio\" <= now() and p.\"fechaCierre\" >= now() then 'EN PROCESO' else case when obtener_calificacion_ecpt(p.id, c.id, e.id)=-1"
						+ " then 'DESERTOR' else case when obtener_calificacion_ecpp(p.id, c.id, e.id)>=70 then 'APROBADO' else 'REPROBADO' end end END) as estado from procesos p"
						+ " inner join \"estudiantesExamenComplexivoPT\" ept on (ept.proceso=p.id) inner join carreras c on (ept.carrera=c.id) inner join usuarios e on (ept.estudiante=e.id)"
						+ " left join examenes ex on (ex.\"estudianteExamenComplexivoPT\"=ept.id) left join \"tiposExamenes\" te on (ex.\"tipoExamen\"=te.id) inner join \"permisosCarreras\" pc on (pc.carrera=c.id)"
						+ " inner join usuarios a on (pc.usuario=a.id) where (e.id like '%" + cedula
						+ "%' or translate (e.\"apellidoNombre\", 'ÁÉÍÓÚ', 'AEIOU') like translate('%'||upper('"
						+ cedula + "')||'%', 'ÁÉÍÓÚ', 'AEIOU')) and c.id=" + carrera + " and a.email='"
						+ UtilSeguridad.obtenerUsuario()
						+ "' union select distinct 'TT-'||ett.id as identificador, e.id as cedula, e.\"apellidoNombre\" as \"apellidosNombres\", c.nombre as carrera, c.id as \"carreraId\","
						+ " 'TRABAJO TITULACIÓN - '||ot.nombre as \"opcionTitulacion\", p.id as proceso, p.observacion as \"periodoProceso\", p.\"fechaInicio\" as \"fechaInicioProceso\","
						+ " p.\"enlaceCronograma\" as \"enlaceCronograma\", (case when p.\"fechaInicio\" <= now() and p.\"fechaCierre\" >= now() then 'EN PROCESO' else case when obtener_horas_tutorias(ett.id, 0) = '0 horas 0 minutos' then '0.00' else"
						+ " case when \"obtenerCalificacionTrabajoTitulacion\"(ett)<=0 then '0.00' else cast(\"obtenerCalificacionTrabajoTitulacion\"(ett) as text) end end end)as \"calificacionFinal\","
						+ " (case when p.\"fechaInicio\" <= now() and p.\"fechaCierre\" >= now() then 'EN PROCESO' else case when (obtener_horas_tutorias(ett.id, 0) = '0 horas 0 minutos' and ett.prorroga is null) then 'DESERTOR' else"
						+ " case when ett.prorroga is not null then 'PRORROGA' else case when \"obtenerCalificacionTrabajoTitulacion\"(ett)>=70 then 'APROBADO' else 'REPROBADO' end end end end) as estado from procesos p inner join \"estudiantesTrabajosTitulacion\" ett on (ett.proceso=p.id)"
						+ " inner join carreras c on (ett.carrera=c.id) inner join usuarios e on (ett.estudiante=e.id) inner join \"opcionesTitulacion\" ot on (ett.\"opcionTitulacion\"=ot.id) inner join \"permisosCarreras\" pc on (pc.carrera=c.id)"
						+ " inner join usuarios a on (pc.usuario=a.id) where(e.id like '%" + cedula
						+ "%' or translate (e.\"apellidoNombre\", 'ÁÉÍÓÚ', 'AEIOU') like translate('%'||upper('"
						+ cedula + "')||'%', 'ÁÉÍÓÚ', 'AEIOU')) and c.id=" + carrera + " and a.email='"
						+ UtilSeguridad.obtenerUsuario() + "'"
						+ " order by \"apellidosNombres\", carrera, \"fechaInicioProceso\" asc",
				EstudianteHistorial.class);

		presentarTablaEstudianteEscogido = true;
		presentarTablaEC = false;
		presentarTablaTT = false;

		encerar();

	}

	public void setCalificacionFinal(BigDecimal calificacionFinal) {
		this.calificacionFinal = calificacionFinal;
	}

	public void setCalificacionParteEscritaFinal(BigDecimal calificacionParteEscritaFinal) {
		this.calificacionParteEscritaFinal = calificacionParteEscritaFinal;
	}

	public void setCalificacionParteOralFinal(BigDecimal calificacionParteOralFinal) {
		this.calificacionParteOralFinal = calificacionParteOralFinal;
	}

	public void setCalificacionParteTeoricaFinal(BigDecimal calificacionParteTeoricaFinal) {
		this.calificacionParteTeoricaFinal = calificacionParteTeoricaFinal;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setCriterioBusquedaEstudiante(String criterioBusquedaEstudiante) {
		this.criterioBusquedaEstudiante = criterioBusquedaEstudiante;
	}

	public void setEstudianteExamenComplexivoPP(EstudianteExamenComplexivoPP estudianteExamenComplexivoPP) {
		this.estudianteExamenComplexivoPP = estudianteExamenComplexivoPP;
	}

	public void setEstudianteExamenComplexivoPT(EstudianteExamenComplexivoPT estudianteExamenComplexivoPT) {
		this.estudianteExamenComplexivoPT = estudianteExamenComplexivoPT;
	}

	public void setEstudianteHistorial(EstudianteHistorial estudianteHistorial) {
		this.estudianteHistorial = estudianteHistorial;
	}

	public void setEstudianteHistorialDetalle(EstudianteHistorial estudianteHistorialDetalle) {
		this.estudianteHistorialDetalle = estudianteHistorialDetalle;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setExamenes(List<Examen> examenes) {
		this.examenes = examenes;
	}

	public void setListadoEstudiantesHistorial(List<EstudianteHistorial> listadoEstudiantesHistorial) {
		this.listadoEstudiantesHistorial = listadoEstudiantesHistorial;
	}

	public void setListadoEstudiantesHistorialDetalle(List<EstudianteHistorial> listadoEstudiantesHistorialDetalle) {
		this.listadoEstudiantesHistorialDetalle = listadoEstudiantesHistorialDetalle;
	}

	public void setNumeroHorasTutorias(String numeroHorasTutorias) {
		this.numeroHorasTutorias = numeroHorasTutorias;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setPresentarTablaEC(boolean presentarTablaEC) {
		this.presentarTablaEC = presentarTablaEC;
	}

	public void setPresentarTablaEstudianteEscogido(boolean presentarTablaEstudianteEscogido) {
		this.presentarTablaEstudianteEscogido = presentarTablaEstudianteEscogido;
	}

	public void setPresentarTablaTT(boolean presentarTablaTT) {
		this.presentarTablaTT = presentarTablaTT;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	public void setTutoriasEC(List<TutoriaEC> tutoriasEC) {
		this.tutoriasEC = tutoriasEC;
	}

	public void setTutoriasTT(List<Tutoria> tutoriasTT) {
		this.tutoriasTT = tutoriasTT;
	}

}
