package carsharing.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseDaoImpl implements DatabaseDao {

    @Override
    public void dropCarTable() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection(); Statement statement = conn.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS CAR");
        }
    }

    @Override
    public void dropCompanyTable() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection(); Statement statement = conn.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS COMPANY");
        }
    }

    @Override
    public void dropCustomerTable() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection(); Statement statement = conn.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS CUSTOMER");
        }
    }

    @Override
    public void createCarTable() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection(); Statement statement = conn.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS CAR (" +
                            "ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                            "NAME VARCHAR(255) UNIQUE NOT NULL, " +
                            "COMPANY_ID INTEGER NOT NULL, " +
                            "FOREIGN KEY(COMPANY_ID) REFERENCES COMPANY(ID)" +
                            ")"
            );
        }
    }

    @Override
    public void createCompanyTable() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection(); Statement statement = conn.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS COMPANY (" +
                            "ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                            "NAME VARCHAR(255) UNIQUE NOT NULL" +
                            ")"
            );
        }
    }

    @Override
    public void createCustomerTable() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection(); Statement statement = conn.createStatement()) {
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS CUSTOMER (" +
                            "ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                            "NAME VARCHAR(255) UNIQUE NOT NULL, " +
                            "RENTED_CAR_ID INTEGER, " +
                            "FOREIGN KEY(RENTED_CAR_ID) REFERENCES CAR(ID)" +
                            ")"
            );
        }
    }

    @Override
    public void resetDatabase(String databaseDirectory, String databaseFileName) {
        File databaseFile = new File(databaseDirectory + databaseFileName);
        if (databaseFile.exists() && !databaseFile.delete()) {
            System.err.printf("Unable to delete database file: %s\n", databaseFile.getAbsoluteFile());
            System.exit(-1);
        }
    }
}
