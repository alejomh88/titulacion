package ec.edu.utmachala.titulacion.entityAux;

import java.util.List;

import ec.edu.utmachala.titulacion.entity.Criterio;
import ec.edu.utmachala.titulacion.entity.CriterioTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Indicador;
import ec.edu.utmachala.titulacion.entity.IndicadorTrabajoTitulacion;

public class CalificacionAux {

	private Criterio criterio;
	private List<Indicador> listIndicador;
	private Indicador indicador;

	private CriterioTrabajoTitulacion criterioTrabajoTitulacion;
	private List<IndicadorTrabajoTitulacion> listIndicadorTrabajoTitulaciones;
	private IndicadorTrabajoTitulacion indicadorTrabajoTitulacion;

	public CalificacionAux(Criterio criterio, List<Indicador> listIndicador, Indicador indicador) {
		this.criterio = criterio;
		this.listIndicador = listIndicador;
		this.indicador = indicador;
	}

	public CalificacionAux(CriterioTrabajoTitulacion criterioTrabajoTitulacion,
			List<IndicadorTrabajoTitulacion> listIndicadorTrabajoTitulaciones,
			IndicadorTrabajoTitulacion indicadorTrabajoTitulacion) {
		this.criterioTrabajoTitulacion = criterioTrabajoTitulacion;
		this.listIndicadorTrabajoTitulaciones = listIndicadorTrabajoTitulaciones;
		this.indicadorTrabajoTitulacion = indicadorTrabajoTitulacion;
	}

	public Criterio getCriterio() {
		return criterio;
	}

	public CriterioTrabajoTitulacion getCriterioTrabajoTitulacion() {
		return criterioTrabajoTitulacion;
	}

	public Indicador getIndicador() {
		return indicador;
	}

	public IndicadorTrabajoTitulacion getIndicadorTrabajoTitulacion() {
		return indicadorTrabajoTitulacion;
	}

	public List<Indicador> getListIndicador() {
		return listIndicador;
	}

	public List<IndicadorTrabajoTitulacion> getListIndicadorTrabajoTitulaciones() {
		return listIndicadorTrabajoTitulaciones;
	}

	public void setCriterio(Criterio criterio) {
		this.criterio = criterio;
	}

	public void setCriterioTrabajoTitulacion(CriterioTrabajoTitulacion criterioTrabajoTitulacion) {
		this.criterioTrabajoTitulacion = criterioTrabajoTitulacion;
	}

	public void setIndicador(Indicador indicador) {
		this.indicador = indicador;
	}

	public void setIndicadorTrabajoTitulacion(IndicadorTrabajoTitulacion indicadorTrabajoTitulacion) {
		this.indicadorTrabajoTitulacion = indicadorTrabajoTitulacion;
	}

	public void setListIndicador(List<Indicador> listIndicador) {
		this.listIndicador = listIndicador;
	}

	public void setListIndicadorTrabajoTitulaciones(List<IndicadorTrabajoTitulacion> listIndicadorTrabajoTitulaciones) {
		this.listIndicadorTrabajoTitulaciones = listIndicadorTrabajoTitulaciones;
	}

}
