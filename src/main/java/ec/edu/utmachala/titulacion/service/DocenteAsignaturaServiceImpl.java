package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.DocenteAsignaturaDao;
import ec.edu.utmachala.titulacion.entity.DocenteAsignatura;

@Service
public class DocenteAsignaturaServiceImpl extends GenericServiceImpl<DocenteAsignatura, Integer>
		implements DocenteAsignaturaService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private DocenteAsignaturaDao docenteAsignaturaDao;

	public void actualizar(DocenteAsignatura docenteAsignatura) {
		docenteAsignaturaDao.actualizar(docenteAsignatura);
	}

	public void eliminar(DocenteAsignatura docenteAsignatura) {
		docenteAsignaturaDao.eliminar(docenteAsignatura);
	}

	public void insertar(DocenteAsignatura docenteAsignatura) {
		docenteAsignatura.setActivo(true);
		docenteAsignaturaDao.insertar(docenteAsignatura);
	}

	public DocenteAsignatura obtenerPorDocenteAsignaturaCarrera(String docente, String carrera, String asignatura) {
		return docenteAsignaturaDao.obtenerObjeto(
				"select da from DocenteAsignatura da left join fetch da.temasPracticos "
						+ "inner join da.docente d inner join da.asignatura a "
						+ "inner join da.periodoReactivo pr inner join pr.carrera c "
						+ "where d.id=?1 and c.nombre=?2 and a.nombre=?3",
				new Object[] { docente, carrera, asignatura }, false, new Object[] {});
	}

}