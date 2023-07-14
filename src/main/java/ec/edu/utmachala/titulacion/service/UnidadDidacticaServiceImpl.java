package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.UnidadDidacticaDao;
import ec.edu.utmachala.titulacion.entity.UnidadDidactica;

@Service
public class UnidadDidacticaServiceImpl extends GenericServiceImpl<UnidadDidactica, Integer>
		implements UnidadDidacticaService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UnidadDidacticaDao unidadDidacticaDao;

	public void actualizar(UnidadDidactica unidadDidactica) {
		unidadDidacticaDao.actualizar(unidadDidactica);
	}

	public void eliminar(UnidadDidactica unidadDidactica) {
		unidadDidacticaDao.eliminar(unidadDidactica);
	}

	public void insertar(UnidadDidactica unidadDidactica) {
		unidadDidactica.setActivo(true);
		unidadDidacticaDao.insertar(unidadDidactica);
	}

	public UnidadDidactica obtener(String carrera, String asignatura, String nombre) {
		System.out.println(carrera);
		System.out.println(asignatura);
		System.out.println(nombre);
		return unidadDidacticaDao.obtenerObjeto(
				"select ud from UnidadDidactica ud inner join ud.asignatura a inner join a.mallaCurricular mc "
						+ "inner join mc.carrera c left join fetch ud.preguntas where c.nombre=?1 and a.nombre=?2 and ud.nombre=?3",
				new Object[] { carrera, asignatura, nombre }, false, new Object[] {});
	}

}