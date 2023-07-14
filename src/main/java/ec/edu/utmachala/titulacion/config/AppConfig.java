package ec.edu.utmachala.titulacion.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("ec.edu.utmachala")
@Import({ PersistenceConfig.class, SecurityConfig.class, ScheduledTaskConfig.class, AsyncConfig.class })
public class AppConfig {

}
