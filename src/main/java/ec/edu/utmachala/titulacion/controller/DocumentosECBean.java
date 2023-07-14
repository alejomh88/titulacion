package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class DocumentosECBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudiantesExamenComplexivoPPService temaPracticoService;

	private List<Carrera> listCarreras;
	private Integer carrera;
	private int consultaABC;

	private List<EstudianteExamenComplexivoPP> listTemaPracticos;
	private EstudianteExamenComplexivoPP temaPractico;

	private String consultaAI = "and (e.apellidoPaterno like 'A%' or e.apellidoPaterno like 'B%' or e.apellidoPaterno like 'C%' or e.apellidoPaterno like 'D%' or e.apellidoPaterno like 'E%' or e.apellidoPaterno like 'F%' or e.apellidoPaterno like 'G%' or e.apellidoPaterno like 'H%' or e.apellidoPaterno like 'I%') ";
	private String consultaJQ = "and (e.apellidoPaterno like 'J%' or e.apellidoPaterno like 'K%' or e.apellidoPaterno like 'L%' or e.apellidoPaterno like 'M%' or e.apellidoPaterno like 'N%' or e.apellidoPaterno like 'Ñ%' or e.apellidoPaterno like 'O%' or e.apellidoPaterno like 'P%' or e.apellidoPaterno like 'Q%') ";
	private String consultaRZ = "and (e.apellidoPaterno like 'R%' or e.apellidoPaterno like 'S%' or e.apellidoPaterno like 'T%' or e.apellidoPaterno like 'U%' or e.apellidoPaterno like 'V%' or e.apellidoPaterno like 'W%' or e.apellidoPaterno like 'X%' or e.apellidoPaterno like 'Y%' or e.apellidoPaterno like 'Z%') ";

	public void buscar() {
		this.listTemaPracticos = this.temaPracticoService.obtenerLista(
				"select tp from TemaPractico tp inner join tp.estudiantePeriodo ep inner join ep.estudiante e inner join ep.periodoExamen pe inner join pe.proceso p inner join pe.carrera c where c.id=?1 and tp.estudiantePeriodo is not null and p.fechaInicio<=?2 and p.fechaCierre>?2 "
						+ (this.consultaABC == 2 ? this.consultaJQ
								: this.consultaABC == 1 ? this.consultaAI : this.consultaRZ)
						+ "order by e.apellidoPaterno, e.apellidoMaterno, e.nombre",
				new Object[] { this.carrera, UtilsDate.timestamp() }, Integer.valueOf(0), false, new Object[0]);
	}

	public Integer getCarrera() {
		return this.carrera;
	}

	public int getConsultaABC() {
		return this.consultaABC;
	}

	public List<Carrera> getListCarreras() {
		return this.listCarreras;
	}

	public List<EstudianteExamenComplexivoPP> getListTemaPracticos() {
		return this.listTemaPracticos;
	}

	public EstudianteExamenComplexivoPP getTemaPractico() {
		return this.temaPractico;
	}

	@PostConstruct
	public void init() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		this.listCarreras = this.carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { email }, Integer.valueOf(0), false, new Object[0]);

		if (this.listCarreras.size() == 1) {
			this.carrera = ((Carrera) this.listCarreras.get(0)).getId();
		}
	}

	public void insertarDocumentos() {
		temaPracticoService.actualizar(temaPractico);
	}

	public void limpiar() {
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setConsultaABC(int consultaABC) {
		this.consultaABC = consultaABC;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListTemaPracticos(List<EstudianteExamenComplexivoPP> listTemaPracticos) {
		this.listTemaPracticos = listTemaPracticos;
	}

	public void setTemaPractico(EstudianteExamenComplexivoPP temaPractico) {
		this.temaPractico = temaPractico;
	}

}
