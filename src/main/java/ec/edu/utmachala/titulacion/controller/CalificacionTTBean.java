package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

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

import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class CalificacionTTBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteTrabajoTitulacionService estudiantesTrabajoTitulacionService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	private List<FechaProceso> listadoFechasProcesos;

	private List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajoTitulacion;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	private Boolean panelPrincipal;
	private Boolean panelEscrita;
	private Boolean panelOral;

	private Integer paginaActual;

	private String user;

	@Digits(integer = 4, fraction = 2, message = "Debe ingresar máximo 2 decimales (68.89)")
	@Min(value = 0, message = "La calificación mínima es 0.00")
	@Max(value = 70, message = "La calificación máxima es 70.00")
	private BigDecimal calificacion;

	@Digits(integer = 4, fraction = 2, message = "Debe ingresar máximo 2 decimales (28.89)")
	@Min(value = 0, message = "La calificación mínima es 0.00")
	@Max(value = 30, message = "La calificación máxima es 30.00")
	private BigDecimal calificacionOralOrdinaria;

	private boolean calificacionEscritaDentroFecha;
	private boolean calificacionOralDentroFecha;

	@PostConstruct
	public void a() {
		calificacion = new BigDecimal("0");

		panelPrincipal = true;
		panelEscrita = false;
		panelOral = false;

		user = SecurityContextHolder.getContext().getAuthentication().getName();

		listadoEstudiantesTrabajoTitulacion = estudiantesTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p "
						+ "inner join ett.carrera c inner join ett.estudiante e inner join ett.opcionTitulacion ot "
						+ "inner join ett.especialista1 e1 inner join ett.especialista2 e2 "
						+ "inner join ett.especialista3 e3 inner join ett.especialistaSuplente1 s1 "
						+ "where (p.fechaInicio<=?1 and p.fechaCierre>=?1) and (e1.email=?2 or e2.email=?2 or e3.email=?2 or s1.email=?2)"
						+ "order by c.nombre, e.apellidoNombre",
				new Object[] { UtilsDate.timestamp(),
						SecurityContextHolder.getContext().getAuthentication().getName() },
				0, false, new Object[0]);

		calificacionEscritaDentroFecha = false;
		calificacionOralDentroFecha = false;
	}

	public void actualizar() {

		if (panelEscrita) {
			if (estudianteTrabajoTitulacion.getEspecialista1().getEmail().compareTo(user) == 0)
				estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee1="
						+ calificacion + " WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");
			else if (estudianteTrabajoTitulacion.getEspecialista2().getEmail().compareTo(user) == 0)
				estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee2="
						+ calificacion + " WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");
			else if (estudianteTrabajoTitulacion.getEspecialista3().getEmail().compareTo(user) == 0)
				estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee3="
						+ calificacion + " WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");
			else if (estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail().compareTo(user) == 0)
				estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET es1="
						+ calificacion + " WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");

			EstudianteTrabajoTitulacion ett2 = estudiantesTrabajoTitulacionService.obtenerObjeto(
					"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e inner join tt.seminarioTrabajoTitulacion stt where stt.id=?1 and e.id!=?2",
					new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
							estudianteTrabajoTitulacion.getEstudiante().getId() },
					false, new Object[] {});

			if (ett2 != null) {
				if (ett2.getEspecialista1().getEmail().compareTo(user) == 0) {
					System.out.println("Ett2: " + ett2.getId());
					estudiantesTrabajoTitulacionService
							.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee1=" + calificacion
									+ " WHERE id=" + ett2.getId() + ";");
				} else if (ett2.getEspecialista2().getEmail().compareTo(user) == 0)
					estudiantesTrabajoTitulacionService
							.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee2=" + calificacion
									+ " WHERE id=" + ett2.getId() + ";");
				else if (ett2.getEspecialista3().getEmail().compareTo(user) == 0)
					estudiantesTrabajoTitulacionService
							.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee3=" + calificacion
									+ " WHERE id=" + ett2.getId() + ";");
				else if (ett2.getEspecialistaSuplente1().getEmail().compareTo(user) == 0)
					estudiantesTrabajoTitulacionService
							.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET es1=" + calificacion
									+ " WHERE id=" + ett2.getId() + ";");
			}

			panelPrincipal = true;
			panelEscrita = false;
			panelOral = false;

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La información fue ingresada de manera correcta");
		} else if (panelOral) {

			if (estudianteTrabajoTitulacion.getEspecialista1().getEmail().compareTo(user) == 0)
				estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET oe1="
						+ calificacionOralOrdinaria + " WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");
			else if (estudianteTrabajoTitulacion.getEspecialista2().getEmail().compareTo(user) == 0)
				estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET oe2="
						+ calificacionOralOrdinaria + " WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");
			else if (estudianteTrabajoTitulacion.getEspecialista3().getEmail().compareTo(user) == 0)
				estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET oe3="
						+ calificacionOralOrdinaria + " WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");
			else if (estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail().compareTo(user) == 0)
				estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET os1="
						+ calificacionOralOrdinaria + " WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");

			panelPrincipal = true;
			panelEscrita = false;
			panelOral = false;

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La información fue ingresada de manera correcta");
		}
		a();
	}

	public void establecerPanelPrincipal() {
		panelPrincipal = true;
		panelEscrita = false;
		panelOral = false;
	}

	public BigDecimal getCalificacion() {
		return calificacion;
	}

	public BigDecimal getCalificacionOralOrdinaria() {
		return calificacionOralOrdinaria;
	}

	public EstudianteTrabajoTitulacionService getEstudiantesTrabajoTitulacionService() {
		return estudiantesTrabajoTitulacionService;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public List<EstudianteTrabajoTitulacion> getListadoEstudiantesTrabajoTitulacion() {
		return listadoEstudiantesTrabajoTitulacion;
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
		if (estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion() == null) {
			a();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El estudiante no tiene asignado un tema de investigación por parte del tutor");
		} else if (estudianteTrabajoTitulacion.getAprobado() == null) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante aún no ha sido aprobado por el tutor.");
		} else if (estudianteTrabajoTitulacion.getOpcionTitulacion().getNombre()
				.compareToIgnoreCase("ENSAYOS O ÁRTICULOS ACADÉMICOS") == 0) {
			a();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La calificación escrita se registra automáticamente en cumplimiento con el artículo 30 de la guía para la instrumentalización del sistema de titulación.");
		} else if (estudianteTrabajoTitulacion.getAntiplagio() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante aún no tiene registrado el porcentaje de coincidencia Urkund.");
			a();
		} else if (estudianteTrabajoTitulacion.getAntiplagio().compareTo(new BigDecimal(10)) == 1
				|| estudianteTrabajoTitulacion.getAntiplagio().compareTo(new BigDecimal(0)) == -1) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante tiene un porcentaje coincidencia Urkund mayor al reglamentado.");
			a();
		} else if (estudianteTrabajoTitulacion.getCitas1() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante aún no tiene registrado el número de citas papers.");
			a();
		} else if (estudianteTrabajoTitulacion.getCitas1().compareTo(new Integer(25)) == -1) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante tiene registrado un número de citas papers menor al reglamentado.");
			a();
		} else if (estudianteTrabajoTitulacion.getValidarDocumentoE3() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El trabajo escrito del estudiante aún no tiene la validación de la estructura del documento.");
			a();
		} else if (!verificarDentroFecha()) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede ingresar calificación por que se encuentra fuera de las fechas determinadas en hoja de ruta.");
		} else {
			obtenerCalificacionEscrita();
			panelPrincipal = false;
			panelEscrita = true;
			panelOral = false;
		}

	}

	public void insertarOral() {
		listadoFechasProcesos = fechaProcesoService.obtenerLista(
				"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='TT' and fp.fase='CALORA' and fp.activo='true' order by fp.id asc",
				new Object[] { estudianteTrabajoTitulacion.getProceso().getId() }, 0, false, new Object[0]);

		if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
			if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
					&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
				calificacionOralDentroFecha = true;
				System.out.println("General Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio()
						+ " - fecha fin: " + listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: "
						+ new Date() + " - valor bool: " + calificacionOralDentroFecha);
			} else {
				listadoFechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='TT' and fp.fase='CALORA' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteTrabajoTitulacion.getProceso().getId(),
								estudianteTrabajoTitulacion.getCarrera().getId() },
						0, false, new Object[0]);

				if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
					if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
							&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
						calificacionOralDentroFecha = true;
						System.out.println("Carrera || Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio()
								+ " - fecha fin: " + listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: "
								+ new Date() + " - valor bool: " + calificacionOralDentroFecha);
					} else {
						calificacionOralDentroFecha = false;
					}
				} else {
					calificacionOralDentroFecha = false;
				}
			}
		} else
			calificacionOralDentroFecha = false;

		if (calificacionOralDentroFecha) {
			obtenerCalificacionEscrita();
			if (estudianteTrabajoTitulacion.getEspecialista1().getEmail().compareTo(user) == 0)
				calificacionOralOrdinaria = estudianteTrabajoTitulacion.getOe1();
			else if (estudianteTrabajoTitulacion.getEspecialista2().getEmail().compareTo(user) == 0)
				calificacionOralOrdinaria = estudianteTrabajoTitulacion.getOe2();
			else if (estudianteTrabajoTitulacion.getEspecialista3().getEmail().compareTo(user) == 0)
				calificacionOralOrdinaria = estudianteTrabajoTitulacion.getOe3();
			else if (estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail().compareTo(user) == 0)
				calificacionOralOrdinaria = estudianteTrabajoTitulacion.getOs1();

			panelPrincipal = false;
			panelEscrita = false;
			panelOral = true;
		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede ingresar calificación por que se encuentra fuera de las fechas determinadas en hoja de ruta.");
		}
	}

	public boolean isCalificacionEscritaDentroFecha() {
		return calificacionEscritaDentroFecha;
	}

	public boolean isCalificacionOralDentroFecha() {
		return calificacionOralDentroFecha;
	}

	public void obtenerCalificacionEscrita() {
		if (estudianteTrabajoTitulacion.getEspecialista1().getEmail().compareTo(user) == 0)
			calificacion = estudianteTrabajoTitulacion.getEe1();
		else if (estudianteTrabajoTitulacion.getEspecialista2().getEmail().compareTo(user) == 0)
			calificacion = estudianteTrabajoTitulacion.getEe2();
		else if (estudianteTrabajoTitulacion.getEspecialista3().getEmail().compareTo(user) == 0)
			calificacion = estudianteTrabajoTitulacion.getEe3();
		else if (estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail().compareTo(user) == 0)
			calificacion = estudianteTrabajoTitulacion.getEs1();
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

	public void setCalificacionOralOrdinaria(BigDecimal calificacionOralOrdinaria) {
		this.calificacionOralOrdinaria = calificacionOralOrdinaria;
	}

	public void setEstudiantesTrabajoTitulacionService(
			EstudianteTrabajoTitulacionService estudiantesTrabajoTitulacionService) {
		this.estudiantesTrabajoTitulacionService = estudiantesTrabajoTitulacionService;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setListadoEstudiantesTrabajoTitulacion(
			List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajoTitulacion) {
		this.listadoEstudiantesTrabajoTitulacion = listadoEstudiantesTrabajoTitulacion;
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

	public boolean verificarDentroFecha() {
		listadoFechasProcesos = fechaProcesoService.obtenerLista(
				"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='TT' and fp.fase='CALESC' and fp.activo='true' order by fp.id asc",
				new Object[] { estudianteTrabajoTitulacion.getProceso().getId() }, 0, false, new Object[0]);

		if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
			if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
					&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
				calificacionEscritaDentroFecha = true;
				System.out.println("General Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio()
						+ " - fecha fin: " + listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: "
						+ new Date() + " - valor bool: " + calificacionEscritaDentroFecha);
			} else {
				listadoFechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='TT' and fp.fase='CALESC' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteTrabajoTitulacion.getProceso().getId(),
								estudianteTrabajoTitulacion.getCarrera().getId() },
						0, false, new Object[0]);

				if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
					if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
							&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
						calificacionEscritaDentroFecha = true;
					} else {
						calificacionEscritaDentroFecha = false;
					}
					System.out.println("Carrera || Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio()
							+ " - fecha fin: " + listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: "
							+ new Date() + " - valor bool: " + calificacionEscritaDentroFecha);
				} else {
					calificacionEscritaDentroFecha = false;
				}
			}
		} else
			calificacionEscritaDentroFecha = false;
		return calificacionEscritaDentroFecha;
	}

}