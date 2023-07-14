package ec.edu.utmachala.titulacion.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class DriveServiceTitulacion {

	private static final String SERVICE_ACCT_ID = "titulacion@titulacionapp.iam.gserviceaccount.com";
	private static final String P12_FILE_PATH = "D:\\titulacionApp.p12";
	private static final String ACCOUNT_USER = "titulacion@utmachala.edu.ec";
	private static final String APPLICATION_NAME = "titulacionApp";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public static GoogleCredential credenciales() throws GeneralSecurityException, IOException {
		GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
				.setJsonFactory(JSON_FACTORY).setServiceAccountId(SERVICE_ACCT_ID)
				.setServiceAccountPrivateKeyFromP12File(new java.io.File(P12_FILE_PATH))
				.setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE)).setServiceAccountUser(ACCOUNT_USER)
				.build();
		// credential.refreshToken();
		return credential;
	}

	public static Drive driveService() throws GeneralSecurityException, IOException {
		Drive driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credenciales())
				.setApplicationName(APPLICATION_NAME).build();
		return driveService;
	}

	public static void ejecutar() throws GeneralSecurityException, IOException {

		Drive service = driveService();

		FileList result = service.files().list().execute();
		List<File> files = result.getFiles();

		if (files == null || files.size() == 0) {
			System.out.println("No Folder found.");
		} else {
			System.out.println("Files:" + files.size());
			// folderID = files.get(0).getId();
			files = result.getFiles();
			if (files == null || files.size() == 0)
				System.out.println("No files found");
			else {
				for (File file : files) {
					System.out.printf("%s (%s)\n", file.getName(), file.getId());
				}
			}
		}
	}

}
