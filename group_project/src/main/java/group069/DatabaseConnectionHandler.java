package group069;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionHandler {
    private static final String DB_URL = "jdbc:mysql://stusql.dcs.shef.ac.uk:3306/team069";
    private static final String DB_USER = "team069";
    private static final String DB_PASSWORD = "Eh4Eu2Aip";

    // Define the connection as a class member to share it across the application.
    private Connection connection = null;

    public void closeConnection() {
        // Close the connection in a separate method to ensure proper resource management.
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection openAndGetConnection() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        return this.connection;
    }
}