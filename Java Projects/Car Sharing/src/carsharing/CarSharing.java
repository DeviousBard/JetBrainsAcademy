package carsharing;

import carsharing.dao.*;
import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;
import menu.Menu;
import menu.MenuItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CarSharing {
    private final MenuSystem menuSystem = new MenuSystem(this);
    private Menu currentMenu = null;
    private List<Company> currentCompanyList = new ArrayList<>();
    private List<Customer> currentCustomerList = new ArrayList<>();
    private List<Car> currentCarList = new ArrayList<>();
    private Company selectedCompany;
    private Customer selectedCustomer;
    private Car selectedCar;


    public CarSharing(String databaseDirectory, String databaseFileName) {
        try {
            ConnectionManager.initializeConnectionManager(databaseDirectory, databaseFileName);
            this.createDatabase(databaseDirectory, databaseFileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void runApp() {
        this.currentMenu = this.menuSystem.getMainMenu();
        //noinspection InfiniteLoopStatement
        while (true) {
            this.currentMenu.showMenu();
            this.currentMenu.selectMenuItem();
        }
    }

    public void handleMainMenuEvents(MenuItem menuItem) {
        String id = menuItem.getId();
        try {
            switch (id) {
                case "LOG_IN_MANAGER":
                    this.currentMenu = this.menuSystem.getCompanyMenu();
                    break;
                case "LOG_IN_CUSTOMER":
                    this.loginCustomer();
                    break;
                case "CREATE_CUSTOMER":
                    this.addCustomer();
                    break;
                case "EXIT":
                    System.exit(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void handleCompanyMenuEvents(MenuItem menuItem) {
        try {
            String id = menuItem.getId();
            switch (id) {
                case "LIST_COMPANIES":
                    this.listCompanies();
                    break;
                case "CREATE_COMPANY":
                    this.addCompany();
                    break;
                case "COMPANY_MENU_BACK":
                    this.currentMenu = this.menuSystem.getMainMenu();
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void handleCompanyListMenuEvents(MenuItem menuItem) {
        if (menuItem.getId().equals("COMPANY_LIST_MENU_BACK")) {
            this.currentMenu = this.menuSystem.getCompanyMenu();
        } else {
            this.menuSystem.getCarMenu().setTitle("'" + menuItem.getText() + "' company");
            int index = Integer.parseInt(menuItem.getId()) - 1;
            this.selectedCompany = this.currentCompanyList.get(index);
            this.currentMenu = this.menuSystem.getCarMenu();
        }
    }

    public void handleCarMenuEvents(MenuItem menuItem) {
        try {
            String id = menuItem.getId();
            switch (id) {
                case "LIST_CARS":
                    this.displayCarList(this.selectedCompany);
                    break;
                case "CREATE_CAR":
                    this.addCar(this.selectedCompany);
                    break;
                case "CAR_MENU_BACK":
                    this.currentMenu = this.menuSystem.getCompanyMenu();
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void handleCustomerMenuEvents(MenuItem menuItem) {
        try {
            String id = menuItem.getId();
            switch (id) {
                case "RENT_CAR":
                    this.selectRentalCompany(this.selectedCustomer);
                    break;
                case "RETURN_CAR":
                    this.returnCar(this.selectedCustomer);
                    break;
                case "SHOW_RENTED_CAR":
                    this.displayRentedCar(this.selectedCustomer);
                    break;
                case "CUSTOMER_MENU_BACK":
                    this.currentMenu = this.menuSystem.getMainMenu();
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void addCustomer() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter the customer name:");
        String customerName = scanner.nextLine();
        CustomerDao dao = new CustomerDaoImpl();
        Customer customer = new Customer();
        customer.setName(customerName);
        dao.addCustomer(customer);
    }

    private void loginCustomer() throws SQLException {
        CustomerDao dao = new CustomerDaoImpl();
        this.currentCustomerList = dao.getAllCustomers();
        if (this.currentCustomerList.size() > 0) {
            this.menuSystem.buildCustomerListMenu(this.currentCustomerList);
            this.currentMenu = this.menuSystem.getCustomerListMenu();
        } else {
            System.out.println("\nThe customer list is empty!");
        }
    }

    private void displayRentedCar(Customer customer) throws SQLException {
        if (customer.getRentedCar() != null) {
            Car rentedCar = customer.getRentedCar();
            System.out.println("\nYour rented car:");
            System.out.println(rentedCar.getName());
            System.out.println("Company:");
            System.out.println(rentedCar.getCompany().getName());
        } else {
            System.out.println("\nYou didn't rent a car!");
        }
    }

    private void selectRentalCompany(Customer customer) throws SQLException {
        if (customer.getRentedCar() == null) {
            CompanyDao companyDao = new CompanyDaoImpl();
            this.currentCompanyList = companyDao.getAllCompanies();
            if (this.currentCompanyList.size() > 0) {
                this.menuSystem.buildRentalCompanyListMenu(this.currentCompanyList);
                this.currentMenu = this.menuSystem.getRentalCompanyListMenu();
            } else {
                System.out.println("\nThe company list is empty!");
            }
        } else {
            System.out.println("\nYou've already rented a car!");
        }
    }

    private void returnCar(Customer customer) throws SQLException {
        if (customer.getRentedCar() != null) {
            customer.setRentedCarId(null);
            customer.setRentedCar(null);
            CustomerDao dao = new CustomerDaoImpl();
            dao.updateCustomer(customer);
            System.out.println("\nYou've returned a rented car!");
        } else {
            System.out.println("\nYou didn't rent a car!");
        }
    }

    public void handleRentalCompanyListMenu(MenuItem menuItem) {
        String id = menuItem.getId();
        if (id.equals("RENTAL_COMPANY_LIST_MENU_BACK")) {
            this.currentMenu = this.menuSystem.getMainMenu();
        } else {
            int index = Integer.parseInt(menuItem.getId());
            this.selectedCompany = this.currentCompanyList.get(index - 1);
            this.selectRentalCar();
        }
    }

    private void selectRentalCar() {
        try {
            CarDao carDao = new CarDaoImpl();
            this.currentCarList = carDao.getAvailableCarsByCompany(this.selectedCompany.getId());
            if (this.currentCarList.size() > 0) {
                this.menuSystem.buildRentalCarListMenu(this.currentCarList);
                this.currentMenu = this.menuSystem.getRentalCarListMenu();
            } else {
                System.out.printf("\nNo available cars in the '%s' company.\n", this.selectedCompany.getName());
                this.currentMenu = this.menuSystem.getRentalCompanyListMenu();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void handleRentalCarListMenu(MenuItem menuItem) {
        String id = menuItem.getId();
        if (id.equals("RENTAL_CAR_LIST_MENU_BACK")) {
            this.currentMenu = this.menuSystem.getRentalCompanyListMenu();
        } else {
            int index = Integer.parseInt(id) - 1;
            this.selectedCar = this.currentCarList.get(index);
            this.assignCarToCustomer();
            this.currentMenu = this.menuSystem.getCustomerMenu();
        }
    }

    public void handleCustomerListMenuEvents(MenuItem menuItem) {
        String id = menuItem.getId();
        if (id.equals("CUSTOMER_LIST_MENU_BACK")) {
            this.currentMenu = this.menuSystem.getMainMenu();
        } else {
            Integer index = null;
            try {
                index = Integer.parseInt(id);
            } catch (Exception e) {
                // Intentionally ignored
            }
            if (index != null) {
                this.selectedCustomer = this.currentCustomerList.get(index - 1);
                this.currentMenu = this.menuSystem.getCustomerMenu();
            }
        }
    }

    private void assignCarToCustomer() {
        CustomerDao dao = new CustomerDaoImpl();
        this.selectedCustomer.setRentedCar(this.selectedCar);
        this.selectedCustomer.setRentedCarId(this.selectedCar.getId());
        try {
            dao.updateCustomer(this.selectedCustomer);
            System.out.printf("\nYou rented '%s'\n", this.selectedCar.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void displayCarList(Company company) throws SQLException {
        CarDao dao = new CarDaoImpl();
        List<Car> carList = dao.getAllCarsByCompany(company.getId());
        if (carList.size() > 0) {
            System.out.println("\nCar list:");
            int index = 1;
            for (Car car : carList) {
                System.out.printf("%d. %s\n", index++, car.getName());
            }
        } else {
            System.out.println("\nThe car list is empty!");
        }
    }

    private void addCar(Company company) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter the car name:");
        String carName = scanner.nextLine();
        Car car = new Car();
        car.setCompanyId(company.getId());
        car.setName(carName);
        CarDao dao = new CarDaoImpl();
        dao.addCar(car);
        System.out.println("The car was added!");
    }

    private void listCompanies() throws SQLException {
        CompanyDao dao = new CompanyDaoImpl();
        this.currentCompanyList = dao.getAllCompanies();
        if (this.currentCompanyList.size() > 0) {
            this.menuSystem.buildCompanyListMenu(this.currentCompanyList);
            this.currentMenu = this.menuSystem.getCompanyListMenu();
        } else {
            System.out.println("\nThe company list is empty!");
        }
    }

    private void addCompany() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter the company name:");
        String companyName = scanner.nextLine();
        Company company = new Company();
        company.setName(companyName);
        CompanyDao dao = new CompanyDaoImpl();
        dao.addCompany(company);
        System.out.println("The company was created!");
    }

    private void createDatabase(String databaseDirectory, String databaseFileName) throws SQLException {
        DatabaseDao dao = new DatabaseDaoImpl();
//        dao.resetDatabase(databaseDirectory, databaseFileName);
//        dao.dropCustomerTable();
//        dao.dropCarTable();
//        dao.dropCompanyTable();
        dao.createCompanyTable();
        dao.createCarTable();
        dao.createCustomerTable();
    }
}
