package carsharing.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String JDBC_URL = "jdbc:h2:";
    private static ConnectionManager instance;

    private final String databaseDirectory;
    private final String databaseFileName;

    private ConnectionManager(String databaseDirectory, String databaseFileName) {
        this.databaseDirectory = databaseDirectory;
        this.databaseFileName = databaseFileName;
    }

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(JDBC_URL + getInstance().databaseDirectory + getInstance().databaseFileName);
        con.setAutoCommit(true);
        return con;
    }

    public static ConnectionManager getInstance() {
        return instance;
    }

    public static void initializeConnectionManager(String databaseDirectory, String databaseFileName) {
        if (instance == null) {
            instance = new ConnectionManager(databaseDirectory, databaseFileName);
        }
    }

    static {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
