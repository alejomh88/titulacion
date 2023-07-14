package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface GenericDao<T, K extends Serializable> {

	boolean actualizar(T t);

	void actualizarSQL(String sentencia);

	void actualizarSQL_Actualizar(String sentencia);

	void eliminar(T t);

	void insertar(T t);

	List<T> obtenerLista(String consulta, Object[] valoresConsulta, Integer registrosMax, boolean mensaje,
			Object[] valoresInicializar);

	List<T> obtenerListaDirecta(String consulta, Object[] valoresConsulta, Integer registrosMax, boolean mensaje,
			Object[] valoresInicializar);

	T obtenerObjeto(String consulta, Object[] valoresConsulta, boolean mensaje, Object[] valoresInicializar);

	List<T> obtenerPorSql(String consulta, Class<T> type);

	Session session();
}