package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.NotaBigDecimal;

@Repository
public class NotaBigDecimalDaoImpl extends GenericDaoImpl<NotaBigDecimal, BigDecimal>
		implements NotaBigDecimalDao, Serializable {
	private static final long serialVersionUID = 1L;
}