package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.PreguntaHistoricoDao;
import ec.edu.utmachala.titulacion.entity.PosibleRespuesta;
import ec.edu.utmachala.titulacion.entity.PosibleRespuestaHistorico;
import ec.edu.utmachala.titulacion.entity.Pregunta;
import ec.edu.utmachala.titulacion.entity.PreguntaHistorico;

@Service
public class PreguntaHistoricoServiceImpl implements PreguntaHistoricoService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private PreguntaHistoricoDao preguntaHistoricoDao;

	private boolean comprobarRespuesta(PosibleRespuestaHistorico prh, List<PosibleRespuestaHistorico> list) {
		for (int i = 0; i < list.size(); i++) {
			PosibleRespuestaHistorico prhAux = list.get(i);
			if (prh.getDescripcion().compareToIgnoreCase(prhAux.getDescripcion()) == 0
					&& prh.getLiteral().compareToIgnoreCase(prhAux.getLiteral()) == 0)
				return true;
		}
		return false;
	}

	public PreguntaHistorico insertar(Pregunta p) {
		PreguntaHistorico ph = new PreguntaHistorico();
		ph.setBibliografia(p.getBibliografia());
		ph.setEjeTematico(p.getEjeTematico());
		ph.setEnunciado(p.getEnunciado());
		ph.setImagenEnunciado(p.getImagenEnunciado());
		ph.setImagenJustificacion(p.getImagenJustificacion());
		ph.setJustificacion(p.getJustificacion());
		ph.setSostiApantisi(p.getSostiApantisi());
		ph.setTiempo(p.getNivelDificultad().getTiempo());
		ph.setActivo(p.getActivo());
		ph.setUnidadDidactica(p.getUnidadDidactica().getId());
		ph.setUnidadesDidacticas(p.getUnidadDidactica().getNombre());
		ph.setAsignatura(p.getUnidadDidactica().getAsignatura().getNombre());
		ph.setUnidadesCurriculares(p.getUnidadDidactica().getAsignatura().getUnidadCurricular().getNombre());
		ph.setNivelDificultad(p.getNivelDificultad().getId());
		ph.setDocente(p.getUnidadDidactica().getAsignatura().getDocentesAsignaturas().get(0).getDocente());
		ph.setPosiblesRespuestas(new ArrayList<PosibleRespuestaHistorico>());
		for (PosibleRespuesta pr : p.getPosiblesRespuestas()) {
			PosibleRespuestaHistorico prh = new PosibleRespuestaHistorico();
			prh.setDescripcion(pr.getDescripcion());
			prh.setImagenDescripcion(pr.getImagenDescripcion());
			prh.setLiteral(pr.getLiteral());
			prh.setPregunta(ph);
			ph.getPosiblesRespuestas().add(prh);
		}

		PreguntaHistorico phAux = obtenerPorEnunciadoAndRespuesta(ph.getEnunciado(), ph.getSostiApantisi());
		if (phAux == null || phAux.getId() == null) {
			preguntaHistoricoDao.insertar(ph);
		} else if (phAux != null && phAux.getId() != null) {
			boolean prh1 = comprobarRespuesta(ph.getPosiblesRespuestas().get(0), phAux.getPosiblesRespuestas());
			boolean prh2 = comprobarRespuesta(ph.getPosiblesRespuestas().get(1), phAux.getPosiblesRespuestas());
			boolean prh3 = comprobarRespuesta(ph.getPosiblesRespuestas().get(2), phAux.getPosiblesRespuestas());
			boolean prh4 = comprobarRespuesta(ph.getPosiblesRespuestas().get(3), phAux.getPosiblesRespuestas());
			if (prh1 == false || prh2 == false || prh3 == false || prh4 == false) {
				preguntaHistoricoDao.insertar(ph);
			} else {
				return phAux;
			}
		} else {
			return phAux;
		}
		return ph;

	}

	public PreguntaHistorico obtenerPorEnunciadoAndRespuesta(String enunciado, String respuesta) {
		return preguntaHistoricoDao.obtenerObjeto(
				"select p from PreguntaHistorico p inner join fetch p.posiblesRespuestas pr "
						+ "where p.enunciado=?1 and p.sostiApantisi=?2",
				new Object[] { enunciado, respuesta }, false, new Object[] {});
	}

	public PreguntaHistorico obtenerPorId(Integer id) {
		return preguntaHistoricoDao.obtenerObjeto(
				"select p from PreguntaHistorico p inner join fetch p.posiblesRespuestas pr where p.id=?1",
				new Object[] { id }, false, new Object[] {});
	}

}