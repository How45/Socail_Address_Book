// SQL Connections
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
// import java.sql.SQLException;
import java.util.ArrayList;
// List
import java.util.List;

public class SQLReader {
    private List<String> configList = helperFunction.getConfig();
    private String url;
    private String username;
    private String password;

    public SQLReader(){
        this.url = configList.get(0);
        this.username = configList.get(1);
        this.password = configList.get(2);
    }

    public List<Integer> returnID(){
        List<Integer> ids = new ArrayList<>();
        String getLastID = "SELECT MAX(\"id\") as last_id, MAX(\"personID\") as last_personid FROM address_book;";

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            PreparedStatement stmt = connection.prepareStatement(getLastID); // Good practice against SQL injections
            ResultSet setIds = stmt.executeQuery();
            // System.out.println(setIds);
            while (setIds.next()){
                ids.add(setIds.getInt("last_id"));
                ids.add(setIds.getInt("last_personid"));
            }

            stmt.close();
            connection.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        return ids;
    }

    public void insertData(String forname, String surname, String socials1, String socials2, String notes){

        try {
            List<Integer> idList = returnID();
            int new_id = idList.get(0)+1;
            int new_personId = idList.get(1)+1;

            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            // System.out.println("Connected to the PostgreSQL server successfully.");
            String sqlInsert = "INSERT INTO address_book (id, forname, surname, socails1, socails2, notes, \"personID\") VALUES ("+new_id+", "+forname+", "+surname+", "+socials1+", "+socials2+", "+notes+", "+new_personId+");";

            PreparedStatement stmt = connection.prepareStatement(sqlInsert);
            // System.out.println(sqlInsert);
            stmt.executeUpdate();

            stmt.close();
            // connection.commit(); # Only needed if doing multiple SQL commands
            connection.close();
            // System.out.println("Connection closed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editUser(){

        try{
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String name){

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            String sqlDelete = "DELETE FROM address_book WHERE forname = "+name+";";

            PreparedStatement stmt = connection.prepareStatement(sqlDelete);
            stmt.executeUpdate();


            stmt.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        SQLReader admin = new SQLReader();
        // admin.insertData("'Test'", null, null, null, null);
        // admin.deleteUser("'tim'");

   }
}
