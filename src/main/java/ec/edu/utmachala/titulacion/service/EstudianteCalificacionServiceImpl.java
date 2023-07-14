package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.EstudianteCalificacion;

@Service
public class EstudianteCalificacionServiceImpl extends GenericServiceImpl<EstudianteCalificacion, String>
		implements EstudianteCalificacionService, Serializable {

	private static final long serialVersionUID = 1L;

	public List<EstudianteCalificacion> quicksortCalificacion(List<EstudianteCalificacion> list, int iz, int de) {
		if (iz >= de)
			return list;
		int i = iz;
		int d = de;
		if (iz != de) {
			EstudianteCalificacion epr;
			int pivote = iz;
			while (iz != de) {
				while (list.get(de).getCalTotal().compareTo(list.get(pivote).getCalTotal()) <= 0 && i < de)
					de--;
				while (list.get(iz).getCalTotal().compareTo(list.get(pivote).getCalTotal()) > 0 && i < de)
					iz++;
				if (de != iz) {
					epr = list.get(de);
					list.set(de, list.get(iz));
					list.set(iz, epr);
				}
				if (iz == de) {
					quicksortCalificacion(list, i, iz - 1);
					quicksortCalificacion(list, iz + 1, d);
				}
			}
		}
		return list;
	}

	public List<EstudianteCalificacion> quicksortNombre(List<EstudianteCalificacion> list, int iz, int de) {
		if (iz >= de)
			return list;
		int i = iz;
		int d = de;
		if (iz != de) {
			EstudianteCalificacion epr;
			int pivote = iz;
			while (iz != de) {
				while (list.get(de).getEstudiante().compareTo(list.get(pivote).getEstudiante()) >= 0 && i < de)
					de--;
				while (list.get(iz).getEstudiante().compareTo(list.get(pivote).getEstudiante()) < 0 && i < de)
					iz++;
				if (de != iz) {
					epr = list.get(de);
					list.set(de, list.get(iz));
					list.set(iz, epr);
				}
				if (iz == de) {
					quicksortNombre(list, i, iz - 1);
					quicksortNombre(list, iz + 1, d);
				}
			}
		}
		return list;
	}

}