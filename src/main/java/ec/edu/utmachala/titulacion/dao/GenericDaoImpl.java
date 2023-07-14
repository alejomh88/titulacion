package ec.edu.utmachala.titulacion.dao;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GenericDaoImpl<T, K extends Serializable> implements GenericDao<T, K> {

	@Autowired
	public SessionFactory sessionFactory;

	public boolean actualizar(T t) {
		try {
			Field[] field = t.getClass().getDeclaredFields();
			for (int i = 0; i < field.length; i++) {
				if (field[i].getType() == new String().getClass() && field[i].getName().compareTo("password") != 0) {
					field[i].setAccessible(true);
					String obj = String.valueOf(field[i].get(t));
					if (obj.isEmpty() || obj.trim().compareTo("null") == 0)
						obj = null;
					field[i].set(t, obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session().update(t);
		// presentaMensaje(FacesMessage.SEVERITY_INFO, "ACTUALIZÓ EL REGISTRO
		// CON ÉXITO", "cerrar", true);
		return true;
	}

	public void actualizarSQL(String sentencia) {
		try {
			SQLQuery sqlQuery = session().createSQLQuery(sentencia);
			sqlQuery.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void actualizarSQL_Actualizar(String sentencia) {
		try {
			SQLQuery sqlQuery = session().createSQLQuery(sentencia);
			sqlQuery.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void eliminar(T t) {
		session().delete(t);
	}

	public void insertar(T t) {
		try {
			Field[] field = t.getClass().getDeclaredFields();
			for (int i = 0; i < field.length; i++) {
				if (field[i].getType() == new String().getClass() && field[i].getName().compareTo("password") != 0) {
					field[i].setAccessible(true);
					String obj = String.valueOf(field[i].get(t));
					if (obj.isEmpty())
						obj = null;
					field[i].set(t, obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		session().save(t);
		// presentaMensaje(FacesMessage.SEVERITY_INFO, "INSERTÓ EL REGISTRO CON
		// ÉXITO", "cerrar", true);
	}

	@SuppressWarnings("unchecked")
	public List<T> obtenerLista(String consulta, Object[] valoresConsulta, Integer registrosMax, boolean mensaje,
			Object[] valoresInicializar) {
		boolean validacion = false;
		List<T> list = null;
		Query query = (Query) session().createQuery(consulta);
		if (valoresConsulta != null)
			for (int i = 0; i < valoresConsulta.length; i++)
				if (valoresConsulta[i] != null) {
					if (valoresConsulta[i] instanceof String) {
						if ((String.valueOf(valoresConsulta[i]).startsWith("%")
								&& String.valueOf(valoresConsulta[i]).endsWith("%")
								&& String.valueOf(valoresConsulta[i]).length() >= 5)
								|| (!String.valueOf(valoresConsulta[i]).startsWith("%")
										&& !String.valueOf(valoresConsulta[i]).endsWith("%")
										&& String.valueOf(valoresConsulta[i]).length() >= 3)) {
							valoresConsulta[i] = String.valueOf(valoresConsulta[i]);
						} else {
							validacion = true;
							break;
						}
					}
					query.setParameter(String.valueOf(i + 1), valoresConsulta[i]);
				}
		if (validacion)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "PARA CUALQUIER BÚSQUEDA DEBE INGRESAR MAS DE 2 CARACTERES");
		else {
			if (registrosMax > 0)
				query.setMaxResults(registrosMax);
			list = query.list();
			if (list.isEmpty() && mensaje)
				presentaMensaje(FacesMessage.SEVERITY_INFO, "NO SE ENCONTRO NINGUNA COINCIDENCIA");
			else
				for (T t : list)
					for (int i = 0; i < valoresInicializar.length; i++) {
						try {
							Hibernate.initialize(t.getClass().getMethod(valoresInicializar[i].toString()).invoke(t));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<T> obtenerListaDirecta(String consulta, Object[] valoresConsulta, Integer registrosMax, boolean mensaje,
			Object[] valoresInicializar) {
		List<T> list = null;
		Query query = (Query) session().createQuery(consulta);
		if (valoresConsulta != null)
			for (int i = 0; i < valoresConsulta.length; i++)
				if (valoresConsulta[i] != null) {
					query.setParameter(String.valueOf(i + 1), valoresConsulta[i]);
				}
		if (registrosMax > 0)
			query.setMaxResults(registrosMax);
		list = query.list();
		if (list.isEmpty() && mensaje)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "NO SE ENCONTRO NINGUNA COINCIDENCIA");
		else
			for (T t : list)
				for (int i = 0; i < valoresInicializar.length; i++) {
					try {
						Hibernate.initialize(t.getClass().getMethod(valoresInicializar[i].toString()).invoke(t));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		return list;
	}

	@SuppressWarnings("unchecked")
	public T obtenerObjeto(String consulta, Object[] valoresConsulta, boolean mensaje, Object[] valoresInicializar) {
		boolean validacion = false;
		List<T> list = null;
		Query query = (Query) session().createQuery(consulta);
		if (valoresConsulta != null)
			for (int i = 0; i < valoresConsulta.length; i++)
				if (valoresConsulta[i] != null) {
					if (valoresConsulta[i] instanceof String) {
						if ((String.valueOf(valoresConsulta[i]).startsWith("%")
								&& String.valueOf(valoresConsulta[i]).endsWith("%")
								&& String.valueOf(valoresConsulta[i]).length() >= 1)
								|| (!String.valueOf(valoresConsulta[i]).startsWith("%")
										&& !String.valueOf(valoresConsulta[i]).endsWith("%")
										&& String.valueOf(valoresConsulta[i]).length() >= 1)) {
							valoresConsulta[i] = String.valueOf(valoresConsulta[i]);
						} else {
							validacion = true;
							break;
						}
					}
					query.setParameter(String.valueOf(i + 1), valoresConsulta[i]);
				}
		if (validacion)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "PARA CUALQUIER BÚSQUEDA DEBE INGRESAR MAS DE 2 CARACTERES");
		else {
			list = query.list();
			if (list.isEmpty() && mensaje)
				presentaMensaje(FacesMessage.SEVERITY_INFO, "NO SE ENCONTRO NINGUNA COINCIDENCIA");
			else if (!list.isEmpty()) {
				for (int i = 0; i < valoresInicializar.length; i++) {
					try {
						Hibernate.initialize(
								list.get(0).getClass().getMethod(valoresInicializar[i].toString()).invoke(list.get(0)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return list.get(0);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<T> obtenerPorSql(String consulta, Class<T> type) {
		Query query = (Query) session().createSQLQuery(consulta).addEntity(type.getName());
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> obtenerPorSql_Actualizar(String consulta, Class<T> type) {
		Query query = (Query) session().createSQLQuery(consulta).addEntity(type.getName());
		return query.list();
	}

	public Session session() {
		return sessionFactory.getCurrentSession();
	}
}