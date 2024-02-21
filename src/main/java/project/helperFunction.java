package project;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class HelperFunction {
    public static List<String> getConfig(){
        Properties prop = new Properties();
        List<String> config = new ArrayList<>();

        try (InputStream input = new FileInputStream("src\\main\\java\\project\\config.properties")) { // read data from a file in the form of sequence of bytes
            prop.load(input); // Loads in properties file with properties liberary

            String url = prop.getProperty("db.url");
            String username = prop.getProperty("db.username");
            String password = prop.getProperty("db.password");

            config.add(url);
            config.add(username);
            config.add(password);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return config; // Have to return in array to return multiple verables
    }
}
