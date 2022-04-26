package carsharing.dao;

import carsharing.model.Car;
import carsharing.model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDaoImpl implements CarDao {

    @Override
    public List<Car> getAllCarsByCompany(Integer companyId) throws SQLException {
        List<Car> cars = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT ID, NAME, COMPANY_ID FROM CAR WHERE COMPANY_ID = ?")) {
            statement.setInt(1, companyId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Car car = buildCar(rs);
                    cars.add(car);
                }
            }
        }
        return cars;
    }

    @Override
    public List<Car> getAvailableCarsByCompany(Integer companyId) throws SQLException {
        List<Car> cars = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT C.ID, C.NAME, C.COMPANY_ID FROM CAR C WHERE C.COMPANY_ID = ? AND NOT EXISTS(SELECT 'X' FROM CUSTOMER WHERE RENTED_CAR_ID = C.ID)")) {
            statement.setInt(1, companyId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Car car = buildCar(rs);
                    cars.add(car);
                }
            }
        }
        return cars;
    }

    @Override
    public Car getCarById(Integer id) throws SQLException {
        Car car = null;
        if (id != null) {
            try (Connection conn = ConnectionManager.getConnection();
                 PreparedStatement statement = conn.prepareStatement("SELECT ID, NAME, COMPANY_ID FROM CAR WHERE ID = ?")) {
                statement.setInt(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        statement.setInt(1, id);
                        car = buildCar(rs);
                    }
                }
            }
        }
        return car;
    }

    @Override
    public void addCar(Car car) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO CAR (NAME, COMPANY_ID) VALUES (?, ?)")) {
            statement.setString(1, car.getName());
            statement.setInt(2, car.getCompanyId());
            statement.executeUpdate();
        }
    }

    @Override
    public void updateCar(Car car) throws SQLException {
    }

    @Override
    public void deleteCar(Car c) throws SQLException {
    }

    private Car buildCar(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getInt(1));
        car.setName(rs.getString(2));
        car.setCompanyId(rs.getInt(3));
        CompanyDao companyDao = new CompanyDaoImpl();
        Company company = companyDao.getCompanyById(car.getCompanyId());
        car.setCompany(company);
        return car;
    }
}
