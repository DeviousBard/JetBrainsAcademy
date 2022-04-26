package carsharing.dao;

import carsharing.model.Car;
import carsharing.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {
    @Override
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT ID, NAME, RENTED_CAR_ID FROM CUSTOMER")) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getInt(1));
                    customer.setName(rs.getString(2));
                    customer.setRentedCarId(rs.getInt(3));
                    CarDao carDao = new CarDaoImpl();
                    Car car = carDao.getCarById(customer.getRentedCarId());
                    customer.setRentedCar(car);
                    customers.add(customer);
                }
            }
        }
        return customers;
    }

    @Override
    public void addCustomer(Customer customer) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO CUSTOMER (NAME, RENTED_CAR_ID) VALUES (?, ?)")) {
            statement.setString(1, customer.getName());
            if (customer.getRentedCarId() == null) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, customer.getRentedCarId());
            }
            statement.executeUpdate();
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("UPDATE CUSTOMER SET NAME = ?, RENTED_CAR_ID = ? WHERE ID = ?")) {
            statement.setString(1, customer.getName());
            if (customer.getRentedCarId() == null) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, customer.getRentedCar().getId());
            }
            statement.setInt(3, customer.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteCustomer(Customer customer) throws SQLException {

    }
}
