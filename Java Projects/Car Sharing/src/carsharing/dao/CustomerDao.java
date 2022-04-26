package carsharing.dao;

import carsharing.model.Customer;

import java.sql.SQLException;
import java.util.List;

public interface CustomerDao {

    List<Customer> getAllCustomers() throws SQLException;

    void addCustomer(Customer customer) throws SQLException;

    void updateCustomer(Customer customer) throws SQLException;

    void deleteCustomer(Customer customer) throws SQLException;
}
