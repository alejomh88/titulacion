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
import ec.edu.utmachala.titulacion.entity.CriterioTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Indicador;
import ec.edu.utmachala.titulacion.entity.IndicadorTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.CalificacionAux;
import ec.edu.utmachala.titulacion.entityAux.Cartilla;
import ec.edu.utmachala.titulacion.service.CalificacionTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.CartillaService;
import ec.edu.utmachala.titulacion.service.CriterioTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.IndicadorTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Controller
@Scope("session")
public class CalificacionTrabajoTitulacionBean implements Serializable {
	private static final long serialVersionUID = 1L;

	public static List<String> quitarRepetidos(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			String dato = list.get(i);
			for (int j = i + 1; j < list.size(); j++) {
				String dato1 = list.get(j);
				if (dato != null && dato1 != null && dato.compareToIgnoreCase(dato1) == 0)
					list.set(j, null);
			}
		}
		List<String> list1 = new ArrayList<String>();
		for (String s : list) {
			if (s != null)
				list1.add(s);
		}
		return list1;
	}

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private CriterioTrabajoTitulacionService criterioTrabajoTitulacionService;

	@Autowired
	private IndicadorTrabajoTitulacionService indicadorTrabajoTitulacionService;

	@Autowired
	private CalificacionTrabajoTitulacionService calificacionTrabajoTitulacionService;

	@Autowired
	private CartillaService cartillaService;
	private List<Carrera> listCarreras;
	private Integer carrera;
	private String cedulaEstudiante;

	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;
	private List<Usuario> listEspecialista;

	private Usuario especialista;
	private List<CriterioTrabajoTitulacion> listCriterioEsc;

	private List<CriterioTrabajoTitulacion> listCriterioOral;

	private List<CalificacionAux> listCalificacion;
	private boolean bnEsc;

	private boolean bnOral;

	public void añadirCalificacion(CalificacionAux calificacionAux) {
		if (calificacionAux.getIndicadorTrabajoTitulacion().getId() != null
				&& calificacionAux.getIndicadorTrabajoTitulacion().getId() != 0)
			calificacionAux.setIndicadorTrabajoTitulacion(indicadorTrabajoTitulacionService.obtenerObjeto(
					"select i from IndicadorTrabajoTitulacion i where i.id=?1",
					new Object[] { calificacionAux.getIndicadorTrabajoTitulacion().getId() }, false, new Object[0]));
		else
			calificacionAux.setIndicador(new Indicador());
	}

	public void buscar() {
		listEspecialista = new ArrayList<Usuario>();
		bnEsc = false;
		bnOral = false;
		estudianteTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerObjeto(
				// "select distinct ett from EstudianteTrabajoTitulacion
				// ett
				// inner join ett.estudiante e inner join
				// ett.seminarioTrabajoTitulacion stt inner join
				// stt.seminario s
				// inner join s.proceso p inner join ett.carrera c "
				// +
				// "inner join ett.especialista1 esp1 inner join
				// ett.especialista2 esp2 inner join ett.especialista3
				// esp3 "
				// +
				// "left join fetch ett.calificacionesTrabajoTitulaciones ctt "
				// +
				// "where c.id=?1 and p.fechaInicio<=?2 and p.fechaCierre>?2 "
				// + "and e.id=?3 order by ett.fechaSustentacion",
				"select distinct ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.carrera c "
						+ "inner join ett.especialista1 esp1 inner join ett.especialista2 esp2 inner join ett.especialista3 esp3 "
						+ "left join fetch ett.calificacionesTrabajoTitulaciones ctt "
						+ "where c.id=?1 and e.id=?2 and ett.opcionTitulacion is not null order by ett.fechaSustentacion",
				new Object[] { carrera, cedulaEstudiante }, false, new Object[0]);
		if (estudianteTrabajoTitulacion != null && estudianteTrabajoTitulacion.getId() != null
				&& estudianteTrabajoTitulacion.getId() != 0) {
			listEspecialista.add(estudianteTrabajoTitulacion.getEspecialista1());
			listEspecialista.add(estudianteTrabajoTitulacion.getEspecialista2());
			listEspecialista.add(estudianteTrabajoTitulacion.getEspecialista3());
			listEspecialista.add(estudianteTrabajoTitulacion.getEspecialistaSuplente1());
			listEspecialista.add(estudianteTrabajoTitulacion.getEspecialistaSuplente2());
		}
	}

	public void cargarCriterioEsc() {
		listCriterioEsc = criterioTrabajoTitulacionService.obtenerLista(
				"select c from CriterioTrabajoTitulacion c inner join c.opcionTitulacion ot inner join c.proceso p inner join c.formaPresentacion fp "
						+ "where p.fechaInicio<=?1 and p.fechaCierre>?1 and ot.id=?2 and fp.id='ESC' order by c.id",
				new Object[] { UtilsDate.timestamp(), estudianteTrabajoTitulacion.getOpcionTitulacion().getId() }, 0,
				false, new Object[0]);
	}

	public void cargarCriterioOral() {
		listCriterioEsc = criterioTrabajoTitulacionService.obtenerLista(
				"select c from CriterioTrabajoTitulacion c inner join c.opcionTitulacion ot inner join c.proceso p inner join c.formaPresentacion fp "
						+ "where p.fechaInicio<=?1 and p.fechaCierre>?1 and ot.id=?2 and fp.id='ORA' order by c.id",
				new Object[] { UtilsDate.timestamp(), estudianteTrabajoTitulacion.getOpcionTitulacion().getId() }, 0,
				false, new Object[0]);
	}

	public Integer getCarrera() {
		return this.carrera;
	}

	public String getCedulaEstudiante() {
		return cedulaEstudiante;
	}

	public Usuario getEspecialista() {
		return especialista;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public List<CalificacionAux> getListCalificacion() {
		return listCalificacion;
	}

	public List<Carrera> getListCarreras() {
		return this.listCarreras;
	}

	public List<CriterioTrabajoTitulacion> getListCriterioEsc() {
		return listCriterioEsc;
	}

	public List<CriterioTrabajoTitulacion> getListCriterioOral() {
		return listCriterioOral;
	}

	public List<Usuario> getListEspecialista() {
		return listEspecialista;
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

	public void insertarCalificacionEsc() {
		boolean bn = true;
		for (CalificacionAux cAux : listCalificacion)
			if (cAux.getIndicadorTrabajoTitulacion().getId() == null
					|| cAux.getIndicadorTrabajoTitulacion().getId() == 0) {
				bn = false;
				break;
			}
		if (bn == false)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese todos los indicadores");
		else if (bn == true) {
			List<CalificacionTrabajoTitulacion> listCalificacionAux = new ArrayList<CalificacionTrabajoTitulacion>();
			if (estudianteTrabajoTitulacion.getCalificacionesTrabajoTitulaciones() == null)
				estudianteTrabajoTitulacion
						.setCalificacionesTrabajoTitulaciones(new ArrayList<CalificacionTrabajoTitulacion>());
			for (CalificacionAux cAux : listCalificacion) {
				CalificacionTrabajoTitulacion c = new CalificacionTrabajoTitulacion();
				c.setIndicadorTrabajoTitulacion(cAux.getIndicadorTrabajoTitulacion());
				c.setEstudianteTrabajoTitulacion(estudianteTrabajoTitulacion);
				c.setEspecialista(usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { especialista.getId() }, false, new Object[0]));
				c.setCalificacion(c.getIndicadorTrabajoTitulacion().getPesoSuperior());
				estudianteTrabajoTitulacion.getCalificacionesTrabajoTitulaciones().add(c);
				listCalificacionAux.add(c);
			}
			estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);

			List<Usuario> listEspecialista = usuarioService.obtenerPorSql("SELECT distinct d.* "
					+ "FROM exetasi.\"calificacionesTrabajosTitulacion\" ctt "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(ctt.\"indicadorTrabajoTitulacion\"=i.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr on(i.\"criterioTrabajoTitulacion\"=cr.id) "
					+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(ett.id=ctt.\"estudianteTrabajoTitulacion\") "
					+ "inner join exetasi.usuarios d on(d.id=ctt.especialista) " + "where ett.id="
					+ estudianteTrabajoTitulacion.getId() + " and fp.id='ESC';", Usuario.class);
			if (listEspecialista.size() == 3) {
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
						+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
						+ "where ett.id=" + estudianteTrabajoTitulacion.getId() + " and fp.id='ESC' order by cr.id";

				List<Cartilla> listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaEsc, Cartilla.class);
				BigDecimal totalEsc = UtilsMath.newBigDecimal();
				for (Cartilla c : listCartillaEsc) {
					c.setTotalCriterio(UtilsMath.divideCalificaciones(
							c.getEsp1().add(c.getEsp2()).add(c.getEsp3()).add(c.getEspSupl1()).add(c.getEspSupl2()),
							3));
					totalEsc = totalEsc.add(c.getTotalCriterio());
				}

				bnEsc = true;
				estudianteTrabajoTitulacion.setCalificacionEscrita(totalEsc);
				estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
			}

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto correctamente la calificación escrita",
					"cerrar", true);
		}
	}

	public void insertarCalificacionOral() {
		boolean bn = true;
		for (CalificacionAux cAux : listCalificacion)
			if (cAux.getIndicadorTrabajoTitulacion().getId() == null
					|| cAux.getIndicadorTrabajoTitulacion().getId() == 0) {
				bn = false;
				break;
			}
		if (bn == false)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese todos los indicadores");
		else if (bn == true) {
			List<CalificacionTrabajoTitulacion> listCalificacionAux = new ArrayList<CalificacionTrabajoTitulacion>();
			if (estudianteTrabajoTitulacion.getCalificacionesTrabajoTitulaciones() == null)
				estudianteTrabajoTitulacion
						.setCalificacionesTrabajoTitulaciones(new ArrayList<CalificacionTrabajoTitulacion>());
			for (CalificacionAux cAux : listCalificacion) {
				CalificacionTrabajoTitulacion c = new CalificacionTrabajoTitulacion();
				c.setIndicadorTrabajoTitulacion(cAux.getIndicadorTrabajoTitulacion());
				c.setEstudianteTrabajoTitulacion(estudianteTrabajoTitulacion);
				c.setEspecialista(usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { especialista.getId() }, false, new Object[0]));
				c.setCalificacion(c.getIndicadorTrabajoTitulacion().getPesoSuperior());
				estudianteTrabajoTitulacion.getCalificacionesTrabajoTitulaciones().add(c);
				listCalificacionAux.add(c);
			}
			estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);

			List<Usuario> listEspecialista = usuarioService.obtenerPorSql("SELECT distinct d.* "
					+ "FROM exetasi.\"calificacionesTrabajosTitulacion\" ctt "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(ctt.\"indicadorTrabajoTitulacion\"=i.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr on(i.\"criterioTrabajoTitulacion\"=cr.id) "
					+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(ett.id=ctt.\"estudianteTrabajoTitulacion\") "
					+ "inner join exetasi.usuarios d on(d.id=ctt.especialista) " + "where ett.id="
					+ estudianteTrabajoTitulacion.getId() + " and fp.id='ORA';", Usuario.class);
			if (listEspecialista.size() == 3) {
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
						+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
						+ "where ett.id=" + estudianteTrabajoTitulacion.getId() + " and fp.id='ORA' order by cr.id";

				List<Cartilla> listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaOral, Cartilla.class);
				BigDecimal totalOral = UtilsMath.newBigDecimal();
				for (Cartilla c : listCartillaEsc) {
					c.setTotalCriterio(UtilsMath.divideCalificaciones(
							c.getEsp1().add(c.getEsp2()).add(c.getEsp3()).add(c.getEspSupl1()).add(c.getEspSupl2()),
							3));
					totalOral = totalOral.add(c.getTotalCriterio());
				}

				bnEsc = true;
				estudianteTrabajoTitulacion.setCalificacionOral(totalOral);
				estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
			}

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto correctamente la calificación oral",
					"cerrar", true);
		}
	}

	public boolean isBnEsc() {
		return bnEsc;
	}

	public boolean isBnOral() {
		return bnOral;
	}

	public void limpiarEsc(Usuario especialista) {
		this.especialista = especialista;
		List<CalificacionTrabajoTitulacion> listCalificacionAux = calificacionTrabajoTitulacionService
				.obtenerListaDirecta(
						"select c from CalificacionTrabajoTitulacion c inner join c.especialista e inner join c.estudianteTrabajoTitulacion ett "
								+ "inner join c.indicadorTrabajoTitulacion i "
								+ "inner join i.criterioTrabajoTitulacion cr inner join cr.formaPresentacion fp "
								+ "where ett.id=?1 and e.id=?2 and fp.id='ESC' ",
						new Object[] { estudianteTrabajoTitulacion.getId(), especialista.getId() }, 0, false,
						new Object[0]);
		if (listCalificacionAux != null && !listCalificacionAux.isEmpty())
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ya Ingreso la calificación de este docente");
		else {
			listCalificacion = new ArrayList<CalificacionAux>();
			cargarCriterioEsc();
			for (CriterioTrabajoTitulacion ctt : listCriterioEsc) {
				listCalificacion.add(new CalificacionAux(ctt, indicadorTrabajoTitulacionService.obtenerLista(
						"select i from IndicadorTrabajoTitulacion i inner join i.criterioTrabajoTitulacion c where c.id=?1 order by i.pesoSuperior desc",
						new Object[] { ctt.getId() }, 0, false, new Object[0]), new IndicadorTrabajoTitulacion()));
				UtilsAplicacion.enviarVariableVista("cerrar", true);
			}
		}
	}

	public void limpiarOral(Usuario especialista) {
		this.especialista = especialista;
		List<CalificacionTrabajoTitulacion> listCalificacionAux = calificacionTrabajoTitulacionService
				.obtenerListaDirecta(
						"select c from CalificacionTrabajoTitulacion c inner join c.especialista e inner join c.estudianteTrabajoTitulacion ett "
								+ "inner join c.indicadorTrabajoTitulacion i "
								+ "inner join i.criterioTrabajoTitulacion cr inner join cr.formaPresentacion fp "
								+ "where ett.id=?1 and e.id=?2 and fp.id='ORA' ",
						new Object[] { estudianteTrabajoTitulacion.getId(), especialista.getId() }, 0, false,
						new Object[0]);
		if (listCalificacionAux != null && !listCalificacionAux.isEmpty())
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ya Ingreso la calificación de este docente");
		else {
			listCalificacion = new ArrayList<CalificacionAux>();
			cargarCriterioOral();
			for (CriterioTrabajoTitulacion ctt : listCriterioEsc) {
				listCalificacion.add(new CalificacionAux(ctt, indicadorTrabajoTitulacionService.obtenerLista(
						"select i from IndicadorTrabajoTitulacion i inner join i.criterioTrabajoTitulacion c where c.id=?1 order by i.pesoSuperior desc",
						new Object[] { ctt.getId() }, 0, false, new Object[0]), new IndicadorTrabajoTitulacion()));
				UtilsAplicacion.enviarVariableVista("cerrar", true);
			}
		}
	}

	public void setBnEsc(boolean bnEsc) {
		this.bnEsc = bnEsc;
	}

	public void setBnOral(boolean bnOral) {
		this.bnOral = bnOral;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCedulaEstudiante(String cedulaEstudiante) {
		this.cedulaEstudiante = cedulaEstudiante;
	}

	public void setEspecialista(Usuario especialista) {
		this.especialista = especialista;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setListCalificacion(List<CalificacionAux> listCalificacion) {
		this.listCalificacion = listCalificacion;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListCriterioEsc(List<CriterioTrabajoTitulacion> listCriterioEsc) {
		this.listCriterioEsc = listCriterioEsc;
	}

	public void setListCriterioOral(List<CriterioTrabajoTitulacion> listCriterioOral) {
		this.listCriterioOral = listCriterioOral;
	}

	public void setListEspecialista(List<Usuario> listEspecialista) {
		this.listEspecialista = listEspecialista;
	}

}
