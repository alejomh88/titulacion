package ec.edu.utmachala.titulacion.utility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Report implements Serializable {
	private static final long serialVersionUID = 6082809270329433203L;
	public static final String FORMAT_PDF = "PDF";
	public static final String FORMAT_EXCEL = "XLS";
	public static final String FORMAT_CSV = "CSV";
	private String name;
	private String path;
	private Map<String, String> params;
	private String format;
	private String outputFolder;

	public Report() {
		this.format = "PDF";
		this.params = new HashMap<String, String>();
	}

	public void addParameter(final String key, final String value) {
		this.params.put(key, value);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals((Object) this, obj);
	}

	public String getFormat() {
		return this.format;
	}

	public String getName() {
		return this.name;
	}

	public String getOutputFolder() {
		return this.outputFolder;
	}

	public Map<String, String> getParams() {
		return this.params;
	}

	public String getPath() {
		return this.path;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode((Object) this);
	}

	public void setFormat(final String format) {
		this.format = format;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOutputFolder(final String outputFolder) {
		this.outputFolder = outputFolder;
	}

	public void setParams(final Map<String, String> parametros) {
		this.params = parametros;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString((Object) this, ToStringStyle.MULTI_LINE_STYLE);
	}
}