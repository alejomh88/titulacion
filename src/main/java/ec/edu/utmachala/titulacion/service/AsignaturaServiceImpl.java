package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.AsignaturaDao;
import ec.edu.utmachala.titulacion.entity.Asignatura;

@Service
public class AsignaturaServiceImpl extends GenericServiceImpl<Asignatura, Integer>
		implements AsignaturaService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private AsignaturaDao asignaturaDao;

	public void actualizar(Asignatura asignatura) {
		asignaturaDao.actualizar(asignatura);
	}

	public void insertar(Asignatura asignatura) {
		asignatura.setActivo(true);
		asignaturaDao.insertar(asignatura);
	}

	public void insertar(List<Asignatura> asignaturas) {
		for (Asignatura asignatura : asignaturas) {
			asignaturaDao.insertar(asignatura);
		}
	}

	public Asignatura obtenerPorNombreYCarrera(String nombre, String carrera) {
		System.out.println("++");
		System.out.println(nombre);
		System.out.println(carrera);
		System.out.println("++");
		return asignaturaDao.obtenerObjeto(
				"select a from Asignatura a inner join a.mallaCurricular mc inner join mc.carrera c left join fetch a.unidadesDidacticas ud "
						+ "left join ud.preguntas p where a.nombre=?1 and c.nombre=?2",
				new Object[] { nombre, carrera }, false, new Object[] {});
		// return asignaturaDao
		// .obtenerObjeto(
		// "select a from Asignatura a inner join a.mallaCurricular mc inner
		// join mc.carrera c left join fetch a.unidadesDidacticas ud "
		// + "where a.nombre=?1 and c.nombre=?2",
		// new Object[] { nombre, carrera }, false,
		// new Object[] {});
	}
}