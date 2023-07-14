package ec.edu.utmachala.titulacion.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema = "exetasi", name = "parametros")
public class Parametro {

	@Id
	@SequenceGenerator(allocationSize = 1, name = "PARAMETROS_ID_GENERATOR", sequenceName = "PARAMETROS_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARAMETROS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(name = "\"fechaInicio\"", nullable = false)
	private Timestamp fechaInicio;

	@Column(name = "\"fechaCierre\"", nullable = false)
	private Timestamp fechaCierre;

	@Column(name = "\"numeroPreguntaBasicaDificil\"", nullable = false)
	private Integer numeroPreguntaBasicaDificil;

	@Column(name = "\"numeroPreguntaBasicaMedio\"", nullable = false)
	private Integer numeroPreguntaBasicaMedio;

	@Column(name = "\"numeroPreguntaBasicaSencillo\"", nullable = false)
	private Integer numeroPreguntaBasicaSencillo;

	@Column(name = "\"numeroPreguntaHumanoTitulacionDificil\"", nullable = false)
	private Integer numeroPreguntaHumanoTitulacionDificil;

	@Column(name = "\"numeroPreguntaHumanoTitulacionMedio\"", nullable = false)
	private Integer numeroPreguntaHumanoTitulacionMedio;

	@Column(name = "\"numeroPreguntaHumanoTitulacionSencillo\"", nullable = false)
	private Integer numeroPreguntaHumanoTitulacionSencillo;

	@Column(name = "\"numeroPreguntaProfesionalDificil\"", nullable = false)
	private Integer numeroPreguntaProfesionalDificil;

	@Column(name = "\"numeroPreguntaProfesionalMedio\"", nullable = false)
	private Integer numeroPreguntaProfesionalMedio;

	@Column(name = "\"numeroPreguntaProfesionalSencillo\"", nullable = false)
	private Integer numeroPreguntaProfesionalSencillo;

	@Column(name = "\"mailSmtpHost\"", length = 25, nullable = false)
	private String mailSmtpHost;

	@Column(name = "\"mailSmtpPort\"", length = 25, nullable = false)
	private String mailSmtpPort;

	@Column(name = "\"mailSmtpAuth\"", length = 25, nullable = false)
	private Boolean mailSmtpAuth;

	@Column(name = "\"mailSmtpSslTrust\"", nullable = false)
	private String mailSmtpSslTrust;

	@Column(name = "\"mailSmtpStartTlsEnable\"", nullable = false)
	private Boolean mailSmtpStartTlsEnable;

	@Column(name = "\"emailEmisor\"", length = 25, nullable = false)
	private String emailEmisor;

	@Column(name = "\"passEmail\"", length = 25, nullable = false)
	private String passEmail;

	@Column(name = "\"pieEmail\"")
	private String pieEmail;

}