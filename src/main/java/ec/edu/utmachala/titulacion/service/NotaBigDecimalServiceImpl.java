package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.NotaBigDecimal;

@Service
public class NotaBigDecimalServiceImpl extends GenericServiceImpl<NotaBigDecimal, BigDecimal>
		implements NotaBigDecimalService, Serializable {

	private static final long serialVersionUID = 1L;

}