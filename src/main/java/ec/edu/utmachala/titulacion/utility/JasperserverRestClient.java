package ec.edu.utmachala.titulacion.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

public final class JasperserverRestClient {

	private static final Logger LOGGER;
	private static JasperserverRestClient instance;
	static {
		LOGGER = LoggerFactory.getLogger((Class) JasperserverRestClient.class);
	}
	public static JasperserverRestClient getInstance(final String serverUrl, final String user, final String pwd) {
		if (JasperserverRestClient.instance == null) {
			JasperserverRestClient.instance = new JasperserverRestClient(serverUrl, user, pwd);
		}
		return JasperserverRestClient.instance;
	}
	private ClientConfig clientConfig;
	private String user;
	private String pwd;

	private Map<String, String> resourceCache;

	private String restEndpointUrl;

	private JasperserverRestClient(final String serverUrl, final String user, final String pwd) {
		this.user = user;
		this.pwd = pwd;
		this.restEndpointUrl = serverUrl + (serverUrl.endsWith("/") ? "rest" : "/rest");
		this.clientConfig = (ClientConfig) new DefaultApacheHttpClientConfig();
		this.clientConfig.getProperties().put("com.sun.jersey.client.property.followRedirects", true);
		this.clientConfig.getProperties().put("com.sun.jersey.impl.client.httpclient.handleCookies", true);
		this.resourceCache = new HashMap<String, String>();
	}

	private Document addParametersToResource(final Document resource, final Report reporte) {
		final Element root = resource.getRootElement();
		final Map<String, String> params = (Map<String, String>) reporte.getParams();
		for (final Map.Entry<String, String> entry : params.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			if (key != null && value != null) {
				root.addElement("parameter").addAttribute("name", key).addText(value);
			}
		}
		return resource;
	}

	private String getExtension(final String format) {
		String ext = null;
		if (format.equals("PDF")) {
			ext = "pdf";
		} else if (format.equals("XLS")) {
			ext = "xls";
		}
		return ext;
	}

	public File getReportAsFile(Report report) throws Exception {
		JasperserverRestClient.LOGGER.debug("getReportAsFile");
		final ApacheHttpClient client = ApacheHttpClient.create(this.clientConfig);
		client.addFilter((ClientFilter) new HTTPBasicAuthFilter(this.user, this.pwd));
		final String describeResourcePath = "/resource" + report.getPath() + report.getName();
		final String generateReportPath = "/report" + report.getPath() + report.getName() + "?RUN_OUTPUT_FORMAT="
				+ report.getFormat();
		JasperserverRestClient.LOGGER.debug("Obtener Recurso...");
		WebResource resource = null;
		String resourceResponse = null;
		if (this.resourceCache.containsKey(describeResourcePath)) {
			resourceResponse = this.resourceCache.get(describeResourcePath);
		} else {
			resource = client.resource(this.restEndpointUrl);
			resource.accept(new String[] { "application/xml" });
			resourceResponse = (String) resource.path(describeResourcePath).get((Class) String.class);
			this.resourceCache.put(describeResourcePath, resourceResponse);
		}
		Document resourceXML = this.parseResource(resourceResponse);
		JasperserverRestClient.LOGGER.debug("Generar Reporte...");
		resourceXML = this.addParametersToResource(resourceXML, report);
		resource = client.resource(this.restEndpointUrl + generateReportPath);
		resource.accept(new String[] { "text/xml" });
		final String reportResponse = (String) resource.put((Class) String.class,
				(Object) this.serializetoXML(resourceXML));
		JasperserverRestClient.LOGGER.debug("Obtener archivo...");
		final String urlReport = this.parseReport(reportResponse);
		resource = client.resource(urlReport);
		File destFile = null;
		try {
			JasperserverRestClient.LOGGER.debug("Inicia escritura de archivo...");
			final File remoteFile = (File) resource.get((Class) File.class);
			destFile = File.createTempFile("report_", "." + this.getExtension(report.getFormat()));
			JasperserverRestClient.LOGGER.debug("destFile:" + destFile.getAbsolutePath());
			FileUtils.copyFile(remoteFile, destFile);
			JasperserverRestClient.LOGGER.debug("Finaliza escritura de archivo...");
		} catch (IOException e) {
			throw e;
		}
		return destFile;
	}

	private String parseReport(final String reportResponse) throws Exception {
		JasperserverRestClient.LOGGER.debug("reportResponse:\n" + reportResponse);
		String urlReport = null;
		try {
			final Document document = DocumentHelper.parseText(reportResponse);
			Node node = document.selectSingleNode("/report/uuid");
			final String uuid = node.getText();
			node = document.selectSingleNode("/report/totalPages");
			final Integer totalPages = Integer.parseInt(node.getText());
			if (totalPages == 0) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No existen datos para generar el reporte");
			}
			urlReport = this.restEndpointUrl + "/report/" + uuid + "?file=report";
		} catch (DocumentException e) {
			throw e;
		}
		return urlReport;
	}

	private Document parseResource(final String resourceAsText) throws Exception {
		Document document;
		try {
			document = DocumentHelper.parseText(resourceAsText);
		} catch (DocumentException e) {
			throw e;
		}
		return document;
	}

	private String serializetoXML(final Document resource) throws Exception {
		final OutputFormat outformat = OutputFormat.createCompactFormat();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		outformat.setEncoding("ISO-8859-1");
		try {
			final XMLWriter writer = new XMLWriter((OutputStream) out, outformat);
			writer.write(resource);
			writer.flush();
		} catch (IOException e) {
			throw e;
		}
		return out.toString();
	}
}