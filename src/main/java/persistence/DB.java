/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael N. Korsgaard
 */
public class DB {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static Connection conn;

    public static Connection getConnection() throws SQLException {

        // Gather information for connection for the propeties file.
        try (FileInputStream f = new FileInputStream("C:\\Users\\Bruger\\Documents\\Skole\\Undervisning\\EgneProgrammer\\2Semester\\OpgaveBoghandel\\src\\main\\java\\persistence\\db.properties")) {

            Properties pros = new Properties();
            pros.load(f);
            URL = pros.getProperty("url");
            USER = pros.getProperty("user");
            PASSWORD = pros.getProperty("password");

        } catch (IOException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            // Make the connection and send return it
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        conn = DriverManager.getConnection(URL, USER, PASSWORD);

        return conn;
    }
}
