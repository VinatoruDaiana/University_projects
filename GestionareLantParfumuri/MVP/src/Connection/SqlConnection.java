package Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {

    private static final String URL = "jdbc:mysql://localhost:3308/tema1_ps";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "daiana07.Vinatoru2003";

    // Metodă pentru obținerea unei conexiuni la baza de date
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Driver MySQL nu a fost găsit.");
        }
    }
}
