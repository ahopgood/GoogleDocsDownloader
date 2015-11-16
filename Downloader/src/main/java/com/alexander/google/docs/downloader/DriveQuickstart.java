package com.alexander.google.docs.downloader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Changes;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class DriveQuickstart {
	/** Application name **/
	private static final String APPLICATION_NAME = "Drive API Java Quickstart";
	
	/** Directory to store user credentials for this application **/
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			"src/main/resources/.credentials","drive-java-quickstart");
	
	 /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES =
        Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY);

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
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            DriveQuickstart.class.getResourceAsStream("client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws IOException
     */
    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        Drive service = getDriveService();

        // Print the names and IDs for up to 10 files.
        FileList result = service.files().list()
             .setMaxResults(10)
             .execute();
        List<File> files = result.getItems();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
//                System.out.printf("%s (%s)\n", file.getTitle(), file.getId());
            	System.out.println(file.toPrettyString());
                System.out.println(downloadFile(file));
            }
        }
        
        File file = service.files().get("11mx90clSk0xcO7xrCCGWqzpQOm1TBm-crdd328S_Jnk")
//        	.setMaxResults(1)
        	.execute();
        
        
        System.out.println(downloadFile(file));
//        Changes changes = service.changes();

    }
    
    public static StringBuilder downloadFile(File file){
    	String br = ",\n";
    	String tab = "\t";
    	StringBuilder fileInformation = new StringBuilder();
    	fileInformation
    		.append("File Name: ").append(file.getTitle()).append(br)
    		.append(tab).append("File Extension: ").append(file.getFileExtension()).append(br)
    		.append(tab).append("File Kind: ").append(file.getKind()).append(br)
    		.append(tab).append("File Size: ").append(file.getFileSize()).append(br)
    		.append(tab).append("Last Modified :").append(file.getModifiedDate()).append(br)
    		.append(tab).append("Mime Type :").append(file.getMimeType()).append(br)
    		.append(tab).append("Download Url:").append(file.getDownloadUrl()).append(br)
    		.append(tab).append("Version :").append(file.getVersion()).append(br)
    		.append(tab).append("Head Revision Id:").append(file.getHeadRevisionId()).append(br)
    		.append(tab).append("Id :").append(file.getId()).append(br);
    	if (file.getExportLinks() != null){
    		fileInformation.append(tab).append("Export Links :").append(br);
        	for (String key : file.getExportLinks().keySet()){
        		fileInformation.append(tab).append(tab).append(key+" :").append(file.getExportLinks().get(key));
        	}
    	}
    	return fileInformation;
    }
}
