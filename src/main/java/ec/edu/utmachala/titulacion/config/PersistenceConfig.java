package ec.edu.utmachala.titulacion.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ec.edu.utmachala.titulacion.utility.Conexion;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:database.properties")
public class PersistenceConfig {

	@Autowired
	private Environment env;

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

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@SuppressWarnings("serial")
	Properties hibernateProperties() {
		return new Properties() {
			{
				setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
				setProperty("hibernate.show_sql", "true");
			}
		};
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(new String[] { "ec.edu.utmachala.titulacion" });
		sessionFactory.setHibernateProperties(hibernateProperties());

		return sessionFactory;
	}

	@Bean
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory().getObject());

		return txManager;
	}
}