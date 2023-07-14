package ec.edu.utmachala.titulacion.seguridad;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ec.edu.utmachala.titulacion.entity.Usuario;

@Entity
@Table(schema = "registros", name = "aops")
public class Aop implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(allocationSize = 1, name = "REGISTROS.AOPS_ID_GENERATOR", sequenceName = "REGISTROS.AOPS_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REGISTROS.AOPS_ID_GENERATOR")
	@Column(unique = true, nullable = false)
	private Integer id;

	@Column(nullable = false, length = 2147483647)
	private String accion;

	@Column(nullable = false)
	private Timestamp fecha;

	@ManyToOne
	@JoinColumn(name = "usuario", nullable = false)
	private Usuario usuario;

	@Column(length = 100)
	private String tabla;

	@Column(length = 100)
	private String ip;

	@Column(length = 200)
	private String navegador;

	public Aop() {
	}

	public Aop(Timestamp fecha, String accion, Usuario usuario) {
		this.fecha = fecha;
		this.accion = accion;
		this.usuario = usuario;
	}

	public String getAccion() {
		return accion;
	}

	public Timestamp getFecha() {
		return fecha;
	}

	public Integer getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public String getNavegador() {
		return navegador;
	}

	public String getTabla() {
		return tabla;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setNavegador(String navegador) {
		this.navegador = navegador;
	}

	public void setTabla(String tabla) {
		this.tabla = tabla;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}