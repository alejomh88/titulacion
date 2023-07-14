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

import ec.edu.utmachala.titulacion.entity.Calificacion;
import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.Criterio;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.Indicador;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.CalificacionAux;
import ec.edu.utmachala.titulacion.entityAux.Cartilla;
import ec.edu.utmachala.titulacion.service.CalificacionService;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.CartillaService;
import ec.edu.utmachala.titulacion.service.CriterioService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.IndicadorService;
import ec.edu.utmachala.titulacion.service.TipoExamenService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Controller
@Scope("session")
public class CalificacionTemaPracticoBean implements Serializable {
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
	private EstudiantesExamenComplexivoPPService temaPracticoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private CriterioService criterioService;

	@Autowired
	private IndicadorService indicadorService;

	@Autowired
	private TipoExamenService tipoExamenService;

	@Autowired
	private CalificacionService calificacionService;

	@Autowired
	private CartillaService cartillaService;
	private List<Carrera> listCarreras;
	private Integer carrera;
	private String cedulaEstudiante;

	private EstudianteExamenComplexivoPP temaPractico;
	private List<Usuario> listEspecialista;

	private Usuario especialista;
	private List<Criterio> listCriterioEsc;

	private List<Criterio> listCriterioOral;

	private List<CalificacionAux> listCalificacion;

	public void añadirCalificacion(CalificacionAux calificacionAux) {
		if (calificacionAux.getIndicador().getId() != null && calificacionAux.getIndicador().getId() != 0) {
			calificacionAux.setIndicador(indicadorService.obtenerObjeto("select i from Indicador i where i.id=?1",
					new Object[] { calificacionAux.getIndicador().getId() }, false, new Object[0]));
		} else
			calificacionAux.setIndicador(new Indicador());
	}

	public void buscar() {
		listEspecialista = new ArrayList<Usuario>();
		temaPractico = temaPracticoService.obtenerObjeto(
				"select distinct tp from TemaPractico tp inner join tp.estudiantePeriodo ep inner join ep.estudiante e inner join ep.periodoExamen pe inner join pe.proceso p inner join pe.carrera c "
						+ "inner join tp.especialista1 esp1 inner join tp.especialista2 esp2 inner join tp.especialista3 esp3 "
						+ "left join fetch tp.calificaciones cal "
						+ "where c.id=?1 and tp.estudiantePeriodo is not null and p.fechaInicio<=?2 and p.fechaCierre>?2 "
						+ "and e.id=?3 order by tp.fechaSustentacionOrdinaria ",
				new Object[] { carrera, UtilsDate.timestamp(), cedulaEstudiante }, false, new Object[0]);
		listEspecialista.add(temaPractico.getEspecialista1());
		listEspecialista.add(temaPractico.getEspecialista2());
		listEspecialista.add(temaPractico.getEspecialista3());
	}

	public void cargarCriterioEsc() {
		listCriterioEsc = criterioService.obtenerLista(
				"select c from Criterio c inner join c.proceso p inner join c.formaPresentacion fp "
						+ "where p.fechaInicio<=?1 and p.fechaCierre>?1 and fp.id='ESC' order by c.id",
				new Object[] { UtilsDate.timestamp() }, 0, false, new Object[0]);
	}

	public void cargarCriterioOral() {
		listCriterioEsc = criterioService.obtenerLista(
				"select c from Criterio c inner join c.proceso p inner join c.formaPresentacion fp "
						+ "where p.fechaInicio<=?1 and p.fechaCierre>?1 and fp.id='ORA' order by c.id",
				new Object[] { UtilsDate.timestamp() }, 0, false, new Object[0]);
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

	public List<CalificacionAux> getListCalificacion() {
		return listCalificacion;
	}

	public List<Carrera> getListCarreras() {
		return this.listCarreras;
	}

	public List<Criterio> getListCriterioEsc() {
		return listCriterioEsc;
	}

	public List<Criterio> getListCriterioOral() {
		return listCriterioOral;
	}

	public List<Usuario> getListEspecialista() {
		return listEspecialista;
	}

	public EstudianteExamenComplexivoPP getTemaPractico() {
		return this.temaPractico;
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
			if (cAux.getIndicador().getId() == null || cAux.getIndicador().getId() == 0) {
				bn = false;
				break;
			}
		if (bn == false)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese todos los indicadores");
		else if (bn == true) {
			List<Calificacion> listCalificacionAux = new ArrayList<Calificacion>();
			if (temaPractico.getCalificaciones() == null)
				temaPractico.setCalificaciones(new ArrayList<Calificacion>());
			for (CalificacionAux cAux : listCalificacion) {
				Calificacion c = new Calificacion();
				c.setIndicador(cAux.getIndicador());
				c.setTemaPractico(temaPractico);
				c.setEspecialista(usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { especialista.getId() }, false, new Object[0]));
				c.setTipoExamen(tipoExamenService.obtenerObjeto("select te from TipoExamen te where te.id=?1",
						new Object[] { temaPractico.getFechaSustentacionGracia() == null ? "OR" : "GR" }, false,
						new Object[0]));
				c.setCalificacion(c.getIndicador().getPesoSuperior());
				temaPractico.getCalificaciones().add(c);
				listCalificacionAux.add(c);
			}
			temaPracticoService.actualizar(temaPractico);

			List<Usuario> listEspecialistaESC = usuarioService.obtenerPorSql("SELECT distinct d.* "
					+ "FROM exetasi.calificaciones c " + "inner join exetasi.indicadores i on(c.indicador=i.id) "
					+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
					+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
					+ "inner join exetasi.\"temasPracticos\" tp on(tp.id=c.\"temaPractico\") "
					+ "inner join exetasi.usuarios d on(d.id=c.especialista) " + "where tp.id=" + temaPractico.getId()
					+ " and fp.id='ESC' and c.\"tipoExamen\"='"
					+ (temaPractico.getFechaSustentacionGracia() == null ? "OR" : "GR") + "';", Usuario.class);
			if (listEspecialistaESC.size() == 3) {
				String consultaCartillaEsc = "select distinct cr.id as id, cr.nombre as criterio, "
						+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
						+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
						+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
						+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
						+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
						+ "where tp1.id=tp.id and u1.id=tp.especialista1 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp1, "
						+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
						+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
						+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
						+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
						+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
						+ "where tp1.id=tp.id and u1.id=tp.especialista2 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp2, "
						+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
						+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
						+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
						+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
						+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
						+ "where tp1.id=tp.id and u1.id=tp.especialista3 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp3,"
						+ "0.00 as espSupl1, 0.00 as espSupl2, 0.00 as totalCriterio "
						+ "from exetasi.calificaciones c "
						+ "inner join exetasi.\"temasPracticos\" tp on(c.\"temaPractico\"=tp.id) "
						+ "inner join exetasi.\"tiposExamenes\" te on(c.\"tipoExamen\"=te.id) "
						+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
						+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
						+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
						+ "where tp.id=" + temaPractico.getId() + " and te.id='"
						+ (temaPractico.getFechaSustentacionGracia() == null ? "OR" : "GR")
						+ "' and fp.id='ESC' order by cr.id";

				List<Cartilla> listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaEsc, Cartilla.class);
				BigDecimal totalEsc = UtilsMath.newBigDecimal();
				for (Cartilla c : listCartillaEsc) {
					c.setTotalCriterio(
							UtilsMath.divideCalificaciones(c.getEsp1().add(c.getEsp2()).add(c.getEsp3()), 3));
					totalEsc = totalEsc.add(c.getTotalCriterio());
				}

				if (temaPractico.getFechaSustentacionGracia() == null) {
					temaPractico.setCalificacionEscritaOrdinaria(totalEsc);
				} else {
					temaPractico.setCalificacionEscritaGracia(totalEsc);
				}
				temaPracticoService.actualizar(temaPractico);
			}

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto correctamente la calificación escrita",
					"cerrar", true);
		}
	}

	public void insertarCalificacionOral() {
		boolean bn = true;
		for (CalificacionAux cAux : listCalificacion)
			if (cAux.getIndicador().getId() == null || cAux.getIndicador().getId() == 0) {
				bn = false;
				break;
			}
		if (bn == false)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese todos los indicadores");
		else if (bn == true) {
			List<Calificacion> listCalificacionAux = new ArrayList<Calificacion>();
			if (temaPractico.getCalificaciones() == null)
				temaPractico.setCalificaciones(new ArrayList<Calificacion>());
			for (CalificacionAux cAux : listCalificacion) {
				Calificacion c = new Calificacion();
				c.setIndicador(cAux.getIndicador());
				c.setTemaPractico(temaPractico);
				c.setEspecialista(usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { especialista.getId() }, false, new Object[0]));
				c.setTipoExamen(tipoExamenService.obtenerObjeto("select te from TipoExamen te where te.id=?1",
						new Object[] { temaPractico.getFechaSustentacionGracia() == null ? "OR" : "GR" }, false,
						new Object[0]));
				c.setCalificacion(c.getIndicador().getPesoSuperior());
				temaPractico.getCalificaciones().add(c);
				listCalificacionAux.add(c);
			}
			temaPracticoService.actualizar(temaPractico);

			List<Usuario> listEspecialistaORA = usuarioService.obtenerPorSql("SELECT distinct d.* "
					+ "FROM exetasi.calificaciones c " + "inner join exetasi.indicadores i on(c.indicador=i.id) "
					+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
					+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
					+ "inner join exetasi.\"temasPracticos\" tp on(tp.id=c.\"temaPractico\") "
					+ "inner join exetasi.usuarios d on(d.id=c.especialista) " + "where tp.id=" + temaPractico.getId()
					+ " and fp.id='ORA' and c.\"tipoExamen\"='"
					+ (temaPractico.getFechaSustentacionGracia() == null ? "OR" : "GR") + "';", Usuario.class);
			if (listEspecialistaORA.size() == 3) {
				String consultaCartillaOral = "select distinct cr.id as id, cr.nombre as criterio, "
						+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
						+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
						+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
						+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
						+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
						+ "where tp1.id=tp.id and u1.id=tp.especialista1 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp1, "
						+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
						+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
						+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
						+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
						+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
						+ "where tp1.id=tp.id and u1.id=tp.especialista2 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp2, "
						+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
						+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
						+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
						+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
						+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
						+ "where tp1.id=tp.id and u1.id=tp.especialista3 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp3, "
						+ "0.00 as espSupl1, 0.00 as espSupl2, 0.00 as totalCriterio "
						+ "from exetasi.calificaciones c "
						+ "inner join exetasi.\"temasPracticos\" tp on(c.\"temaPractico\"=tp.id) "
						+ "inner join exetasi.\"tiposExamenes\" te on(c.\"tipoExamen\"=te.id) "
						+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
						+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
						+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
						+ "where tp.id=" + temaPractico.getId() + " and te.id='"
						+ (temaPractico.getFechaSustentacionGracia() == null ? "OR" : "GR")
						+ "' and fp.id='ORA' order by cr.id";

				List<Cartilla> listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaOral, Cartilla.class);
				BigDecimal totalOral = UtilsMath.newBigDecimal();
				for (Cartilla c : listCartillaEsc) {
					c.setTotalCriterio(
							UtilsMath.divideCalificaciones(c.getEsp1().add(c.getEsp2()).add(c.getEsp3()), 3));
					totalOral = totalOral.add(c.getTotalCriterio());
				}

				if (temaPractico.getFechaSustentacionGracia() == null) {
					temaPractico.setCalificacionOralOrdinaria(totalOral);
				} else {
					temaPractico.setCalificacionOralGracia(totalOral);
				}
				temaPracticoService.actualizar(temaPractico);
			}

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto correctamente la calificación oral",
					"cerrar", true);
		}
	}

	public void limpiarEsc(Usuario especialista) {
		this.especialista = especialista;
		List<Calificacion> listCalificacionAux = calificacionService.obtenerListaDirecta(
				"select c from Calificacion c inner join c.especialista e inner join c.temaPractico tp "
						+ "inner join c.tipoExamen te inner join c.indicador i "
						+ "inner join i.criterio cr inner join cr.formaPresentacion fp "
						+ "where tp.id=?1 and te.id=?2 and e.id=?3 and fp.id='ESC' ",
				new Object[] { temaPractico.getId(), temaPractico.getFechaSustentacionGracia() == null ? "OR" : "GR",
						especialista.getId() },
				0, false, new Object[0]);
		if (listCalificacionAux != null && !listCalificacionAux.isEmpty())
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ya Ingreso la calificación de este docente");
		else {
			listCalificacion = new ArrayList<CalificacionAux>();
			cargarCriterioEsc();
			for (Criterio cr : listCriterioEsc) {
				listCalificacion.add(new CalificacionAux(cr, indicadorService.obtenerLista(
						"select i from Indicador i inner join i.criterio c where c.id=?1 order by i.pesoSuperior desc",
						new Object[] { cr.getId() }, 0, false, new Object[0]), new Indicador()));
				UtilsAplicacion.enviarVariableVista("cerrar", true);
			}
		}
	}

	public void limpiarOral(Usuario especialista) {
		this.especialista = especialista;
		List<Calificacion> listCalificacionAux = calificacionService.obtenerListaDirecta(
				"select c from Calificacion c inner join c.especialista e inner join c.temaPractico tp "
						+ "inner join c.tipoExamen te inner join c.indicador i "
						+ "inner join i.criterio cr inner join cr.formaPresentacion fp "
						+ "where tp.id=?1 and te.id=?2 and e.id=?3 and fp.id='ORA' ",
				new Object[] { temaPractico.getId(), temaPractico.getFechaSustentacionGracia() == null ? "OR" : "GR",
						especialista.getId() },
				0, false, new Object[0]);
		if (listCalificacionAux != null && !listCalificacionAux.isEmpty())
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ya Ingreso la calificación de este docente");
		else {
			listCalificacion = new ArrayList<CalificacionAux>();
			cargarCriterioOral();
			for (Criterio cr : listCriterioEsc) {
				listCalificacion.add(new CalificacionAux(cr, indicadorService.obtenerLista(
						"select i from Indicador i inner join i.criterio c where c.id=?1 order by i.pesoSuperior desc",
						new Object[] { cr.getId() }, 0, false, new Object[0]), new Indicador()));
				UtilsAplicacion.enviarVariableVista("cerrar", true);
			}
		}
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

	public void setListCalificacion(List<CalificacionAux> listCalificacion) {
		this.listCalificacion = listCalificacion;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListCriterioEsc(List<Criterio> listCriterioEsc) {
		this.listCriterioEsc = listCriterioEsc;
	}

	public void setListCriterioOral(List<Criterio> listCriterioOral) {
		this.listCriterioOral = listCriterioOral;
	}

	public void setListEspecialista(List<Usuario> listEspecialista) {
		this.listEspecialista = listEspecialista;
	}

	public void setTemaPractico(EstudianteExamenComplexivoPP temaPractico) {
		this.temaPractico = temaPractico;
	}

}
