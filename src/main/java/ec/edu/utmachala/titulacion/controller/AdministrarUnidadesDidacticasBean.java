package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Asignatura;
import ec.edu.utmachala.titulacion.entity.UnidadDidactica;
import ec.edu.utmachala.titulacion.service.AsignaturaService;
import ec.edu.utmachala.titulacion.service.UnidadDidacticaService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class AdministrarUnidadesDidacticasBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private AsignaturaService asignaturaService;

	@Autowired
	private UnidadDidacticaService unidadDidacticaService;

	private List<Asignatura> listAsignaturas;
	private Asignatura asignaturaSeleccionada;
	private int asignatura;
	private int estado;

	private List<UnidadDidactica> listUnidadesDidacticas;
	private UnidadDidactica unidadDidactica;

	public void actualizar() {
		if (unidadDidactica.getNombre().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el nombre de la unidad didáctica");
		else {
			unidadDidacticaService.actualizar(unidadDidactica);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualizó correctamente la unidad didáctica",
					"cerrar", true);
			buscar();
		}
	}

	public void buscar() {
		listUnidadesDidacticas = unidadDidacticaService.obtenerLista(
				"select ud from UnidadDidactica ud inner join ud.asignatura a where a.id=?1",
				new Object[] { asignatura }, 0, false, new Object[0]);
	}

	public void eliminar(UnidadDidactica unidadDidactica) {
		if (unidadDidactica.getActivo()) {
			unidadDidactica.setActivo(false);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Desactivó la unidad didáctica");
		} else {
			unidadDidactica.setActivo(true);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Activó la unidad didáctica");
		}
		unidadDidacticaService.actualizar(unidadDidactica);
		buscar();
	}

	public int getAsignatura() {
		return asignatura;
	}

	public Asignatura getAsignaturaSeleccionada() {
		return asignaturaSeleccionada;
	}

	public int getEstado() {
		return estado;
	}

	public int getId(Asignatura asignatura) {
		return System.identityHashCode(asignatura);
	}

	public List<Asignatura> getListAsignaturas() {
		return listAsignaturas;
	}

	public List<UnidadDidactica> getListUnidadesDidacticas() {
		return listUnidadesDidacticas;
	}

	public UnidadDidactica getUnidadDidactica() {
		return unidadDidactica;
	}

	@PostConstruct
	public void init() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		listAsignaturas = asignaturaService.obtenerLista(
				"select a from Asignatura a inner join a.docentesAsignaturas da "
						+ "inner join da.docente d where d.email=?1 and a.activo=true",
				new Object[] { email }, 0, false, new Object[0]);
		if (listAsignaturas.size() == 1) {
			asignatura = listAsignaturas.get(0).getId();
		}
	}

	public void insertar() {
		if (unidadDidactica.getNombre().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el nombre de la unidad didáctica");
		else {
			unidadDidactica.setAsignatura(new Asignatura());
			unidadDidactica.getAsignatura().setId(asignatura);
			unidadDidacticaService.insertar(unidadDidactica);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Insertó correctamente la unidad didáctica",
					"cerrar", true);
			buscar();
		}
	}

	public void limpiar() {
		unidadDidactica = new UnidadDidactica();
	}

	public void onRowSelect(SelectEvent event) {
		asignatura = asignaturaSeleccionada.getId();
		buscar();
	}

	public void setAsignatura(int asignatura) {
		this.asignatura = asignatura;
	}

	public void setAsignaturaSeleccionada(Asignatura asignaturaSeleccionada) {
		this.asignaturaSeleccionada = asignaturaSeleccionada;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public void setListAsignaturas(List<Asignatura> listAsignaturas) {
		this.listAsignaturas = listAsignaturas;
	}

	public void setListUnidadesDidacticas(List<UnidadDidactica> listUnidadesDidacticas) {
		this.listUnidadesDidacticas = listUnidadesDidacticas;
	}

	public void setUnidadDidactica(UnidadDidactica unidadDidactica) {
		this.unidadDidactica = unidadDidactica;
	}
}
