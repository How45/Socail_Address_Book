package project;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.gmail.model.Message;

public class Main {

    public static void getNotiication(ArrayList<String> searchEmail) throws Exception{
        Notification notification = new Notification();
        try{
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
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void main(String[] args) throws Exception {
        SQLReader admin = new SQLReader();

        // admin.insertData("'Test'", null, null, null, null);
        ArrayList<String> searchEmail = admin.getEmails();
        // admin.deleteUser("'tim'");
        // admin.detectConnection();

        getNotiication(searchEmail);
    }

}
