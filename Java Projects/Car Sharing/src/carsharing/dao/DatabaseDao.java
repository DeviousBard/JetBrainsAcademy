package carsharing.dao;

import java.sql.SQLException;

public interface DatabaseDao {
    void dropCarTable() throws SQLException;
    void dropCompanyTable() throws SQLException;
    void dropCustomerTable() throws SQLException;
    void createCarTable() throws SQLException;
    void createCompanyTable() throws SQLException;
    void createCustomerTable() throws SQLException;
    void resetDatabase(String databaseDirectory, String databaseFile);
}
