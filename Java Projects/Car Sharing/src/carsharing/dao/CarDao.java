package carsharing.dao;

import carsharing.model.Car;

import java.sql.SQLException;
import java.util.List;

public interface CarDao {
    List<Car> getAllCarsByCompany(Integer companyId) throws SQLException;

    List<Car> getAvailableCarsByCompany(Integer companyId) throws SQLException;

    Car getCarById(Integer id) throws SQLException;

    void addCar(Car car) throws SQLException;

    void updateCar(Car car) throws SQLException;

    void deleteCar(Car c) throws SQLException;
}
