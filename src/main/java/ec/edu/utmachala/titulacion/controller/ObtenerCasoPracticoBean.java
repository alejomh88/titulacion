package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.entity.PreguntaExamen;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPTService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.service.PreguntaExamenService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class ObtenerCasoPracticoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPTService estudiantePeriodoService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private EstudiantesExamenComplexivoPTService estudiantesExamenComplexivoPTService;

	@Autowired
	private PreguntaExamenService preguntaExamenService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EstudiantesExamenComplexivoPPService temaPracticoService;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	@Autowired
	private CarreraService carreraService;

	private Usuario estudiante;
	private List<EstudianteExamenComplexivoPP> temasPracticos;
	private EstudianteExamenComplexivoPP temaPractico;
	private String tituloPanel;
	private String tituloTabla;
	private boolean mostrarMensajeFaltaReactivos;
	private String procesoEstudiante;
	private int numerosProcesosEstudiante;

	private EstudianteExamenComplexivoPT ept;

	@PostConstruct
	public void a() {
		tituloPanel = "Seleccionar caso práctico";
		tituloTabla = "Ya no hay casos prácticos disponibles";

		mostrarMensajeFaltaReactivos = false;

		// estudiante = usuarioService.obtenerObjeto("select e from Usuario e
		// inner join fetch e.estudiantesExamenComplexivoPT pt where e.email=?1
		// order by pt.id desc",
		// new Object[] {
		// SecurityContextHolder.getContext().getAuthentication().getName() },
		// false,
		// new Object[] {});

		// procesoEstudiante =
		// estudiante.getEstudiantesExamenComplexivoPT().get(0).getProceso().getId();

		List<EstudianteExamenComplexivoPT> listadoEPT = estudiantesExamenComplexivoPTService.obtenerLista(
				"select ept from EstudianteExamenComplexivoPT ept inner join ept.estudiante e inner join ept.proceso p inner join ept.carrera c where e.email=?1 order by ept.id asc",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

		// System.out.println("Ept id: " + ept.getId());

		if (listadoEPT == null || listadoEPT.size() == 0) {
			tituloTabla = "Usted no está en el actual proceso de titulación.";
		} else {
			ept = listadoEPT.get(listadoEPT.size() - 1);// escoger// el
			// último
			List<PreguntaExamen> preguntaExamenes = preguntaExamenService.obtenerLista(
					"select pe from PreguntaExamen pe "
							+ "inner join pe.examen e inner join e.estudianteExamenComplexivoPT pt "
							+ "inner join pt.proceso p inner join pt.estudiante e inner join pe.pregunta pr "
							+ "where p.id=?1 and e.email=?2 and pe.respuesta=pr.sostiApantisi",
					new Object[] { ept.getProceso().getId(),
							SecurityContextHolder.getContext().getAuthentication().getName() },
					0, false, new Object[] {});

			// System.out.println("Cantidad de preguntas: " +
			// preguntaExamenes.size());
			// System.out.println("Utiltime: " + UtilsDate.timestamp());

			if (SecurityContextHolder.getContext().getAuthentication().getName()
					.compareTo("jcelleri@utomachala.edu.ec") == 0) {
				temaPractico = temaPracticoService.obtenerObjeto(
						"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e "
								+ "inner join pp.proceso p where p.id=?2 and e.email=?1",
						new Object[] { SecurityContextHolder.getContext().getAuthentication().getName(),
								ept.getProceso().getId() },
						false, new Object[] {});
				temasPracticos = new ArrayList<EstudianteExamenComplexivoPP>();
				temasPracticos.add(temaPractico);
				tituloPanel = "Usted a escogido correctamente el caso práctico";
			} else {
				if (preguntaExamenes == null || preguntaExamenes.size() < 20) {
					tituloTabla = "Usted no aprobó el examen complexivo parte teórica";
				} else {

					List<FechaProceso> fechasProcesos = fechaProcesoService.obtenerLista(
							"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='EC' and fp.fase='ESCOPP' and fp.activo is true "
									+ "and fp.nombre is not null order by fp.id asc",
							new Object[] { ept.getProceso().getId() }, 0, false, new Object[] {});

					// System.out.println("Número de fechas de la consulta
					// antes: " + fechasProcesos.size()
					// S+ ", con proceso: " + ept.getProceso().getId());

					if (fechasProcesos != null && fechasProcesos.size() != 0) {
						if ((fechasProcesos.get(0).getFechaInicio().compareTo(new Date()) > 0
								&& fechasProcesos.get(0).getFechaFinal().compareTo(new Date()) > 0)
								|| (fechasProcesos.get(0).getFechaInicio().compareTo(new Date()) < 0)
										&& (fechasProcesos.get(0).getFechaFinal().compareTo(new Date()) < 0)) {
							List<FechaProceso> fechasProcesosAux = fechaProcesoService.obtenerLista(
									"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and fp.modalidad='EC' and fp.fase='ESCOPP' and "
											+ "fp.activo='true' and c.id=?2 order by fp.id asc",
									new Object[] { ept.getProceso().getId(), ept.getCarrera().getId() }, 0, false,
									new Object[] {});
							// System.out.println("Número de fechas de la
							// consulta auxiliar: " + fechasProcesosAux.size());
							if (fechasProcesosAux != null && fechasProcesosAux.size() != 0) {
								fechasProcesos.clear();
								fechasProcesos.addAll(fechasProcesosAux);
							}
						}
					} else {
						List<FechaProceso> fechasProcesosAux = fechaProcesoService.obtenerLista(
								"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and fp.modalidad='EC' and fp.fase='ESCOPP' and "
										+ "fp.activo='true' and c.id=?2 order by fp.id asc",
								new Object[] { ept.getProceso().getId(), ept.getCarrera().getId() }, 0, false,
								new Object[] {});
						// SSystem.out.println("Número de fechas de la consulta
						// auxiliar: " + fechasProcesosAux.size());
						if (fechasProcesosAux != null && fechasProcesosAux.size() != 0) {
							fechasProcesos.clear();
							fechasProcesos.addAll(fechasProcesosAux);
						}
					}

					// System.out.println("Número de fechas de la consulta
					// despues: " + fechasProcesos.size());

					if (fechasProcesos != null && fechasProcesos.size() != 0) {
						if (fechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
								&& fechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

							temaPractico = temaPracticoService.obtenerObjeto(
									"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e "
											+ "inner join pp.proceso p where e.email=?1 and p.id=?2",
									new Object[] { UtilSeguridad.obtenerUsuario(), ept.getProceso().getId() }, false,
									new Object[] {});

							if (temaPractico != null) {
								temasPracticos = new ArrayList<EstudianteExamenComplexivoPP>();
								temasPracticos.add(temaPractico);
								tituloPanel = "Usted a escogido correctamente el caso práctico";
							} else {

								Carrera c = carreraService.obtenerObjeto(
										"select c from Carrera c inner join fetch c.estudiantesExamenComplexivoPT pt inner join pt.proceso p inner join pt.estudiante e where p.id=?1 and e.email=?2",
										new Object[] { ept.getProceso().getId(), UtilSeguridad.obtenerUsuario() },
										false, new Object[0]);
								temasPracticos = temaPracticoService.obtenerLista(
										"select distinct pp from EstudianteExamenComplexivoPP pp inner join pp.docenteAsignatura da "
												+ "inner join da.asignatura a inner join a.mallaCurricular mc inner join mc.carreraMallaProceso cmp inner join cmp.carrera c "
												+ "where pp.activo=true and a.activo is true and pp.estudiante is null and c.id=?1",
										new Object[] { c.getId() }, 0, false, new Object[] {});
								mostrarMensajeFaltaReactivos = (temasPracticos == null || temasPracticos.size() == 0
										? true
										: false);
							}
						} else if (fechasProcesos.get(0).getFechaInicio().compareTo(new Date()) >= 0
								&& fechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

							if (temasPracticos != null)
								temasPracticos.clear();

							tituloTabla = "Aún no estamos en la fecha de inicio para escoger el reactivo práctico. Por favor sea paciente e ingrese en los días correspondientes al cronograma del proceso de titulación.";
						} else if (fechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
								&& fechasProcesos.get(0).getFechaFinal().compareTo(new Date()) <= 0) {
							if (temasPracticos != null)
								temasPracticos.clear();
							tituloTabla = "La fecha para escoger el reactivo práctico ha caducado. Si es que usted ha escogido el reactivo práctico, puede observar los detalles en el Dashboard, si es que usted no ha escogido el reactivo práctico y ha aprobado la parte teórica del examen complexivo, acerquése al coordinador de carrera e indíque el inconveniente.";
						}
					} else {
						if (temasPracticos != null) {
							temasPracticos.clear();
						}
						tituloTabla = "Aún no estamos en la fecha de inicio para escoger el reactivo práctico. Por favor sea paciente e ingrese para escoger los días correspondientes.";
					}

				}
			}
		}

	}

	public Usuario getEstudiante() {
		return estudiante;
	}

	public EstudiantesExamenComplexivoPTService getEstudiantePeriodoService() {
		return estudiantePeriodoService;
	}

	public EstudianteExamenComplexivoPP getTemaPractico() {
		return temaPractico;
	}

	public EstudiantesExamenComplexivoPPService getTemaPracticoService() {
		return temaPracticoService;
	}

	public List<EstudianteExamenComplexivoPP> getTemasPracticos() {
		return temasPracticos;
	}

	public String getTituloPanel() {
		return tituloPanel;
	}

	public String getTituloTabla() {
		return tituloTabla;
	}

	public UsuarioService getUsuarioService() {
		return usuarioService;
	}

	public boolean isMostrarMensajeFaltaReactivos() {
		return mostrarMensajeFaltaReactivos;
	}

	public void obtenerCasoPractico() {
		EstudianteExamenComplexivoPP tp = temaPracticoService.obtenerObjeto(
				"select pp from EstudianteExamenComplexivoPP pp where pp.estudiante is null and pp.id=?1",
				new Object[] { temaPractico.getId() }, false, new Object[] {});

		if (tp != null) {
			Usuario e = usuarioService.obtenerObjeto("select e from Usuario e where e.email=?1",
					new Object[] { SecurityContextHolder.getContext().getAuthentication().getName() }, false,
					new Object[] {});
			// temaPractico.setEstudiante(estudiante);
			temaPractico.setEstudiante(e);
			Proceso p = procesoService.obtenerObjeto("select p from Proceso p where p.id=?1",
					new Object[] { ept.getProceso().getId() }, false, new Object[0]);
			temaPractico.setProceso(p);
			Carrera c = carreraService.obtenerObjeto(
					"select c from Carrera c inner join fetch c.estudiantesExamenComplexivoPT pt inner join pt.proceso p inner join pt.estudiante e where p.fechaInicio<=?1 and p.fechaCierre>=?1 and e.email=?2",
					new Object[] { UtilsDate.timestamp(), UtilSeguridad.obtenerUsuario() }, false, new Object[0]);
			temaPractico.setCarrera(c);
			// System.out.println("Proceso: " + p.getId());
			estudiantesExamenComplexivoPPService.actualizar(temaPractico);

			temasPracticos = new ArrayList<EstudianteExamenComplexivoPP>();
			temasPracticos.add(temaPractico);

			EstudianteExamenComplexivoPP epp = estudiantesExamenComplexivoPPService.obtenerObjeto(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.estudiante e inner join epp.proceso p inner join epp.carrera c where p.id=?1 and c.id=?2 and e.email=?3",
					new Object[] { temaPractico.getProceso().getId(), temaPractico.getCarrera().getId(),
							SecurityContextHolder.getContext().getAuthentication().getName() },
					false, new Object[0]);

			List<EstudianteExamenComplexivoPT> lept = new ArrayList<EstudianteExamenComplexivoPT>();
			lept = estudiantesExamenComplexivoPTService.obtenerLista(
					"select ept from EstudianteExamenComplexivoPT ept inner join ept.proceso p inner join ept.estudiante e inner join ept.carrera c left join fetch ept.examenes ex where p.id=?1 and e.email=?2 and c.id=?3",
					new Object[] { epp.getProceso().getId(),
							SecurityContextHolder.getContext().getAuthentication().getName(),
							epp.getCarrera().getId() },
					0, false, new Object[0]);

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Este caso práctico fue seleccionado con éxito. Por tu seguridad cierre la sesión y vuelva a ingresar para confirmar la selección.");

			if (lept.get(0).getExamenes().size() == 2) {
				if (lept.get(0).getExamenes().get(0).getTipoExamen().getId().compareTo("GR") == 0)
					usuarioService.actualizarSQL(
							"select exetasi.\"obtenerCalificacionECPT_Actualizar\"(\'" + epp.getProceso().getId()
									+ "\'," + epp.getCarrera().getId() + ",\'" + epp.getEstudiante().getId() + "\',\'"
									+ lept.get(0).getExamenes().get(0).getTipoExamen().getId() + "\');");
				else
					usuarioService.actualizarSQL(
							"select exetasi.\"obtenerCalificacionECPT_Actualizar\"(\'" + epp.getProceso().getId()
									+ "\'," + epp.getCarrera().getId() + ",\'" + epp.getEstudiante().getId() + "\',\'"
									+ lept.get(0).getExamenes().get(1).getTipoExamen().getId() + "\');");

			} else
				usuarioService.actualizarSQL_Actualizar(
						"select exetasi.\"obtenerCalificacionECPT_Actualizar\"(\'" + epp.getProceso().getId() + "\',"
								+ epp.getCarrera().getId() + ",\'" + epp.getEstudiante().getId() + "\',\'"
								+ lept.get(0).getExamenes().get(0).getTipoExamen().getId() + "\');");

		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Este caso práctico ya fue escogido, prueba con otro caso");

			temasPracticos = temaPracticoService.obtenerLista(
					"select pp from EstudianteExamenComplexivoPP pp inner join pp.docenteAsignatura da "
							+ "inner join da.asignatura a inner join a.mallaCurricular mc "
							+ "inner join mc.carrera c where pp.activo=true and pp.estudiante is null and c.id=?1 order by a.nombre",
					new Object[] { estudiante.getEstudiantesExamenComplexivoPT().get(0).getCarrera().getId() }, 0,
					false, new Object[] {});
		}
		a();

	}

	public void setEstudiante(Usuario estudiante) {
		this.estudiante = estudiante;
	}

	public void setEstudiantePeriodoService(EstudiantesExamenComplexivoPTService estudiantePeriodoService) {
		this.estudiantePeriodoService = estudiantePeriodoService;
	}

	public void setMostrarMensajeFaltaReactivos(boolean mostrarMensajeFaltaReactivos) {
		this.mostrarMensajeFaltaReactivos = mostrarMensajeFaltaReactivos;
	}

	public void setTemaPractico(EstudianteExamenComplexivoPP temaPractico) {
		this.temaPractico = temaPractico;
	}

	public void setTemaPracticoService(EstudiantesExamenComplexivoPPService temaPracticoService) {
		this.temaPracticoService = temaPracticoService;
	}

	public void setTemasPracticos(List<EstudianteExamenComplexivoPP> temasPracticos) {
		this.temasPracticos = temasPracticos;
	}

	public void setTituloPanel(String tituloPanel) {
		this.tituloPanel = tituloPanel;
	}

	public void setTituloTabla(String tituloTabla) {
		this.tituloTabla = tituloTabla;
	}

	public void setUsuarioService(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

}