package ec.edu.utmachala.titulacion.utility;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPTransport;

public class UtilsMail {
	public static void envioCorreo(final String destinatarios, final String asunto, final String detalle,
			final List<File> listAdjunto, final Map<String, String> parametro) throws Exception {
		final Properties properties = new Properties();
		properties.put("mail.smtp.host", parametro.get("mailSmtpHost"));
		properties.put("mail.smtp.port", parametro.get("mailSmtpPort"));
		properties.put("mail.smtp.auth", parametro.get("mailSmtpAuth"));
		properties.put("mail.smtp.ssl.trust", parametro.get("mailSmtpSslTrust"));
		properties.put("mail.smtp.starttls.enable", parametro.get("mailSmtpStartTlsEnable"));
		final Session session = Session.getInstance(properties, (Authenticator) null);
		final MimeMessage mensaje = new MimeMessage(session);
		mensaje.setFrom((Address) new InternetAddress((String) parametro.get("emailEmisor")));
		try {
			mensaje.addRecipient(Message.RecipientType.TO, (Address) new InternetAddress(destinatarios));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mensaje.setSubject(asunto);
		mensaje.setSentDate(new Date());
		BodyPart messageBodyPart = (BodyPart) new MimeBodyPart();
		messageBodyPart.setContent((Object) detalle, "text/html");
		final Multipart multipart = (Multipart) new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		if (listAdjunto != null && !listAdjunto.isEmpty()) {
			for (final File adjunto : listAdjunto) {
				messageBodyPart = (BodyPart) new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler((DataSource) new FileDataSource(adjunto)));
				messageBodyPart.setFileName(adjunto.getName());
				multipart.addBodyPart(messageBodyPart);
			}
		}
		mensaje.setContent(multipart);
		System.clearProperty("javax.net.ssl.keyStore");
		System.clearProperty("javax.net.ssl.keyStorePassword");
		System.clearProperty("javax.net.ssl.trustStore");
		System.clearProperty("javax.net.ssl.trustStorePassword");
		final SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
		try {
			transport.connect((String) parametro.get("emailEmisor"), (String) parametro.get("passEmail"));
			transport.sendMessage((Message) mensaje, mensaje.getAllRecipients());
		} finally {
			transport.close();
		}
	}

	public static void envioCorreo(final String[] destinatarios, final String asunto, final String detalle,
			final List<File> listAdjunto, final Map<String, String> parametro) throws Exception {
		final Properties properties = new Properties();
		properties.put("mail.smtp.host", parametro.get("mailSmtpHost"));
		properties.put("mail.smtp.port", parametro.get("mailSmtpPort"));
		properties.put("mail.smtp.auth", parametro.get("mailSmtpAuth"));
		properties.put("mail.smtp.ssl.trust", parametro.get("mailSmtpSslTrust"));
		properties.put("mail.smtp.starttls.enable", parametro.get("mailSmtpStartTlsEnable"));
		final Session session = Session.getInstance(properties, (Authenticator) null);
		final MimeMessage mensaje = new MimeMessage(session);
		mensaje.setFrom((Address) new InternetAddress((String) parametro.get("emailEmisor")));
		final Address[] destinos = new Address[destinatarios.length];
		for (int i = 0; i < destinos.length; ++i) {
			destinos[i] = (Address) new InternetAddress(destinatarios[i]);
		}
		try {
			mensaje.addRecipients(Message.RecipientType.TO, destinos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mensaje.setSubject(asunto);
		mensaje.setSentDate(new Date());
		BodyPart messageBodyPart = (BodyPart) new MimeBodyPart();
		messageBodyPart.setContent((Object) detalle, "text/html");
		final Multipart multipart = (Multipart) new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		if (listAdjunto != null && !listAdjunto.isEmpty()) {
			for (final File adjunto : listAdjunto) {
				messageBodyPart = (BodyPart) new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler((DataSource) new FileDataSource(adjunto)));
				messageBodyPart.setFileName(adjunto.getName());
				multipart.addBodyPart(messageBodyPart);
			}
		}
		mensaje.setContent(multipart);
		System.clearProperty("javax.net.ssl.keyStore");
		System.clearProperty("javax.net.ssl.keyStorePassword");
		System.clearProperty("javax.net.ssl.trustStore");
		System.clearProperty("javax.net.ssl.trustStorePassword");
		final SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
		try {
			transport.connect((String) parametro.get("emailEmisor"), (String) parametro.get("passEmail"));
			transport.sendMessage((Message) mensaje, mensaje.getAllRecipients());
		} finally {
			transport.close();
		}
	}

	public static void envioCorreoArchivoUMMOG(final List<String> destinatarios, final String asunto,
			final String detalle, final List<File> listAdjunto, final Map<String, String> parametro) throws Exception {
		final Properties properties = new Properties();
		properties.put("mail.smtp.host", parametro.get("mailSmtpHost"));
		properties.put("mail.smtp.port", parametro.get("mailSmtpPort"));
		properties.put("mail.smtp.auth", parametro.get("mailSmtpAuth"));
		properties.put("mail.smtp.ssl.trust", parametro.get("mailSmtpSslTrust"));
		properties.put("mail.smtp.starttls.enable", parametro.get("mailSmtpStartTlsEnable"));
		final Session session = Session.getInstance(properties, (Authenticator) null);
		final MimeMessage mensaje = new MimeMessage(session);
		mensaje.setFrom((Address) new InternetAddress((String) parametro.get("emailEmisor")));
		final Address[] destinos = new Address[destinatarios.size()];
		for (int i = 0; i < destinos.length; ++i) {
			destinos[i] = (Address) new InternetAddress((String) destinatarios.get(i));
		}
		try {
			mensaje.addRecipients(Message.RecipientType.TO, destinos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mensaje.setSubject(asunto);
		mensaje.setSentDate(new Date());
		BodyPart messageBodyPart = (BodyPart) new MimeBodyPart();
		messageBodyPart.setContent((Object) detalle, "text/html");
		final Multipart multipart = (Multipart) new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		if (listAdjunto != null && !listAdjunto.isEmpty()) {
			for (final File adjunto : listAdjunto) {
				messageBodyPart = (BodyPart) new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler((DataSource) new FileDataSource(adjunto)));
				messageBodyPart.setFileName(adjunto.getName());
				multipart.addBodyPart(messageBodyPart);
			}
		}
		mensaje.setContent(multipart);
		System.clearProperty("javax.net.ssl.keyStore");
		System.clearProperty("javax.net.ssl.keyStorePassword");
		System.clearProperty("javax.net.ssl.trustStore");
		System.clearProperty("javax.net.ssl.trustStorePassword");
		final SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
		try {
			transport.connect((String) parametro.get("emailEmisor"), (String) parametro.get("passEmail"));
			transport.sendMessage((Message) mensaje, mensaje.getAllRecipients());
		} finally {
			transport.close();
		}
	}

	public static void envioCorreoEstudiantesComiteEvaluador(final String[] destinatarios, final String asunto,
			final String detalle, final List<File> listAdjunto, final Map<String, String> parametro) throws Exception {
		final Properties properties = new Properties();
		properties.put("mail.smtp.host", parametro.get("mailSmtpHost"));
		properties.put("mail.smtp.port", parametro.get("mailSmtpPort"));
		properties.put("mail.smtp.auth", parametro.get("mailSmtpAuth"));
		properties.put("mail.smtp.ssl.trust", parametro.get("mailSmtpSslTrust"));
		properties.put("mail.smtp.starttls.enable", parametro.get("mailSmtpStartTlsEnable"));
		final Session session = Session.getInstance(properties, (Authenticator) null);
		final MimeMessage mensaje = new MimeMessage(session);
		mensaje.setFrom((Address) new InternetAddress((String) parametro.get("emailEmisor")));
		final Address[] destinos = new Address[destinatarios.length];
		for (int i = 0; i < destinos.length; ++i) {
			destinos[i] = (Address) new InternetAddress(destinatarios[i]);
		}
		try {
			mensaje.addRecipients(Message.RecipientType.TO, destinos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mensaje.setSubject(asunto);
		mensaje.setSentDate(new Date());
		BodyPart messageBodyPart = (BodyPart) new MimeBodyPart();
		messageBodyPart.setContent((Object) detalle, "text/html");
		final Multipart multipart = (Multipart) new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		if (listAdjunto != null && !listAdjunto.isEmpty()) {
			for (final File adjunto : listAdjunto) {
				messageBodyPart = (BodyPart) new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler((DataSource) new FileDataSource(adjunto)));
				messageBodyPart.setFileName(adjunto.getName());
				multipart.addBodyPart(messageBodyPart);
			}
		}
		mensaje.setContent(multipart);
		System.clearProperty("javax.net.ssl.keyStore");
		System.clearProperty("javax.net.ssl.keyStorePassword");
		System.clearProperty("javax.net.ssl.trustStore");
		System.clearProperty("javax.net.ssl.trustStorePassword");
		final SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
		try {
			transport.connect((String) parametro.get("emailEmisor"), (String) parametro.get("passEmail"));
			transport.sendMessage((Message) mensaje, mensaje.getAllRecipients());
		} finally {
			transport.close();
		}
	}

	public static void envioCorreoZimbra(final String destinatarios, final String asunto, final String detalle,
			final List<File> listAdjunto, final String emailEmisor, final String passEmail) throws Exception {
		final Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		properties.put("mail.smtp.starttls.enable", true);
		final Session session = Session.getInstance(properties, (Authenticator) null);
		final Message mensaje = (Message) new MimeMessage(session);
		mensaje.setFrom((Address) new InternetAddress(emailEmisor));
		try {
			mensaje.addRecipient(Message.RecipientType.TO, (Address) new InternetAddress(destinatarios));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mensaje.setSubject(asunto);
		mensaje.setSentDate(new Date());
		BodyPart messageBodyPart = (BodyPart) new MimeBodyPart();
		messageBodyPart.setText(detalle);
		final Multipart multipart = (Multipart) new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		if (listAdjunto != null && !listAdjunto.isEmpty()) {
			for (final File adjunto : listAdjunto) {
				messageBodyPart = (BodyPart) new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler((DataSource) new FileDataSource(adjunto)));
				messageBodyPart.setFileName(adjunto.getName());
				multipart.addBodyPart(messageBodyPart);
			}
		}
		mensaje.setContent(multipart);
		System.clearProperty("javax.net.ssl.keyStore");
		System.clearProperty("javax.net.ssl.keyStorePassword");
		System.clearProperty("javax.net.ssl.trustStore");
		System.clearProperty("javax.net.ssl.trustStorePassword");
		final SMTPTransport transport = (SMTPTransport) session.getTransport("smtp");
		try {
			transport.connect(emailEmisor, passEmail);
			transport.sendMessage(mensaje, mensaje.getAllRecipients());
		} finally {
			transport.close();
		}
	}
}