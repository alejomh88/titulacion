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
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entityAux.Cartilla;
import ec.edu.utmachala.titulacion.service.CalificacionService;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.CartillaService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Controller
@Scope("session")
public class ListadoCalificacionTemaPracticoBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudiantesExamenComplexivoPPService temaPracticoService;

	@Autowired
	private CartillaService cartillaService;

	@Autowired
	private CalificacionService calificacionService;

	private List<Carrera> listCarreras;
	private Integer carrera;
	private int consultaABC;

	private List<EstudianteExamenComplexivoPP> listTemaPracticos;
	private EstudianteExamenComplexivoPP temaPractico;

	private List<Cartilla> listCartillaEsc;
	private BigDecimal totalEsc;
	private List<Cartilla> listCartillaOral;
	private BigDecimal totalOral;

	private int consultaOrdGra;
	private String consultaAI = "and (e.apellidoPaterno like 'A%' or e.apellidoPaterno like 'B%' or e.apellidoPaterno like 'C%' or e.apellidoPaterno like 'D%' or e.apellidoPaterno like 'E%' or e.apellidoPaterno like 'F%' or e.apellidoPaterno like 'G%' or e.apellidoPaterno like 'H%' or e.apellidoPaterno like 'I%') ";
	private String consultaJQ = "and (e.apellidoPaterno like 'J%' or e.apellidoPaterno like 'K%' or e.apellidoPaterno like 'L%' or e.apellidoPaterno like 'M%' or e.apellidoPaterno like 'N%' or e.apellidoPaterno like 'Ñ%' or e.apellidoPaterno like 'O%' or e.apellidoPaterno like 'P%' or e.apellidoPaterno like 'Q%') ";
	private String consultaRZ = "and (e.apellidoPaterno like 'R%' or e.apellidoPaterno like 'S%' or e.apellidoPaterno like 'T%' or e.apellidoPaterno like 'U%' or e.apellidoPaterno like 'V%' or e.apellidoPaterno like 'W%' or e.apellidoPaterno like 'X%' or e.apellidoPaterno like 'Y%' or e.apellidoPaterno like 'Z%') ";

	public void buscar() {
		listTemaPracticos = temaPracticoService.obtenerLista(
				"select distinct tp from TemaPractico tp inner join tp.estudiantePeriodo ep inner join ep.estudiante e inner join ep.periodoExamen pe inner join pe.proceso p inner join pe.carrera c "
						+ "inner join tp.especialista1 esp1 inner join tp.especialista2 esp2 inner join tp.especialista3 esp3 "
						+ "left join fetch tp.calificaciones cal "
						+ "where c.id=?1 and tp.estudiantePeriodo is not null and p.fechaInicio<=?2 and p.fechaCierre>?2 "
						+ (consultaABC == 2 ? consultaJQ : consultaABC == 1 ? consultaAI : consultaRZ)
						+ (consultaOrdGra == 1
								? "and tp.calificacionEscritaOrdinaria is not null and tp.calificacionOralOrdinaria is not null and tp.validarCalificacionOrdinaria is null "
										+ "order by tp.fechaSustentacionOrdinaria "
								: "and tp.calificacionEscritaGracia is not null and tp.calificacionOralGracia is not null and tp.validarCalificacionGracia is null "
										+ "order by tp.fechaSustentacionGracia "),
				// +
				// "order by e.apellidoPaterno, e.apellidoMaterno, e.nombre ",
				new Object[] { carrera, UtilsDate.timestamp() }, 0, false, new Object[0]);
	}

	public void cargarActa(EstudianteExamenComplexivoPP temaPractico) {
		this.temaPractico = temaPractico;
		listCartillaEsc = new ArrayList<Cartilla>();
		totalEsc = UtilsMath.newBigDecimal();
		listCartillaOral = new ArrayList<Cartilla>();
		totalOral = UtilsMath.newBigDecimal();
		String consultaCartillaEsc = "select distinct cr.id as id, cr.nombre as criterio, "
				+ "(select cal.calificacion from exetasi.calificaciones cal "
				+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
				+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
				+ "where tp1.id=tp.id and u1.id=tp.especialista1 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp1, "
				+ "(select cal.calificacion from exetasi.calificaciones cal "
				+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
				+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
				+ "where tp1.id=tp.id and u1.id=tp.especialista2 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp2, "
				+ "(select cal.calificacion from exetasi.calificaciones cal "
				+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
				+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
				+ "where tp1.id=tp.id and u1.id=tp.especialista3 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp3,"
				+ "0.00 as espSupl1, 0.00 as espSupl2, 0.00 as totalCriterio " + "from exetasi.calificaciones c "
				+ "inner join exetasi.\"temasPracticos\" tp on(c.\"temaPractico\"=tp.id) "
				+ "inner join exetasi.\"tiposExamenes\" te on(c.\"tipoExamen\"=te.id) "
				+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
				+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
				+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) " + "where tp.id="
				+ temaPractico.getId() + " and te.id='" + (consultaOrdGra == 1 ? "OR" : "GR")
				+ "' and fp.id='ESC' order by cr.id";
		String consultaCartillaOral = "select distinct cr.id as id, cr.nombre as criterio, "
				+ "(select cal.calificacion from exetasi.calificaciones cal "
				+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
				+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
				+ "where tp1.id=tp.id and u1.id=tp.especialista1 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp1, "
				+ "(select cal.calificacion from exetasi.calificaciones cal "
				+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
				+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
				+ "where tp1.id=tp.id and u1.id=tp.especialista2 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp2, "
				+ "(select cal.calificacion from exetasi.calificaciones cal "
				+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
				+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
				+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
				+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
				+ "where tp1.id=tp.id and u1.id=tp.especialista3 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp3, "
				+ "0.00 as espSupl1, 0.00 as espSupl2, 0.00 as totalCriterio " + "from exetasi.calificaciones c "
				+ "inner join exetasi.\"temasPracticos\" tp on(c.\"temaPractico\"=tp.id) "
				+ "inner join exetasi.\"tiposExamenes\" te on(c.\"tipoExamen\"=te.id) "
				+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
				+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
				+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) " + "where tp.id="
				+ temaPractico.getId() + " and te.id='" + (consultaOrdGra == 1 ? "OR" : "GR")
				+ "' and fp.id='ORA' order by cr.id";
		listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaEsc, Cartilla.class);
		for (Cartilla c : listCartillaEsc) {
			c.setTotalCriterio(UtilsMath.divideCalificaciones(c.getEsp1().add(c.getEsp2()).add(c.getEsp3()), 3));
		}
		listCartillaOral = cartillaService.obtenerPorSql(consultaCartillaOral, Cartilla.class);
		for (Cartilla c : listCartillaOral) {
			c.setTotalCriterio(UtilsMath.divideCalificaciones(c.getEsp1().add(c.getEsp2()).add(c.getEsp3()), 3));
		}
		if (consultaOrdGra == 1) {
			totalEsc = temaPractico.getCalificacionEscritaOrdinaria();
			totalOral = temaPractico.getCalificacionOralOrdinaria();
		} else {
			totalEsc = temaPractico.getCalificacionEscritaGracia();
			totalOral = temaPractico.getCalificacionOralGracia();
		}
	}

	public void eliminarCalificacion() {
		if (temaPractico != null && temaPractico.getId() != null) {
			if (consultaOrdGra == 1) {
				temaPractico.setCalificacionEscritaOrdinaria(null);
				temaPractico.setCalificacionOralOrdinaria(null);
			} else {
				temaPractico.setCalificacionEscritaGracia(null);
				temaPractico.setCalificacionOralGracia(null);
			}

			if (consultaOrdGra == 1) {
				for (Calificacion c : temaPractico.getCalificaciones())
					calificacionService.eliminar(c);
				temaPractico.setCalificaciones(null);
			} else
				for (int i = 0; i < temaPractico.getCalificaciones().size(); i++) {
					Calificacion c = temaPractico.getCalificaciones().get(i);
					if (c.getTipoExamen().getId().compareToIgnoreCase("GR") == 0) {
						temaPractico.getCalificaciones().remove(c);
						i--;
					}
				}
			temaPracticoService.actualizar(temaPractico);
			listTemaPracticos.remove(temaPractico);
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

	public int getConsultaOrdGra() {
		return consultaOrdGra;
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

	public List<EstudianteExamenComplexivoPP> getListTemaPracticos() {
		return this.listTemaPracticos;
	}

	public EstudianteExamenComplexivoPP getTemaPractico() {
		return this.temaPractico;
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

	public void setConsultaOrdGra(int consultaOrdGra) {
		this.consultaOrdGra = consultaOrdGra;
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

	public void setListTemaPracticos(List<EstudianteExamenComplexivoPP> listTemaPracticos) {
		this.listTemaPracticos = listTemaPracticos;
	}

	public void setTemaPractico(EstudianteExamenComplexivoPP temaPractico) {
		this.temaPractico = temaPractico;
	}

	public void setTotalEsc(BigDecimal totalEsc) {
		this.totalEsc = totalEsc;
	}

	public void setTotalOral(BigDecimal totalOral) {
		this.totalOral = totalOral;
	}

}
