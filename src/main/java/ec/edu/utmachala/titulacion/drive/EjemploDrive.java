package ec.edu.utmachala.titulacion.drive;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.File.Capabilities;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;

import ec.edu.utmachala.titulacion.utility.UtilsArchivos;

public class EjemploDrive {

	/** Application name. */
	private static final String APPLICATION_NAME = "Drive API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("os.name").compareToIgnoreCase("linux") == 0 ? "/opt" : System.getProperty("user.home"),
			".credentials/drive-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/drive-java-quickstart
	 */
	private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in = EjemploDrive.class.getResourceAsStream("/client_secret_titulacion.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	public static void comprobarPermisos(Drive service, String id, String emailEspecialistas) throws IOException {
		PermissionList permisosEliminar = service.permissions().list(id).setFields("permissions").execute();
		System.out.println("Número de permisos encontrados: " + permisosEliminar.getPermissions().size());
		String[] especialistas = emailEspecialistas.split(";");
		int contador = 0;
		for (Permission p : permisosEliminar.getPermissions()) {
			if (p.getRole().compareTo("commenter") == 0) {
				for (String e : especialistas)
					if (p.getEmailAddress().compareTo(e) == 0)
						contador++;

				if (contador != 1) {
					String idPermiso = p.getId();
					service.permissions().delete(id, idPermiso).execute();
				}
			}
			contador = 0;
		}
		PermissionList permisosCrear = service.permissions().list(id).setFields("permissions").execute();
		int contador2 = 0;
		for (String e : especialistas) {
			for (Permission p : permisosCrear.getPermissions()) {
				if (p.getRole().compareTo("commenter") == 0)
					if (e.compareTo(p.getEmailAddress()) == 0)
						contador2++;
			}
			if (contador2 == 0) {
				Permission newPermission = new Permission();
				newPermission.setType("user");
				newPermission.setRole("commenter");
				newPermission.setEmailAddress(e);
				service.permissions().create(id, newPermission)
						.setEmailMessage("Se ha creado un documento en google drive"
								+ " en donde usted podrá realizar comentarios acerca del avance del trabajo escrito, tanto de examen complexivo"
								+ " como trabajo de titulación de los estudiantes a los cuales usted tendrá que evaluar, esto se debe a que usted"
								+ " consta como especialista del comité evaluador.")
						.execute();
			}
			contador2 = 0;
		}
	}

	public static String creacionDocumento(String id, String tituloDocumento, String idProceso, String modalidad,
			String emailEstudiante, String emailEspecialistas) throws IOException {
		Drive service = getDriveService();
		String idDocumento = "no";

		if (verificarDocumento(service, id)) {
			System.out.println("El documento ya existe");

			PermissionList permisos = service.permissions().list(id).setFields("permissions").execute();
			System.out.println("Número de permisos encontrados: " + permisos.getPermissions().size());

			for (Permission p : permisos.getPermissions()) {
				System.out.println("id del permiso: " + p.getId());
				System.out.println("Tipo del permiso: " + p.getType());
				System.out.println("Rol del servicio: " + p.getRole());
				System.out
						.println("Emails: " + (p.getEmailAddress() == null ? "No existe correo" : p.getEmailAddress()));
				System.out.println("Domain: " + (p.getDomain() == null ? "No existe dominio" : p.getDomain()));
				System.out.println(
						"---------------------------------------------------------------------------------------");
			}
			comprobarPermisos(service, id, emailEspecialistas);
		} else {
			idDocumento = crearDocumento(service, tituloDocumento, idProceso, modalidad);
			System.out.println("El id del documento creado es: " + idDocumento);
			otorgarPermisos(service, idDocumento, modalidad, emailEstudiante, emailEspecialistas);
		}

		return idDocumento;
	}

	public static String crearDocumento(Drive service, String tituloDocumento, String idProceso, String modalidad)
			throws IOException {
		System.out.println("No existe el archivo, se procede a crearlo.");
		String pageToken = null;

		FileList carpeta = service.files().list()
				.setQ("mimeType='application/vnd.google-apps.folder' and name = '" + modalidad + "_" + idProceso + "'")
				.setFields("nextPageToken, files(id, name)").setPageToken(pageToken).execute();
		String idCarpeta = "";
		for (File f : carpeta.getFiles()) {
			System.out.printf("Carpeta encontrada: %s (%s)\n", f.getName(), f.getId());
			idCarpeta = f.getId();
		}

		File fileMetadata = new File();
		fileMetadata.setName(tituloDocumento);
		fileMetadata.setMimeType("application/vnd.google-apps.document");
		fileMetadata.setParents(Collections.singletonList(idCarpeta));

		File file = service.files().create(fileMetadata).setFields("id, parents").execute();

		File fileUnico = service.files().get(file.getId())
				.setFields("id, name, capabilities, viewersCanCopyContent, writersCanShare, trashed").execute();

		try {
			System.out.println("Nombre: " + fileUnico.getName());
			System.out.println("Original Name: " + fileUnico.getOriginalFilename());
			System.out.println("viewersCanCopyContent: " + fileUnico.getViewersCanCopyContent());
			System.out.println("writersCanShare: " + fileUnico.getWritersCanShare());
			System.out.println("trashed: " + fileUnico.getTrashed());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		File fileUpdate = new File();
		fileUpdate.setTrashed(false);
		fileUpdate.setOriginalFilename(tituloDocumento);
		fileUpdate.setCapabilities(new Capabilities().setCanRename(false).setCanDownload(false).setCanDelete(false));
		fileUpdate.setViewersCanCopyContent(false);
		fileUpdate.setWritersCanShare(false);

		File fUpdate = service.files().update(file.getId(), fileUpdate)
				.setFields("id, name, capabilities, viewersCanCopyContent, writersCanShare, trashed").execute();

		File fileUpdateU = service.files().get(file.getId())
				.setFields("id, name, capabilities, viewersCanCopyContent, writersCanShare, trashed").execute();

		try {
			System.out.println("Nombre: " + fileUpdateU.getName());
			System.out.println("Original Name: " + fileUpdateU.getOriginalFilename());
			System.out.println("viewersCanCopyContent: " + fileUpdateU.getViewersCanCopyContent());
			System.out.println("writersCanShare: " + fileUpdateU.getWritersCanShare());
			System.out.println("trashed: " + fileUpdateU.getTrashed());
			System.out.println("Capabiblities can rename: " + fileUpdateU.getCapabilities().getCanRename());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return file.getId();
	}

	public static String exportarDocumento(String idEstudiante, String idDocumento, String proceso,
			String opcionTitulacion) throws IOException {
		Drive service = getDriveService();
		String nombreDocumento = "No";

		OutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		service.files().export(idDocumento, "application/pdf").executeMediaAndDownloadTo(outputStream);
		File f = service.files().get(idDocumento).setFields("id, name").execute();

		FileOutputStream fos = new FileOutputStream(new java.io.File((opcionTitulacion.compareToIgnoreCase("TT") == 0
				? UtilsArchivos.getRutaCuerpoDocumentoDriveTT(proceso) + UtilsArchivos.cambiarTildes(f.getName())
						+ ".pdf"
				: UtilsArchivos.getRutaCuerpoDocumentoDriveEC(proceso) + UtilsArchivos.cambiarTildes(f.getName())
						+ ".pdf")));
		try {
			ByteArrayOutputStream baosP = (ByteArrayOutputStream) outputStream;
			baosP.writeTo(fos);
			// nombreDocumento = f.getName();
			nombreDocumento = UtilsArchivos.cambiarTildes(f.getName()) + ".pdf";
		} catch (IOException ioe) {
			System.out.println("Ingreso al error del IOException del exportarDocumento() con el siguiente erorr:");
			ioe.printStackTrace();
		} finally {
			fos.close();
		}
		return nombreDocumento;
	}

	/**
	 * Build and return an authorized Drive client service.
	 * 
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	public static Drive getDriveService() throws IOException {
		Credential credential = authorize();
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, setHttpTimeout(credential))
				.setApplicationName(APPLICATION_NAME).build();
	}

	public static OutputStream obtenerOutputDocumento(String idDocumento) throws IOException {
		Drive service = getDriveService();

		OutputStream outputStream = new ByteArrayOutputStream();

		service.files().export(idDocumento, "application/pdf").executeMediaAndDownloadTo(outputStream);

		return outputStream;
	}

	public static void otorgarPermisos(Drive service, String idDocumento, String modalidad, String emailEstudiante,
			String emailEspecialistas) throws IOException {

		System.out.println("Dándole permiso");

		// Permission newPermission = new Permission();
		// newPermission.setType("domain");
		// newPermission.setRole("reader");
		// newPermission.setDomain("utmachala.edu.ec");
		// newPermission.setAllowFileDiscovery(false);
		// service.permissions().create(idDocumento, newPermission).execute();

		if (modalidad.compareTo("TT") == 0 && emailEstudiante.contains(";")) {
			String[] emailEstudiantes = emailEstudiante.split(";");
			for (String ee : emailEstudiantes) {
				Permission newPermission2 = new Permission();
				newPermission2.setType("user");
				newPermission2.setRole("writer");
				newPermission2.setEmailAddress(ee);
				// newPermission.setAllowFileDiscovery(true);
				service.permissions().create(idDocumento, newPermission2)
						.setEmailMessage("Se ha creado un documento en google drive"
								+ " en la cual usted y su compañer@ de trabajo de titulación podrán realizar la escritura de su trabajo escrito,"
								+ " ustedes tendrán permisos de escritura y los miembros del comité evaluador podrán realizar comentarios sobre"
								+ " los avances que vayan realizando en el documento.\n\n Pueden encontrar el acceso a su documento creado del"
								+ " Google Drive en la opción de menú, de la plataforma de titulación, Estudiante --> Examen Complexivo o Trabajo"
								+ " de Titulación --> Tutorías --> clic en el botón Documento Drive.\n\nEl botón aparecerá desactivado con el texto"
								+ " 'Aún no se ha generado el documento' cuando no exista el documento en el google drive, en caso de existir el"
								+ " documento el botón estará activo con el texto 'Ir al documento' y el botón activado.")
						.execute();
			}
		} else if (modalidad.compareTo("TT") == 0) {
			Permission newPermission2 = new Permission();
			newPermission2.setType("user");
			newPermission2.setRole("writer");
			newPermission2.setEmailAddress(emailEstudiante);
			// newPermission.setAllowFileDiscovery(true);
			service.permissions().create(idDocumento, newPermission2)
					.setEmailMessage("Se ha creado un documento en google drive"
							+ " en la cual usted podrá realizar la escritura de su trabajo escrito del trabajo de titulación,"
							+ " tendrá permiso de escritura y los miembros del comité evaluador podrán realizar comentarios sobre"
							+ " los avances que vayan realizando en el documento. \n\n Puede encontrar el acceso a su documento creado del"
							+ " Google Drive en la opción de menú, de la plataforma de titulación, Estudiante --> Examen Complexivo o"
							+ " Trabajo de Titulación --> Tutorías --> clic en el botón Documento Drive.\n\nEl botón aparecerá desactivado con el texto"
							+ " 'Aún no se ha generado el documento' cuando no exista el documento en el google drive, en caso de existir el"
							+ " documento el botón estará activo con el texto 'Ir al documento' y el botón activado.")
					.execute();
		} else {
			Permission newPermission2 = new Permission();
			newPermission2.setType("user");
			newPermission2.setRole("writer");
			newPermission2.setEmailAddress(emailEstudiante);
			// newPermission.setAllowFileDiscovery(true);
			service.permissions().create(idDocumento, newPermission2)
					.setEmailMessage("Se ha creado un documento en google drive"
							+ " en donde usted podrá realizar la escritura del informe de su caso práctico,"
							+ " usted tendrá permiso de escritura y los miembros del comité evuador podrán realizar comentarios sobre"
							+ " los avances que vayan realizando en el documento. \n\n Puede encontrar el acceso a su documento creado del"
							+ " Google Drive en la opción de menú, de la plataforma de titulación, Estudiante --> Examen Complexivo o Trabajo"
							+ " de Titulación --> Tutorías --> clic en el botón Documento Drive.\n\nEl botón aparecerá desactivado con el texto"
							+ " 'Aún no se ha generado el documento' cuando no exista el documento en el google drive, en caso de existir el"
							+ " documento el botón estará activo con el texto 'Ir al documento' y el botón activado.")
					.execute();
		}

		String[] emailsEspecialistas = emailEspecialistas.split(";");
		for (String es : emailsEspecialistas) {
			Permission newPermission3 = new Permission();
			newPermission3.setType("user");
			newPermission3.setRole("commenter");
			newPermission3.setEmailAddress(es);
			// newPermission.setAllowFileDiscovery(true);
			service.permissions().create(idDocumento, newPermission3)
					.setEmailMessage("Se ha creado un documento en google drive"
							+ " en donde usted podrá realizar comentarios acerca del avance del trabajo escrito, tanto de examen complexivo"
							+ " como trabajo de titulación de los estudiantes a los cuales usted tendrá que evaluar, esto se debe a que usted"
							+ " consta como especialista del comité evaluador. \n\nPuede encontrar el acceso al documento creado del"
							+ " Google Drive en la opción de menú, de la plataforma de titulación, Evaluador --> Examen Complexivo o"
							+ " Trabajo de Titulación --> Calificaciones --> clic en el botón Documento Drive.\n\nEl botón aparecerá debajo"
							+ " del nombre de cada estudiante, si el documento aún no se ha generado se le presentará el botón desactivado"
							+ " con el mensaje 'Aún no se ha generado el documento', en caso de existir el documento el botón estará activo con el texto 'Documento Drive'.")
					.execute();
		}

	}

	public static void probar() throws IOException {
		// Build a new authorized API client service.
		Drive service = getDriveService();

		////// Print the names and IDs for up to 10 files.
		// FileList result1 =
		// service.files().list().setPageSize(20).setFields("nextPageToken,
		// files(id, name)").execute();
		// FileList result = service.files().list().execute();
		// List<File> files = result.getFiles();
		// if (files == null || files.size() == 0) {
		// System.out.println("No files found.");
		// } else {
		// System.out.println("Files:");
		// for (File file : files) {
		// System.out.printf("%s (%s)\n", file.getName(), file.getId());
		// }
		// }
		// service.files().create(arg0)
		System.out.println("Utilizando filtros");
		String pageToken = null;
		do {
			FileList resultado = service.files().list()
					.setQ("mimeType='application/vnd.google-apps.document' and name contains 'guardado'")
					.setFields("nextPageToken, files(id, name)").setPageToken(pageToken).execute();
			for (File file : resultado.getFiles()) {
				System.out.printf("Found file: %s (%s)\n", file.getName(), file.getId());
			}
			pageToken = resultado.getNextPageToken();
		} while (pageToken != null);
		///////////////////////////////////////

		System.out.println("Creando un archivo tipo documento de google");
		File fileMetadata = new File();
		fileMetadata.setName("Ejemplo de documento de google");
		fileMetadata.setMimeType("application/vnd.google-apps.document");

		File file = service.files().create(fileMetadata).setFields("id").execute();
		System.out.println("File ID: " + file.getId());

		//////////////////////////////// Obtener el id de la carpeta y crear un
		//////////////////////////////// documento de google.
		// String idCarpeta = "";
		// FileList carpeta = service.files().list()
		// .setQ("mimeType='application/vnd.google-apps.folder' and name
		// contains 'TT_PT-010517'")
		// .setFields("nextPageToken, files(id,
		// name)").setPageToken(pageToken).execute();
		// for (File f : carpeta.getFiles()) {
		// System.out.printf("Carpeta encontrada: %s (%s)\n", f.getName(),
		// f.getId());
		// idCarpeta = f.getId();
		// }
		// System.out.println("Carpeta id: " + idCarpeta);

		// File fileMetadata = new File();
		// fileMetadata.setName("documento guardado en carpeta");
		// fileMetadata.setMimeType("application/vnd.google-apps.document");
		// fileMetadata.setParents(Collections.singletonList(idCarpeta));
		// File file = service.files().create(fileMetadata).setFields("id,
		// parents").execute();
		// System.out.println("Documento creado ID: " + file.getId());

		/////////////////////////////////////////////////////////

		System.out.println("Dándole permiso");
		String idArchivo = "";
		String pageToken1 = null;
		do {
			FileList resultado = service.files().list()
					.setQ("mimeType='application/vnd.google-apps.document' and name = '0705667509_PT-010517'")
					.setFields("nextPageToken, files(id, name)").setPageToken(pageToken1).execute();
			for (File file1 : resultado.getFiles()) {
				idArchivo = file1.getId();
				System.out.printf("Found file: %s (%s)\n", file1.getName(), file1.getId());
			}
			pageToken1 = resultado.getNextPageToken();
		} while (pageToken1 != null);

		System.out.println("Realizar el permiso al archivo con id: " + idArchivo);
		// Permission newPermission = new Permission();
		// newPermission.setType("anyone");
		// newPermission.setRole("reader");
		// newPermission.setType("domain");
		// newPermission.setRole("reader");
		// newPermission.setDomain("utmachala.edu.ec");
		// newPermission.setAllowFileDiscovery(false);
		// service.permissions().create(idArchivo, newPermission).execute();

		Permission newPermission2 = new Permission();
		newPermission2.setType("user");
		newPermission2.setRole("writer");
		newPermission2.setEmailAddress("cxvega_est@utmachala.edu.ec");
		// newPermission2.setEmailAddress("carlos_barce93@hotmail.com");
		// newPermission.setAllowFileDiscovery(true);
		service.permissions().create(idArchivo, newPermission2).execute();

		// Permission newPermission3 = new Permission();
		// newPermission3.setType("user");
		// newPermission3.setRole("commenter");
		// newPermission3.setEmailAddress("cxvega@utmachala.edu.ec");
		// newPermission.setAllowFileDiscovery(true);
		// service.permissions().create(idArchivo, newPermission3).execute();

		// Permission newPermission4 = new Permission();
		// newPermission4.setType("user");
		// newPermission4.setRole("reader");
		// newPermission4.setEmailAddress("cvcarlosvega93@gmail.com");
		// service.permissions().create(idArchivo, newPermission4).execute();
		//
		// Permission newPermission5 = new Permission();
		// newPermission5.setType("user");
		// newPermission5.setRole("reader");
		// newPermission5.setEmailAddress("faconza@utmachala.edu.ec");
		// service.permissions().create(idArchivo, newPermission5).execute();

	}

	public static void probarExportacionDocumento(String idDocumento, String proceso, String opcionTitulacion)
			throws IOException {
		Drive service = getDriveService();

		OutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		service.files().export(idDocumento, "application/pdf").executeMediaAndDownloadTo(outputStream);
		File f = service.files().get(idDocumento).setFields("id, name").execute();

		FileOutputStream fos = new FileOutputStream(new java.io.File((opcionTitulacion.compareToIgnoreCase("TT") == 0
				? UtilsArchivos.getRutaCuerpoDocumentoDriveTT(proceso) + f.getName() + ".pdf"
				: UtilsArchivos.getRutaCuerpoDocumentoDriveEC(proceso) + f.getName() + ".pdf")));
		try {
			ByteArrayOutputStream baosP = (ByteArrayOutputStream) outputStream;
			baosP.writeTo(fos);
		} catch (IOException ioe) {
			// Handle exception here
			ioe.printStackTrace();
		} finally {
			fos.close();
		}
	}

	private static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
		return new HttpRequestInitializer() {
			public void initialize(HttpRequest httpRequest) throws IOException {
				requestInitializer.initialize(httpRequest);
				httpRequest.setConnectTimeout(8 * 60000); // 5 minutes connect
															// timeout
				httpRequest.setReadTimeout(8 * 60000); // 5 minutes read
														// timeout
			}
		};
	}

	public static boolean verificarDocumento(Drive service, String idDocumento) throws IOException {

		boolean existe = true;
		if (idDocumento == null) {
			existe = false;
		} else {
			File documento = service.files().get(idDocumento).execute();
			existe = (documento != null ? true : false);
		}

		return existe;

	}

	public static void verificarDocumentosSinPermisos() {

	}

}
