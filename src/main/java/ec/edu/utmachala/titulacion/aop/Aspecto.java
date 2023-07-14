package ec.edu.utmachala.titulacion.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ec.edu.utmachala.titulacion.controller.LoginBean;

@Aspect
@Component
public class Aspecto {

	// @Around("execution(* ec.edu.utmachala.titulacion.service.init())")
	public void tiempoPasado(ProceedingJoinPoint punto) throws Throwable {
		Logger LOGGER = LoggerFactory.getLogger(LoginBean.class);
		LOGGER.info("Ejecutado desde el método AOP");
	}
}