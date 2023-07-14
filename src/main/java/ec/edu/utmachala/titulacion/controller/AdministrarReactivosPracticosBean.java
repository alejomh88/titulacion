package ec.edu.utmachala.titulacion.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.DocenteAsignatura;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.PosibleRespuesta;
import ec.edu.utmachala.titulacion.entity.Pregunta;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.CarreraMallaProcesoAux;
import ec.edu.utmachala.titulacion.service.CarreraMallaProcesoAuxService;
import ec.edu.utmachala.titulacion.service.DocenteAsignaturaService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.PreguntaService;
import ec.edu.utmachala.titulacion.service.UnidadDidacticaService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class AdministrarReactivosPracticosBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private DocenteAsignatura docenteAsignatura;

	private DocenteAsignatura docenteAsignaturaSeleccionada;
	@Autowired
	private DocenteAsignaturaService docenteAsignaturaService;

	@Autowired
	private CarreraMallaProcesoAuxService carreraMallaProcesoAuxService;

	@Autowired
	private UsuarioService usuarioService;

	private int estado;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	private List<DocenteAsignatura> listDocenteAsignatura;
	private List<EstudianteExamenComplexivoPP> listTemaPracticos;
	private List<CarreraMallaProcesoAux> listCMPA;
	private CarreraMallaProcesoAux carreraMallaProcesoAux;

	@Autowired
	private PreguntaService preguntaService;

	private EstudianteExamenComplexivoPP temaPractico;

	@Autowired
	private UnidadDidacticaService unidadDidacticaService;

	@PostConstruct
	public void a() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		listCMPA = carreraMallaProcesoAuxService.obtenerPorSql(
				"select distinct a.id as id, mc.id as \"mallaCurricular\", (select string_agg(distinct ps.id||' ('||ps.observacion||')',', ')from procesos ps inner join \"carrerasMallasProcesos\" cmps on (cmps.proceso=ps.id) inner join \"mallasCurriculares\" mcs on(cmps.\"mallaCurricular\"=mcs.id) inner join asignaturas asig on (asig.\"mallaCurricular\"=mcs.id) where asig.id=a.id) as proceso, (select string_agg(distinct cas.nombre||' - '||cas.id,', ')from carreras cas inner join \"carrerasMallasProcesos\" camps on (camps.carrera=cas.id) inner join \"mallasCurriculares\" macs on(camps.\"mallaCurricular\"=macs.id) inner join asignaturas asigs on (asigs.\"mallaCurricular\"=macs.id) where asigs.id=a.id) as carrera, a.nombre||'-'||a.id as asignatura from \"carrerasMallasProcesos\" cmp inner join procesos p on (p.id=cmp.proceso) inner join carreras c on (c.id=cmp.carrera) inner join \"mallasCurriculares\" mc on (mc.id=cmp.\"mallaCurricular\") inner join asignaturas a on (a.\"mallaCurricular\"=mc.id) inner join \"docentesAsignaturas\" da on (da.asignatura=a.id) inner join usuarios d on(da.docente=d.id) where d.email='"
						+ email
						+ "' and (mc.id>40 or mc.id=14 or mc.id=10 or mc.id=12 or mc.id=25 or mc.id=30) and da.activo=true and a.\"unidadCurricular\"='PR' order by \"mallaCurricular\";",
				CarreraMallaProcesoAux.class);
	}

	public void actualizar() {
		if (temaPractico.getCasoInvestigacion().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No puede ingresar espacios en blanco");
		else {
			estudiantesExamenComplexivoPPService.actualizar(temaPractico);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualizó correctamente el reactivo práctico",
					"cerrar", true);
		}
		buscar();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("administrarReactivosPracticos.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void buscar() {
		listTemaPracticos = estudiantesExamenComplexivoPPService.obtenerLista(
				"select pp from EstudianteExamenComplexivoPP pp inner join pp.docenteAsignatura da where da.id=?1",
				new Object[] { docenteAsignatura.getId() }, 0, false, new Object[0]);
	}

	public void clonar() {
		List<Pregunta> listaPreguntas = new ArrayList<Pregunta>();

		listaPreguntas = preguntaService.obtenerLista(
				"select distinct p from Pregunta p inner join p.unidadDidactica ud inner join fetch p.posiblesRespuestas where ud.id=?1",
				new Object[] { 4919 }, 0, false, new Object[0]);

		for (Pregunta pregunta : listaPreguntas) {
			Pregunta p = new Pregunta();
			p.setActivo(pregunta.getActivo());
			p.setBibliografia(pregunta.getBibliografia());
			p.setEjeTematico(pregunta.getEjeTematico());
			p.setEnunciado(pregunta.getEnunciado());
			p.setImagenEnunciado(pregunta.getImagenEnunciado());
			p.setImagenJustificacion(pregunta.getImagenJustificacion());
			p.setJustificacion(pregunta.getJustificacion());
			p.setNivelDificultad(pregunta.getNivelDificultad());
			p.setPosiblesRespuestas(pregunta.getPosiblesRespuestas());
			p.setSostiApantisi(pregunta.getSostiApantisi());
			p.setTiempo(pregunta.getTiempo());
			p.setUnidadDidactica(unidadDidacticaService.obtenerObjeto(
					"select ud from UnidadDidactica ud where ud.id=?1", new Object[] { 7003 }, false, new Object[0]));

			List<PosibleRespuesta> lpr = new ArrayList<PosibleRespuesta>();
			PosibleRespuesta pr;
			for (PosibleRespuesta pra : pregunta.getPosiblesRespuestas()) {
				pr = new PosibleRespuesta();
				pr.setDescripcion(pra.getDescripcion());
				pr.setImagenDescripcion(pra.getImagenDescripcion());
				pr.setLiteral(pra.getLiteral());
				pr.setPregunta(p);
				lpr.add(pr);
			}
			p.setPosiblesRespuestas(lpr);
			preguntaService.insertar(p);
		}
	}

	public void editar() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("editarReactivosPracticos.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void eliminar(EstudianteExamenComplexivoPP tp) {
		if (tp.getActivo())
			tp.setActivo(false);
		else
			tp.setActivo(true);
		estudiantesExamenComplexivoPPService.actualizar(tp);
		buscar();
	}

	public CarreraMallaProcesoAux getCarreraMallaProcesoAux() {
		return carreraMallaProcesoAux;
	}

	public DocenteAsignatura getDocenteAsignatura() {
		return docenteAsignatura;
	}

	public DocenteAsignatura getDocenteAsignaturaSeleccionada() {
		return docenteAsignaturaSeleccionada;
	}

	public int getEstado() {
		return estado;
	}

	public List<CarreraMallaProcesoAux> getListCMPA() {
		return listCMPA;
	}

	public List<DocenteAsignatura> getListDocenteAsignatura() {
		return listDocenteAsignatura;
	}

	public List<EstudianteExamenComplexivoPP> getListTemaPracticos() {
		return listTemaPracticos;
	}

	public EstudianteExamenComplexivoPP getTemaPractico() {
		return temaPractico;
	}

	public void insertar() {
		if (temaPractico.getCasoInvestigacion().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No puede ingresar espacios en blanco");
		else {
			estudiantesExamenComplexivoPPService.insertar(temaPractico);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Insertó correctamente el reactivo práctico",
					"cerrar", true);
		}
		buscar();
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("administrarReactivosPracticos.jsf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void limpiar() {
		temaPractico = new EstudianteExamenComplexivoPP();
		Usuario u = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1",
				new Object[] { UtilSeguridad.obtenerUsuario() }, false, new Object[0]);
		System.out.println("Id de asignatura: " + Integer.valueOf(carreraMallaProcesoAux.getAsignatura()
				.split("-")[carreraMallaProcesoAux.getAsignatura().split("-").length - 1].trim()));
		temaPractico.setDocenteAsignatura(docenteAsignaturaService.obtenerObjeto(
				"select da from DocenteAsignatura da inner join da.docente d inner join da.asignatura a where d.id=?1 and a.id=?2",
				new Object[] { u.getId(),
						Integer.valueOf(carreraMallaProcesoAux.getAsignatura()
								.split("-")[carreraMallaProcesoAux.getAsignatura().split("-").length - 1].trim()) },
				false, new Object[0]));

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("insertarReactivosPracticos.jsf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onRowSelect(SelectEvent event) {
		docenteAsignatura = docenteAsignaturaService.obtenerObjeto(
				"select da from DocenteAsignatura da inner join da.asignatura a inner join da.docente d where a.id=?1 and d.email=?2",
				new Object[] {
						Integer.parseInt(carreraMallaProcesoAux.getAsignatura()
								.split("-")[carreraMallaProcesoAux.getAsignatura().split("-").length - 1].trim()),
						UtilSeguridad.obtenerUsuario() },
				false, new Object[0]);
		buscar();
	}

	public void setCarreraMallaProcesoAux(CarreraMallaProcesoAux carreraMallaProcesoAux) {
		this.carreraMallaProcesoAux = carreraMallaProcesoAux;
	}

	public void setDocenteAsignatura(DocenteAsignatura docenteAsignatura) {
		this.docenteAsignatura = docenteAsignatura;
	}

	public void setDocenteAsignaturaSeleccionada(DocenteAsignatura docenteAsignaturaSeleccionada) {
		this.docenteAsignaturaSeleccionada = docenteAsignaturaSeleccionada;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public void setListCMPA(List<CarreraMallaProcesoAux> listCMPA) {
		this.listCMPA = listCMPA;
	}

	public void setListDocenteAsignatura(List<DocenteAsignatura> listDocenteAsignatura) {
		this.listDocenteAsignatura = listDocenteAsignatura;
	}

	public void setListTemaPracticos(List<EstudianteExamenComplexivoPP> listTemaPracticos) {
		this.listTemaPracticos = listTemaPracticos;
	}

	public void setTemaPractico(EstudianteExamenComplexivoPP temaPractico) {
		this.temaPractico = temaPractico;
	}

}
