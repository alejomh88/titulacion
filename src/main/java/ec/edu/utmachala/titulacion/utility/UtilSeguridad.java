package ec.edu.utmachala.titulacion.utility;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class UtilSeguridad {
	public static String generadorContrasena(final int longitud) {
		final String NUMEROS = "0123456789";
		final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
		final String ESPECIALES = "$#&_-@+-";
		final String key = NUMEROS + MAYUSCULAS + MINUSCULAS + ESPECIALES;
		String pswd = "";
		for (int i = 0; i < longitud; ++i) {
			pswd += key.charAt((int) (Math.random() * key.length()));
		}
		return pswd;
	}

	public static String generarClave(final String clave) {
		final ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder(256);
		return shaPasswordEncoder.encodePassword(clave, (Object) null);
	}

	public static boolean obtenerRol(final String rol) {
		for (final GrantedAuthority ga : SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			if (ga.getAuthority().compareToIgnoreCase(rol) == 0)
				return true;
		return false;
	}

	public static String obtenerUsuario() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}