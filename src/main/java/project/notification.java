package project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class Notification {
    private static final String APPLICATION_NAME = "Project Notfication";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/gmail.readonly"); // Read only
    private static final String CREDENTIALS_FILE_PATH = "token\\client_secret_451378032531-5i5ui21h5hm3dij14h7m0oikhqr9mksu.apps.googleusercontent.com.json"; // Creds of API
    private final Gmail service;

    public Notification() throws Exception{
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, JSON_FACTORY))
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

    private static Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory JSON_FACTORY) throws IOException {
        // Load client secrets.
        InputStream in = Notification.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
            .setAccessType("offline")
            .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return credential;
    }

    // Gets lists of the unreadmessages
    public List<Message> getUnreadMessages(String senderEmail) throws IOException {
        String query = "is:unread from:" + senderEmail;
        ListMessagesResponse response = service.users().messages().list("me").setQ(query).execute();
        return response.getMessages();
    }

    // Turns the list of string ID the subject
    public String getMessageSubject(String messageId) throws IOException {
        Message message = service.users().messages().get("me", messageId).execute();
        for (com.google.api.services.gmail.model.MessagePartHeader header : message.getPayload().getHeaders()) {
            if ("Subject".equals(header.getName())) {
                return header.getValue();
            }
        }
        return "No subject";
    }

    public static void getNotification(String userEmail, ArrayList<String> searchEmail) throws Exception{

        Notification notification = new Notification();

        List<List<Message>> unreadMessages = new ArrayList<>();
        for(String emails : searchEmail){
            unreadMessages.add(notification.getUnreadMessages(emails));
        }

        // Process Message
        for (List<Message> userM : unreadMessages){
            for (Message message : userM) {
                System.out.println(message + " :Subject: " + notification.getMessageSubject(message.getId()) + "\n");
            }
        }

    }
}