package carsharing.dao;

import carsharing.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDaoImpl implements CompanyDao {

    public CompanyDaoImpl() {
    }

    @Override
    public List<Company> getAllCompanies() throws SQLException {
        List<Company> companies = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT ID, NAME FROM COMPANY")) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Company company = new Company();
                    company.setId(rs.getInt(1));
                    company.setName(rs.getString(2));
                    companies.add(company);
                }
            }
        }
        return companies;
    }

    @Override
    public Company getCompanyById(Integer id) throws SQLException {
        Company company = null;
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT ID, NAME FROM COMPANY WHERE ID = ?")) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    company = new Company();
                    company.setId(rs.getInt(1));
                    company.setName(rs.getString(2));
                }
            }
        }
        return company;
    }

    @Override
    public void addCompany(Company company) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO COMPANY (NAME) VALUES (?)")) {
            statement.setString(1, company.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void updateCompany(Company c) throws SQLException {

    }

    @Override
    public void deleteCompany(Company c) throws SQLException {

    }
}
