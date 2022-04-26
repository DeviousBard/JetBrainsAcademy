package carsharing.dao;

import carsharing.model.Company;

import java.sql.SQLException;
import java.util.List;

public interface CompanyDao {

    List<Company> getAllCompanies() throws SQLException;

    void addCompany(Company c) throws SQLException;

    Company getCompanyById(Integer id) throws SQLException;

    void updateCompany(Company c) throws SQLException;

    void deleteCompany(Company c) throws SQLException;
}
