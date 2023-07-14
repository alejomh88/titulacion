package ec.edu.utmachala.titulacion.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import ec.edu.utmachala.titulacion.utility.Conexion;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:database.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private Environment env;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
				.antMatchers("/javax.faces.resource/**", "/resources/**", "/views/publicas/**").permitAll()

				.antMatchers("/views/privadas/home.jsf").access("isAuthenticated()")
				.antMatchers("/views/privadas/actualizarDatos.jsf").access("isAuthenticated()")
				.antMatchers("/views/privadas/cambiarContrasena.jsf").access("isAuthenticated()")

				.antMatchers("/views/privadas/administrarAsignatura.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA")
				.antMatchers("/views/privadas/administrarCasosInvestigacion.jsf").hasAnyAuthority("DOTU")
				.antMatchers("/views/privadas/administrarDoceAsignatura.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA").antMatchers("/views/privadas/administrarPregunta.jsf")
				.hasAnyAuthority("DORE").antMatchers("/views/privadas/administrarReactivosPracticos.jsf")
				.hasAnyAuthority("DORE").antMatchers("/views/privadas/administrarSeminario.jsf")
				.hasAnyAuthority("UMMO", "ANAL").antMatchers("/views/privadas/administrarTemasPracticos.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA")
				.antMatchers("/views/privadas/administrarTrabajoTitulacion.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA")
				.antMatchers("/views/privadas/administrarUnidadesDidacticas.jsf").hasAnyAuthority("DORE")
				.antMatchers("/views/privadas/antiplagioC.jsf").hasAnyAuthority("DORE")
				.antMatchers("/views/privadas/antiplagioTT.jsf").hasAnyAuthority("DORE")
				.antMatchers("/views/privadas/asignarEstudianteATrabajoTitulacion.jsf").hasAnyAuthority("DOTU")
				.antMatchers("/views/privadas/bibliotecaC.jsf").hasAnyAuthority("ESTU", "BIBL")
				.antMatchers("/views/privadas/biblioteca.jsf").hasAnyAuthority("ESTU", "BIBL")
				.antMatchers("/views/privadas/bibliotecaTT.jsf").hasAnyAuthority("ESTU", "BIBL")
				.antMatchers("/views/privadas/calificacion.jsf").hasAnyAuthority("ESTU")
				.antMatchers("/views/privadas/calificacionTemasPracticosjsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA")
				// .antMatchers("/views/privadas/calificacionTT.jsf").hasAnyAuthority("DOEV")
				.antMatchers("/views/privadas/calificacionTrabajoTitulacion.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "DOEV", "COCA")
				.antMatchers("/views/privadas/calificacionTrabajoTitulacionDOCE.jsf").hasAnyAuthority("DOCE")
				.antMatchers("/views/privadas/consultarDatosComplexivo.jsf").hasAnyAuthority("ESTU")
				.antMatchers("/views/privadas/consultarDatosTitulacion.jsf").hasAnyAuthority("ESTU")
				.antMatchers("/views/privadas/documentosEC.jsf").hasAnyAuthority("UMMO", "ANAL")
				.antMatchers("/views/privadas/documentosTT.jsf").hasAnyAuthority("UMMO", "ANAL")
				.antMatchers("/views/privadas/eliminarCalificacionTemasPracticos.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA").antMatchers("/views/privadas/principalEC.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA")
				.antMatchers("/views/privadas/eliminarCalificacionTrabajoTitulacion.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA").antMatchers("/views/privadas/escojerCarrera.jsf")
				.hasAnyAuthority("ESTU").antMatchers("/views/privadas/examen.jsf").hasAnyAuthority("ESTU")
				.antMatchers("/views/privadas/fechasyComitesTemasPracticos.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA")
				.antMatchers("/views/privadas/fechasyComitesTrabajoTitulacion.jsf")
				.hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA")
				.antMatchers("/views/privadas/incorporacionTemasPracticos.jsf").hasAnyAuthority("UMMO", "ANAL")
				.antMatchers("/views/privadas/incorporacionTrabajoTitulacion.jsf").hasAnyAuthority("UMMO", "ANAL")
				.antMatchers("/views/privadas/insertarTutorias.jsf").hasAnyAuthority("DOCE")
				.antMatchers("/views/privadas/listadoDocentes.jsf").hasAnyAuthority("UMMO", "ANAL", "COTI", "COCA")
				.antMatchers("/views/privadas/listadoEstudiantes.jsf").hasAnyAuthority("UMMO", "ANAL")
				.antMatchers("/views/privadas/menuEstu.jsf").hasAnyAuthority("ESTU")
				.antMatchers("/views/privadas/obtenerCasoPractico.jsf").hasAnyAuthority("ESEC")
				.antMatchers("/views/privadas/reporteADM.jsf").hasAnyAuthority("ADMI")
				.antMatchers("/views/privadas/reporteAnal.jsf").hasAnyAuthority("ANAL")
				.antMatchers("/views/privadas/reporteCoti.jsf").hasAnyAuthority("COTI")
				.antMatchers("/views/privadas/reporteEstudianteComplexivo.jsf").hasAnyAuthority("ESTU")
				.antMatchers("/views/privadas/reporteEstudianteTrabajoTitulacion.jsf").hasAnyAuthority("ESTU")
				.antMatchers("/views/privadas/reporteUmog.jsf").hasAnyAuthority("UMMO")
				.antMatchers("/views/privadas/resolucionAptitudLegalTemasPracticos.jsf").hasAnyAuthority("UMMO")
				.antMatchers("/views/privadas/resolucionAptitudLegalTrabajoTitulacion.jsf").hasAnyAuthority("UMMO")
				.antMatchers("/views/privadas/resolucionTemasPracticos.jsf").hasAnyAuthority("UMMO")
				.antMatchers("/views/privadas/resolucionTrabajoTitulacion.jsf").hasAnyAuthority("UMMO")
				.antMatchers("/views/privadas/seguimientoTutorias.jsf").hasAnyAuthority("DOTU")
				.antMatchers("/views/privadas/seguimientoTutorias1.jsf").hasAnyAuthority("DOTU")
				.antMatchers("/views/privadas/tituloReactivoPractico.jsf").hasAnyAuthority("ESTU")
				.antMatchers("/views/privadas/tituloTrabajoTitulacion.jsf").hasAnyAuthority("ESTU")
				.antMatchers("/views/privadas/validarCalificacionTemasPracticos.jsf").hasAnyAuthority("UMMO", "ANAL")
				.antMatchers("/views/privadas/validarCalificacionTrabajoTitulacion.jsf").hasAnyAuthority("UMMO", "ANAL")
				.antMatchers("/views/privadas/antiplagioEC.jsf").hasAnyAuthority("DOEV")
				.antMatchers("/views/privadas/citaPapersEC.jsf").hasAnyAuthority("DOEV")

				.antMatchers("/views/privadas/antiplagioTT.jsf").hasAnyAuthority("DOEV")
				.antMatchers("/views/privadas/citaPapersTT.jsf").hasAnyAuthority("DOEV")

				.and().formLogin().loginPage("/views/publicas/login.jsf").defaultSuccessUrl("/views/privadas/home.jsf")

				.and().logout().logoutUrl("/views/publicas/logout.jsf").logoutSuccessUrl("/views/publicas/login.jsf")
				.invalidateHttpSession(true).deleteCookies("JSESSIONID")

				.and().exceptionHandling().accessDeniedPage("/views/publicas/access-denied.jsf")

				.and().sessionManagement().invalidSessionUrl("/views/publicas/login.jsf").maximumSessions(1);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(this.dataSource()).passwordEncoder(new ShaPasswordEncoder(256))
				.usersByUsernameQuery(getUserQuery()).authoritiesByUsernameQuery(getAuthoritiesQuery());
	}

	@Bean
	public DataSource dataSource() {
		Conexion.iniciarConeccion("postgresql", "org.postgresql.Driver", env.getProperty("jdbc.server"),
				env.getProperty("jdbc.port"), env.getProperty("jdbc.database"), env.getProperty("jdbc.user"),
				env.getProperty("jdbc.password"));

		Conexion conexion = Conexion.getConexion();

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(conexion.getDriver());
		dataSource.setUrl("jdbc:" + conexion.getDb() + "://" + conexion.getServer() + ":" + conexion.getPort() + "/"
				+ conexion.getDatabase());
		dataSource.setUsername(conexion.getUser());
		dataSource.setPassword(conexion.getPassword());

		return dataSource;
	}

	private String getAuthoritiesQuery() {
		return "select distinct u.email, r.id from exetasi.usuarios u "
				+ "inner join exetasi.permisos p on (p.usuario=u.id) "
				+ "inner join exetasi.roles r on (r.id=p.rol) where u.email=LOWER(?)";
	}

	private String getUserQuery() {
		return "select u.email, u.password, u.activo from exetasi.usuarios u where u.email=LOWER(?)";
	}
}
