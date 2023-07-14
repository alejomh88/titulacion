package ec.edu.utmachala.titulacion.seguridad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;

@Component
@Aspect
public class BitacoraAspect implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	public LogService logService;

	@Autowired
	public UsuarioService usuarioService;

	@Autowired
	public AopService aopService;

	@Autowired
	public EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Around("execution (public * ec.edu.utmachala.titulacion.service..*.actualizar(..))")
	public void auditarActualizar(ProceedingJoinPoint joinPoint) {

		String sentencia = "", entidadStr = "";
		try {
			Object entidad = joinPoint.getArgs()[0];
			entidadStr = entidad.getClass().getSimpleName();

			Field[] campos = entidad.getClass().getDeclaredFields();
			Method[] metodos = new Method[campos.length - 1];

			System.out.println("Los nombres de los campos encontrados son: ");

			for (int i = 0; i < campos.length - 1; i++)
				metodos[i] = entidad.getClass().getMethod("get" + capitalize(campos[i + 1].getName()), new Class[] {});

			for (Method m : metodos) {
				try {
					if (!String.valueOf(m.getName().charAt(m.getName().length() - 1)).contains("s")) {
						if (m.invoke(entidad, new Object[] {}).toString().contains("@")
								&& !(m.invoke(entidad, new Object[] {}).toString().split("@")[1].contains("utmachala")
										|| m.invoke(entidad, new Object[] {}).toString().split("@")[1].contains("gmail")
										|| m.invoke(entidad, new Object[] {}).toString().split("@")[1]
												.contains("hotmail")
										|| m.invoke(entidad, new Object[] {}).toString().split("@")[1]
												.contains("outlook"))) {
							System.err.println("Del método " + m.getName() + " el valor es: "
									+ m.invoke(entidad, new Object[] {}));
							sentencia += m.getName().split("get")[1] + " = "
									+ m.invoke(entidad, new Object[] {}).getClass().getMethod("getId", new Class[] {})
											.invoke(m.invoke(entidad, new Object[] {}), new Object[] {})
									+ " | ";
						} else {
							System.err.println("Del método " + m.getName() + " el valor es: "
									+ m.invoke(entidad, new Object[] {}));
							sentencia += m.getName().split("get")[1] + " = " + m.invoke(entidad, new Object[] {})
									+ " | ";
						}
					}
				} catch (NullPointerException npe) {
					sentencia += m.getName() + " = null |";
					continue;
				}
			}

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			joinPoint.proceed();
			insertarAOP(entidadStr, "Actualizar = ".concat(sentencia));
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Around("execution (public * ec.edu.utmachala.titulacion.service..*.actualizarSQL(..))")
	public void auditarActualizarSQL(ProceedingJoinPoint joinPoint) {
		String entidad = "", sentenciaSQL = "";

		try {
			Object sentenciaSQLClaseString = joinPoint.getArgs()[0];
			sentenciaSQL = sentenciaSQLClaseString.getClass().getMethod("toString", new Class[] {})
					.invoke(sentenciaSQLClaseString, new Object[] {}).toString();
			String idObjeto = sentenciaSQL.split("=")[sentenciaSQL.split("=").length - 1].replace(";", "");
			String tablaBD = (sentenciaSQL.split(" ")[1].contains("\"") ? sentenciaSQL.split(" ")[1].replace("\"", "")
					: sentenciaSQL.split(" ")[1]);
			System.out.println("Id del objeto tomado correctamente: " + idObjeto + " y nombre de tabla: " + tablaBD);

			File f = new File(UtilsArchivos.getRutaRaiz() + "/tablasBaseDatos.csv");

			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (null != line) {
				String[] fields = line.split(";");
				if (tablaBD.compareTo(fields[0]) == 0) {
					entidad = fields[1];
					break;
				}
				line = br.readLine();
			}
			System.out.println("La entidad encontrada es: " + entidad);

			if (sentenciaSQL.contains("WHERE")) {
				String campos = sentenciaSQL.split("WHERE")[0].split("SET")[1].replace("\"", "").replace("'", "");

				System.out.println("Campos con sus valores: " + campos);

				String[][] camposValores = (campos.contains(",") ? new String[campos.split(",").length][2]
						: new String[1][2]);

				System.out.println("Después nombre de la entidad que viene: "
						+ sentenciaSQLClaseString.getClass().getSimpleName());
			}

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			joinPoint.proceed();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		insertarAOP(entidad, sentenciaSQL);
	}

	@After("execution (public * ec.edu.utmachala.titulacion.service..*.insertar(..))")
	public void auditarInsertar(JoinPoint llamada) {
		String sentencia = "";
		try {
			Object entidad = llamada.getArgs()[0];
			Field[] campos2 = entidad.getClass().getDeclaredFields();
			Method[] metodosClase = new Method[campos2.length];

			for (int j = 0; j < campos2.length - 1; j++) {
				System.err.println("Con getDeclaredFields: " + campos2[j + 1].getName());
				System.err.println("Con getDeclaredFields con método para obtener el valor: get"
						+ capitalize(campos2[j + 1].getName()) + "\n");
				metodosClase[j] = entidad.getClass().getMethod("get" + capitalize(campos2[j + 1].getName()),
						new Class[] {});
			}

			for (Method m : metodosClase) {
				try {
					if (!String.valueOf(m.getName().charAt(m.getName().length() - 1)).contains("s")) {

						if (m.invoke(entidad, new Object[] {}).toString().contains("@")
								&& !(m.invoke(entidad, new Object[] {}).toString().split("@")[1].contains("utmachala")
										|| m.invoke(entidad, new Object[] {}).toString().split("@")[1].contains("gmail")
										|| m.invoke(entidad, new Object[] {}).toString().split("@")[1]
												.contains("hotmail")
										|| m.invoke(entidad, new Object[] {}).toString().split("@")[1]
												.contains("outlook"))) {
							System.err.println("Del método " + m.getName() + " el valor es: "
									+ m.invoke(entidad, new Object[] {}));
							sentencia += m.getName().split("get")[1] + " = "
									+ m.invoke(entidad, new Object[] {}).getClass().getMethod("getId", new Class[] {})
											.invoke(m.invoke(entidad, new Object[] {}), new Object[] {})
									+ " | ";
						} else {
							System.err.println("Del método " + m.getName() + " el valor es: "
									+ m.invoke(entidad, new Object[] {}));
							sentencia += m.getName().split("get")[1] + " = " + m.invoke(entidad, new Object[] {})
									+ " | ";
						}
					}
				} catch (Exception e) {
					sentencia += m.getName().split("get")[1] + " = null | ";
					continue;
				}
			}
			System.out.println("Entro: Sentencia a guardar en acción: " + sentencia);
			insertarAOP(llamada.getArgs()[0].getClass().getSimpleName(), "Insertar = ".concat(sentencia));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Finally: Sentencia a guardar en acción: " + sentencia);
			insertarAOP(llamada.getArgs()[0].getClass().getSimpleName(), "Insertar = ".concat(sentencia));
		}
	}

	private String capitalize(String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}

	@After("execution(public * ec.edu.utmachala.titulacion.service.UsuarioService.obtenerParaLogin(..)) ")
	public void ingresoExetasi(JoinPoint joinPoint) {
		insertarAOP("", "Ingresó al sistema.");

	}

	private void insertarAOP(String tabla, String accion) {
		String ipCliente1 = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
				.getRemoteAddr();
		String ipNavegador = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
				.getHeader("User-Agent");
		String cedula = SecurityContextHolder.getContext().getAuthentication().getName();

		Aop aop = new Aop();
		aop.setFecha(new Timestamp((new Date()).getTime()));
		aop.setUsuario(usuarioService.obtenerPorEmail(cedula));
		aop.setAccion(accion);
		aop.setTabla(tabla);
		aop.setIp(ipCliente1);
		aop.setNavegador(ipNavegador);

		// Map<String, String> map = new HashMap<String, String>();
		//
		// Enumeration headerNames = ((HttpServletRequest)
		// FacesContext.getCurrentInstance().getExternalContext()
		// .getRequest()).getHeaderNames();
		// while (headerNames.hasMoreElements()) {
		// String key = (String) headerNames.nextElement();
		// String value = ((HttpServletRequest)
		// FacesContext.getCurrentInstance().getExternalContext().getRequest())
		// .getHeader(key);
		// map.put(key, value);
		// }
		//
		// for (Entry<String, String> e : map.entrySet()) {
		// System.out.println("COn el header: [" + e.getKey() + "=" +
		// e.getValue() + "]");
		// }

		aopService.insertar(aop);
	}

	@Before("execution(public * ec.edu.utmachala.titulacion.service.UsuarioService.logout(..)) ")
	public void salidaExetasi(JoinPoint joinPoint) {
		System.out.println("logout");
		insertarAOP("", "Salió del sistema.");
	}
}
