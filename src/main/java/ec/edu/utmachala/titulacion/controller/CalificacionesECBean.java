package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.PageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class CalificacionesECBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	private List<FechaProceso> listadoFechasProcesos;

	private List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

	private Boolean panelPrincipal;
	private Boolean panelEscrita;
	private Boolean panelOral;

	private Integer paginaActual;

	private String user;

	@Digits(integer = 4, fraction = 2, message = "Debe ingresar máximo 2 decimales (18.89)")
	@Min(value = 0, message = "La calificación mínima es 0.00")
	@Max(value = 20, message = "La calificación máxima es 20.00")
	private BigDecimal calificacion;

	@Digits(integer = 4, fraction = 2, message = "Debe ingresar máximo 2 decimales (28.89)")
	@Min(value = 0, message = "La calificación mínima es 0.00")
	@Max(value = 30, message = "La calificación máxima es 30.00")
	private BigDecimal calificacionOralOrdinaria;

	@Digits(integer = 4, fraction = 2, message = "Debe ingresar máximo 2 decimales (28.89)")
	@Min(value = 0, message = "La calificación mínima es 0.00")
	@Max(value = 30, message = "La calificación máxima es 30.00")
	private BigDecimal calificacionOralGracia;

	private boolean calificacionEscritaDentroFecha;
	private boolean calificacionOralDentroFecha;
	private boolean calificacionOralGraciaDentroFecha;

	@PostConstruct
	public void a() {
		calificacion = new BigDecimal("0");

		panelPrincipal = true;
		panelEscrita = false;
		panelOral = false;

		user = SecurityContextHolder.getContext().getAuthentication().getName();

		listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
				"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p "
						+ "inner join epp.carrera c inner join epp.estudiante e "
						+ "inner join epp.especialista1 e1 inner join epp.especialista2 e2 "
						+ "inner join epp.especialista3 e3 inner join epp.especialistaSuplente1 s1 "
						+ "where (p.fechaInicio<=?1 and p.fechaCierre>=?1) and (e1.email=?2 or e2.email=?2 or e3.email=?2 or s1.email=?2)"
						+ "order by c.nombre, e.apellidoNombre",
				new Object[] { UtilsDate.timestamp(),
						SecurityContextHolder.getContext().getAuthentication().getName() },
				0, false, new Object[0]);

	}

	public void actualizar() {

		if (panelEscrita) {
			if (estudianteExamenComplexivoPP.getEspecialista1().getEmail().compareTo(user) == 0)
				estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ee1="
						+ calificacion + " WHERE id=" + estudianteExamenComplexivoPP.getId() + ";");
			else if (estudianteExamenComplexivoPP.getEspecialista2().getEmail().compareTo(user) == 0)
				estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ee2="
						+ calificacion + " WHERE id=" + estudianteExamenComplexivoPP.getId() + ";");
			else if (estudianteExamenComplexivoPP.getEspecialista3().getEmail().compareTo(user) == 0)
				estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ee3="
						+ calificacion + " WHERE id=" + estudianteExamenComplexivoPP.getId() + ";");
			else if (estudianteExamenComplexivoPP.getEspecialistaSuplente1().getEmail().compareTo(user) == 0)
				estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET es1="
						+ calificacion + " WHERE id=" + estudianteExamenComplexivoPP.getId() + ";");

			panelPrincipal = true;
			panelEscrita = false;
			panelOral = false;

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La información fue ingresada de manera correcta");
		} else if (panelOral) {
			if (calificacionOralOrdinaria == null && calificacionOralGracia != null) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Antes de registrar la calificación de gracia, debe ingresar la calificación ordinaria");
				calificacionOralGracia = null;
			} else {
				if (estudianteExamenComplexivoPP.getEspecialista1().getEmail().compareTo(user) == 0) {
					estudiantesExamenComplexivoPPService
							.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ooe1="
									+ calificacionOralOrdinaria + ", oge1=" + calificacionOralGracia + " WHERE id="
									+ estudianteExamenComplexivoPP.getId() + ";");
				} else if (estudianteExamenComplexivoPP.getEspecialista2().getEmail().compareTo(user) == 0) {
					estudiantesExamenComplexivoPPService
							.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ooe2="
									+ calificacionOralOrdinaria + ", oge2=" + calificacionOralGracia + " WHERE id="
									+ estudianteExamenComplexivoPP.getId() + ";");
				} else if (estudianteExamenComplexivoPP.getEspecialista3().getEmail().compareTo(user) == 0) {
					estudiantesExamenComplexivoPPService
							.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ooe3="
									+ calificacionOralOrdinaria + ", oge3=" + calificacionOralGracia + " WHERE id="
									+ estudianteExamenComplexivoPP.getId() + ";");
				} else if (estudianteExamenComplexivoPP.getEspecialistaSuplente1().getEmail().compareTo(user) == 0) {
					estudiantesExamenComplexivoPPService
							.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET oos1="
									+ calificacionOralOrdinaria + ", ogs1=" + calificacionOralGracia + " WHERE id="
									+ estudianteExamenComplexivoPP.getId() + ";");
				}

				panelPrincipal = true;
				panelEscrita = false;
				panelOral = false;

				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"La información fue ingresada de manera correcta");
			}
		}
		a();
	}

	public void establecerPanelPrincipal() {
		a();
		panelPrincipal = true;
		panelEscrita = false;
		panelOral = false;
	}

	public BigDecimal getCalificacion() {
		return calificacion;
	}

	public BigDecimal getCalificacionOralGracia() {
		return calificacionOralGracia;
	}

	public BigDecimal getCalificacionOralOrdinaria() {
		return calificacionOralOrdinaria;
	}

	public EstudianteExamenComplexivoPP getEstudianteExamenComplexivoPP() {
		return estudianteExamenComplexivoPP;
	}

	public EstudiantesExamenComplexivoPPService getEstudiantesExamenComplexivoPPService() {
		return estudiantesExamenComplexivoPPService;
	}

	public List<EstudianteExamenComplexivoPP> getListadoEstudiantesExamenComplexivoPP() {
		return listadoEstudiantesExamenComplexivoPP;
	}

	public List<FechaProceso> getListadoFechasProcesos() {
		return listadoFechasProcesos;
	}

	public Integer getPaginaActual() {
		return paginaActual;
	}

	public Boolean getPanelEscrita() {
		return panelEscrita;
	}

	public Boolean getpanelOral() {
		return panelOral;
	}

	public Boolean getPanelOral() {
		return panelOral;
	}

	public Boolean getPanelPrincipal() {
		return panelPrincipal;
	}

	public String getUser() {
		return user;
	}

	public void insertarEscrita() {
		if (estudianteExamenComplexivoPP.getAprobado() == null || estudianteExamenComplexivoPP.getAprobado() == false) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante aún no ha sido aprobado por su tutor.");
			a();
		} else if (estudianteExamenComplexivoPP.getAntiplagio() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante aún no tiene registrado el porcentaje de coincidencia Urkund.");
			a();
		} else if (estudianteExamenComplexivoPP.getAntiplagio().compareTo(new BigDecimal(10)) == 1
				|| estudianteExamenComplexivoPP.getAntiplagio().compareTo(new BigDecimal(0)) == -1) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante tiene un porcentaje coincidencia Urkund mayor al reglamentado.");
			a();
		} else if (estudianteExamenComplexivoPP.getCitas1() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante aún no tiene registrado el número de citas papers.");
			a();
		} else if (estudianteExamenComplexivoPP.getCitas1().compareTo(new Integer(10)) == -1) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante tiene registrado un número de citas papers menor al reglamentado.");
			a();
		} else if (estudianteExamenComplexivoPP.getValidarDocumentoE3() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante aún no tiene la validación de la estructura del documento.");
			a();
		} else {
			listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='EC' and fp.fase='CALESC' and fp.activo='true' order by fp.id asc",
					new Object[] { estudianteExamenComplexivoPP.getProceso().getId() }, 0, false, new Object[0]);

			if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
				if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
						&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
					calificacionEscritaDentroFecha = true;
				} else {
					listadoFechasProcesos = fechaProcesoService.obtenerLista(
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='CALESC' and fp.activo='true' order by fp.id asc",
							new Object[] { estudianteExamenComplexivoPP.getProceso().getId(),
									estudianteExamenComplexivoPP.getCarrera().getId() },
							0, false, new Object[0]);

					if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
						if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
								&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
							calificacionEscritaDentroFecha = true;
						} else {
							calificacionEscritaDentroFecha = false;
						}

					} else {
						calificacionEscritaDentroFecha = false;
					}
				}

			} else
				calificacionEscritaDentroFecha = false;

			if (calificacionEscritaDentroFecha) {
				obtenerCalificacionEscrita();
				panelPrincipal = false;
				panelEscrita = true;
				panelOral = false;
			} else {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No puede ingresar calificación porque se encuentra fuera de las fechas determinadas en hoja de ruta.");
				a();
			}
		}
	}

	public void insertarOral() {

		listadoFechasProcesos = fechaProcesoService.obtenerLista(
				"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='EC' and fp.fase='CALORA' and fp.activo='true' order by fp.id asc",
				new Object[] { estudianteExamenComplexivoPP.getProceso().getId() }, 0, false, new Object[0]);

		if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
			if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
					&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
				calificacionOralDentroFecha = true;
				System.out.println("Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio() + " - fecha fin: "
						+ listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: " + new Date()
						+ " - valor bool: " + calificacionOralDentroFecha);
			} else {
				listadoFechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='CALORA' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteExamenComplexivoPP.getProceso().getId(),
								estudianteExamenComplexivoPP.getCarrera().getId() },
						0, false, new Object[0]);

				if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
					if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
							&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
						calificacionOralDentroFecha = true;
						System.out.println("Trajo de la carrera || Fecha inicio: "
								+ listadoFechasProcesos.get(0).getFechaInicio() + " - fecha fin: "
								+ listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: " + new Date()
								+ " - valor bool: " + calificacionOralDentroFecha);
					} else {
						calificacionOralDentroFecha = false;
					}
				} else {
					calificacionOralDentroFecha = false;
				}
			}

		} else
			calificacionOralDentroFecha = false;

		///////////////////////////////////////////////////////////////
		listadoFechasProcesos = fechaProcesoService.obtenerLista(
				"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='EC' and fp.fase='CALGRA' and fp.activo='true' order by fp.id asc",
				new Object[] { estudianteExamenComplexivoPP.getProceso().getId() }, 0, false, new Object[0]);

		if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
			if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
					&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
				calificacionOralGraciaDentroFecha = true;
				System.out.println("Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio() + " - fecha fin: "
						+ listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: " + new Date()
						+ " - valor bool: " + calificacionOralGraciaDentroFecha);
			} else {
				listadoFechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='CALGRA' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteExamenComplexivoPP.getProceso().getId(),
								estudianteExamenComplexivoPP.getCarrera().getId() },
						0, false, new Object[0]);

				if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
					if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
							&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
						calificacionOralGraciaDentroFecha = true;
						System.out.println("Trajo de la carrera || Fecha inicio: "
								+ listadoFechasProcesos.get(0).getFechaInicio() + " - fecha fin: "
								+ listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: " + new Date()
								+ " - valor bool: " + calificacionOralGraciaDentroFecha);
					} else {
						calificacionOralGraciaDentroFecha = false;
					}
				} else {
					calificacionOralGraciaDentroFecha = false;
				}
			}

		} else
			calificacionOralGraciaDentroFecha = false;
		///////////////////////////////////////////////////////////////

		if (calificacionOralDentroFecha || calificacionOralGraciaDentroFecha) {
			obtenerCalificacionEscrita();
			if (estudianteExamenComplexivoPP.getEspecialista1().getEmail().compareTo(user) == 0) {
				calificacionOralOrdinaria = estudianteExamenComplexivoPP.getOoe1();
				calificacionOralGracia = estudianteExamenComplexivoPP.getOge1();
			} else if (estudianteExamenComplexivoPP.getEspecialista2().getEmail().compareTo(user) == 0) {
				calificacionOralOrdinaria = estudianteExamenComplexivoPP.getOoe2();
				calificacionOralGracia = estudianteExamenComplexivoPP.getOge2();
			} else if (estudianteExamenComplexivoPP.getEspecialista3().getEmail().compareTo(user) == 0) {
				calificacionOralOrdinaria = estudianteExamenComplexivoPP.getOoe3();
				calificacionOralGracia = estudianteExamenComplexivoPP.getOge3();
			} else if (estudianteExamenComplexivoPP.getEspecialistaSuplente1().getEmail().compareTo(user) == 0) {
				calificacionOralOrdinaria = estudianteExamenComplexivoPP.getOos1();
				calificacionOralGracia = estudianteExamenComplexivoPP.getOgs1();
			}
			panelPrincipal = false;
			panelEscrita = false;
			panelOral = true;
		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede ingresar calificación porque se encuentra fuera de las fechas determinadas en hoja de ruta.");
			a();
		}
	}

	public boolean isCalificacionEscritaDentroFecha() {
		return calificacionEscritaDentroFecha;
	}

	public boolean isCalificacionOralDentroFecha() {
		return calificacionOralDentroFecha;
	}

	public boolean isCalificacionOralGraciaDentroFecha() {
		return calificacionOralGraciaDentroFecha;
	}

	public void obtenerCalificacionEscrita() {
		if (estudianteExamenComplexivoPP.getEspecialista1().getEmail().compareTo(user) == 0)
			calificacion = estudianteExamenComplexivoPP.getEe1();
		else if (estudianteExamenComplexivoPP.getEspecialista2().getEmail().compareTo(user) == 0)
			calificacion = estudianteExamenComplexivoPP.getEe2();
		else if (estudianteExamenComplexivoPP.getEspecialista3().getEmail().compareTo(user) == 0)
			calificacion = estudianteExamenComplexivoPP.getEe3();
		else if (estudianteExamenComplexivoPP.getEspecialistaSuplente1().getEmail().compareTo(user) == 0)
			calificacion = estudianteExamenComplexivoPP.getEs1();
	}

	public void onPageChange(PageEvent event) {
		System.out.println("Pagina: " + ((DataTable) event.getSource()).getPage());
		this.setPaginaActual(((DataTable) event.getSource()).getPage());
	}

	public void setCalificacion(BigDecimal calificacion) {
		this.calificacion = calificacion;
	}

	public void setCalificacionEscritaDentroFecha(boolean calificacionEscritaDentroFecha) {
		this.calificacionEscritaDentroFecha = calificacionEscritaDentroFecha;
	}

	public void setCalificacionOralDentroFecha(boolean calificacionOralDentroFecha) {
		this.calificacionOralDentroFecha = calificacionOralDentroFecha;
	}

	public void setCalificacionOralGracia(BigDecimal calificacionOralGracia) {
		this.calificacionOralGracia = calificacionOralGracia;
	}

	public void setCalificacionOralGraciaDentroFecha(boolean calificacionOralGraciaDentroFecha) {
		this.calificacionOralGraciaDentroFecha = calificacionOralGraciaDentroFecha;
	}

	public void setCalificacionOralOrdinaria(BigDecimal calificacionOralOrdinaria) {
		this.calificacionOralOrdinaria = calificacionOralOrdinaria;
	}

	public void setEstudianteExamenComplexivoPP(EstudianteExamenComplexivoPP estudianteExamenComplexivoPP) {
		this.estudianteExamenComplexivoPP = estudianteExamenComplexivoPP;
	}

	public void setEstudiantesExamenComplexivoPPService(
			EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService) {
		this.estudiantesExamenComplexivoPPService = estudiantesExamenComplexivoPPService;
	}

	public void setListadoEstudiantesExamenComplexivoPP(
			List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP) {
		this.listadoEstudiantesExamenComplexivoPP = listadoEstudiantesExamenComplexivoPP;
	}

	public void setListadoFechasProcesos(List<FechaProceso> listadoFechasProcesos) {
		this.listadoFechasProcesos = listadoFechasProcesos;
	}

	public void setPaginaActual(Integer paginaActual) {
		this.paginaActual = paginaActual;
	}

	public void setPanelEscrita(Boolean panelEscrita) {
		this.panelEscrita = panelEscrita;
	}

	public void setpanelOral(Boolean panelOral) {
		this.panelOral = panelOral;
	}

	public void setPanelOral(Boolean panelOral) {
		this.panelOral = panelOral;
	}

	public void setPanelPrincipal(Boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void tener() {
		Integer ee1 = 0;
		Integer ee2 = 0;
		Integer ee3 = 0;
		Integer es1 = 0;
		Integer notaTeorica = 0;

		boolean b = (estudianteExamenComplexivoPP.getActivarSuplenteE() == true)
				? (estudianteExamenComplexivoPP.getEspecialista1() == estudianteExamenComplexivoPP
						.getEspecialistaSuplantadoE())
								? (ee2 != null && ee3 != null && es1 != null)
										? ((ee2 + ee3 + es1) / 3) + notaTeorica <= 39.5 ? true : false
										: true
								: (estudianteExamenComplexivoPP.getEspecialista2() == estudianteExamenComplexivoPP
										.getEspecialistaSuplantadoE())
												? (ee1 != null && ee3 != null && es1 != null)
														? ((ee2 + ee3 + es1) / 3) + notaTeorica <= 39.5 ? true : false
														: true
												: (estudianteExamenComplexivoPP
														.getEspecialista3() == estudianteExamenComplexivoPP
																.getEspecialistaSuplantadoE())
																		? (ee1 != null && ee2 != null && es1 != null)
																				? ((ee1 + ee2 + es1) / 3)
																						+ notaTeorica <= 39.5 ? true
																								: false
																				: true
																		: true
				: (ee1 != null && ee2 != null && ee3 != null)
						? ((ee1 + ee2 + ee3) / 3) + notaTeorica <= 39.5 ? true : false
						: true;

	}

}
