package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.CalificacionTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entityAux.Cartilla;
import ec.edu.utmachala.titulacion.service.CalificacionTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.CartillaService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Controller
@Scope("session")
public class EliminarCalificacionTrabajoTitulacionBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private CartillaService cartillaService;

	@Autowired
	private CalificacionTrabajoTitulacionService calificacionTrabajoTitulacionService;

	private List<Carrera> listCarreras;
	private Integer carrera;
	private int consultaABC;

	private List<EstudianteTrabajoTitulacion> listEstudianteTrabajoTitulaciones;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	private List<Cartilla> listCartillaEsc;
	private BigDecimal totalEsc;
	private List<Cartilla> listCartillaOral;
	private BigDecimal totalOral;

	private String consultaAI = "and (e.apellidoPaterno like 'A%' or e.apellidoPaterno like 'B%' or e.apellidoPaterno like 'C%' or e.apellidoPaterno like 'D%' or e.apellidoPaterno like 'E%' or e.apellidoPaterno like 'F%' or e.apellidoPaterno like 'G%' or e.apellidoPaterno like 'H%' or e.apellidoPaterno like 'I%') ";
	private String consultaJQ = "and (e.apellidoPaterno like 'J%' or e.apellidoPaterno like 'K%' or e.apellidoPaterno like 'L%' or e.apellidoPaterno like 'M%' or e.apellidoPaterno like 'N%' or e.apellidoPaterno like 'Ñ%' or e.apellidoPaterno like 'O%' or e.apellidoPaterno like 'P%' or e.apellidoPaterno like 'Q%') ";
	private String consultaRZ = "and (e.apellidoPaterno like 'R%' or e.apellidoPaterno like 'S%' or e.apellidoPaterno like 'T%' or e.apellidoPaterno like 'U%' or e.apellidoPaterno like 'V%' or e.apellidoPaterno like 'W%' or e.apellidoPaterno like 'X%' or e.apellidoPaterno like 'Y%' or e.apellidoPaterno like 'Z%') ";

	public void buscar() {
		listEstudianteTrabajoTitulaciones = estudianteTrabajoTitulacionService.obtenerLista(
				"select distinct ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.carrera c "
						+ "inner join ett.especialista1 esp1 inner join ett.especialista2 esp2 inner join ett.especialista3 esp3 "
						+ "left join fetch ett.calificacionesTrabajoTitulaciones cal where c.id=?1 "
						+ (consultaABC == 2 ? consultaJQ : consultaABC == 1 ? consultaAI : consultaRZ)
						+ "and ett.calificacionEscrita is not null and ett.calificacionOral is not null and ett.validarCalificacion is null "
						+ "order by ett.fechaSustentacion",
				new Object[] { carrera }, 0, false, new Object[0]);
	}

	public void cargarActa(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
		listCartillaEsc = new ArrayList<Cartilla>();
		totalEsc = UtilsMath.newBigDecimal();
		listCartillaOral = new ArrayList<Cartilla>();
		totalOral = UtilsMath.newBigDecimal();
		String consultaCartillaEsc = "select distinct cr.id as id, cr.nombre as criterio, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.especialista1 and cr1.id=cr.id), 0.00) as esp1, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.especialista2 and cr1.id=cr.id), 0.00) as esp2, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.especialista3 and cr1.id=cr.id), 0.00) as esp3, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.\"especialistaSuplente1\" and cr1.id=cr.id), 0.00) as espSupl1, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.\"especialistaSuplente2\" and cr1.id=cr.id), 0.00) as espSupl2, "
				+ "0.00 as totalCriterio " + "from exetasi.\"calificacionesTrabajosTitulacion\" c "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(c.\"estudianteTrabajoTitulacion\"=ett.id) "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(c.\"indicadorTrabajoTitulacion\"=i.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr on(i.\"criterioTrabajoTitulacion\"=cr.id) "
				+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) " + "where ett.id="
				+ estudianteTrabajoTitulacion.getId() + " and fp.id='ESC' order by cr.id";
		String consultaCartillaOral = "select distinct cr.id as id, cr.nombre as criterio, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.especialista1 and cr1.id=cr.id), 0.00) as esp1, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.especialista2 and cr1.id=cr.id), 0.00) as esp2, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.especialista3 and cr1.id=cr.id), 0.00) as esp3, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.\"especialistaSuplente1\" and cr1.id=cr.id), 0.00) as espSupl1, "
				+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
				+ "where ett1.id=ett.id and u1.id=ett.\"especialistaSuplente2\" and cr1.id=cr.id), 0.00) as espSupl2, "
				+ "0.00 as totalCriterio " + "from exetasi.\"calificacionesTrabajosTitulacion\" c "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(c.\"estudianteTrabajoTitulacion\"=ett.id) "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(c.\"indicadorTrabajoTitulacion\"=i.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr on(i.\"criterioTrabajoTitulacion\"=cr.id) "
				+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) " + "where ett.id="
				+ estudianteTrabajoTitulacion.getId() + " and fp.id='ORA' order by cr.id";
		listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaEsc, Cartilla.class);
		for (Cartilla c : listCartillaEsc) {
			c.setTotalCriterio(UtilsMath.divideCalificaciones(
					c.getEsp1().add(c.getEsp2()).add(c.getEsp3()).add(c.getEspSupl1()).add(c.getEspSupl2()), 3));
		}
		listCartillaOral = cartillaService.obtenerPorSql(consultaCartillaOral, Cartilla.class);
		for (Cartilla c : listCartillaOral) {
			c.setTotalCriterio(UtilsMath.divideCalificaciones(
					c.getEsp1().add(c.getEsp2()).add(c.getEsp3()).add(c.getEspSupl1()).add(c.getEspSupl2()), 3));
		}
		totalEsc = estudianteTrabajoTitulacion.getCalificacionEscrita();
		totalOral = estudianteTrabajoTitulacion.getCalificacionOral();
	}

	public void eliminarCalificacion() {
		if (estudianteTrabajoTitulacion != null && estudianteTrabajoTitulacion.getId() != null) {
			estudianteTrabajoTitulacion.setCalificacionEscrita(null);
			estudianteTrabajoTitulacion.setCalificacionOral(null);
			for (CalificacionTrabajoTitulacion ctt : estudianteTrabajoTitulacion.getCalificacionesTrabajoTitulaciones())
				calificacionTrabajoTitulacionService.eliminar(ctt);
			estudianteTrabajoTitulacion.setCalificacionesTrabajoTitulaciones(null);
			estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
			listEstudianteTrabajoTitulaciones.remove(estudianteTrabajoTitulacion);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Elimino correctamente la calificación",
					"cerrar", true);
		}
	}

	public Integer getCarrera() {
		return this.carrera;
	}

	public int getConsultaABC() {
		return this.consultaABC;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public List<Carrera> getListCarreras() {
		return this.listCarreras;
	}

	public List<Cartilla> getListCartillaEsc() {
		return listCartillaEsc;
	}

	public List<Cartilla> getListCartillaOral() {
		return listCartillaOral;
	}

	public List<EstudianteTrabajoTitulacion> getListEstudianteTrabajoTitulaciones() {
		return listEstudianteTrabajoTitulaciones;
	}

	public BigDecimal getTotalEsc() {
		return totalEsc;
	}

	public BigDecimal getTotalOral() {
		return totalOral;
	}

	@PostConstruct
	public void init() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		listCarreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { email }, Integer.valueOf(0), false, new Object[0]);

		if (this.listCarreras.size() == 1) {
			this.carrera = ((Carrera) this.listCarreras.get(0)).getId();
		}
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setConsultaABC(int consultaABC) {
		this.consultaABC = consultaABC;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListCartillaEsc(List<Cartilla> listCartillaEsc) {
		this.listCartillaEsc = listCartillaEsc;
	}

	public void setListCartillaOral(List<Cartilla> listCartillaOral) {
		this.listCartillaOral = listCartillaOral;
	}

	public void setListEstudianteTrabajoTitulaciones(
			List<EstudianteTrabajoTitulacion> listEstudianteTrabajoTitulaciones) {
		this.listEstudianteTrabajoTitulaciones = listEstudianteTrabajoTitulaciones;
	}

	public void setTotalEsc(BigDecimal totalEsc) {
		this.totalEsc = totalEsc;
	}

	public void setTotalOral(BigDecimal totalOral) {
		this.totalOral = totalOral;
	}

}
