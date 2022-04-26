package carsharing;

import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;
import menu.Menu;
import menu.MenuItem;

import java.util.List;

public class MenuSystem {

    private final CarSharing carSharing;
    private final Menu mainMenu;
    private final Menu companyMenu;
    private final Menu customerMenu;
    private final Menu carMenu;

    private Menu companyListMenu;
    private Menu customerListMenu;
    private Menu rentalCompanyListMenu;
    private Menu rentalCarListMenu;

    public MenuSystem(CarSharing carSharing) {
        this.carSharing = carSharing;
        this.mainMenu = this.buildMainMenu();
        this.companyMenu = this.buildCompanyMenu();
        this.customerMenu = this.buildCustomerMenu();
        this.carMenu = this.buildCarMenu();
    }

    private Menu buildMainMenu() {
        Menu menu = new Menu();
        menu.addMenuItem(new MenuItem("LOG_IN_MANAGER", '1', "Log in as a manager"));
        menu.addMenuItem(new MenuItem("LOG_IN_CUSTOMER", '2', "Log in as a customer"));
        menu.addMenuItem(new MenuItem("CREATE_CUSTOMER", '3', "Create a customer"));
        menu.addMenuItem(new MenuItem("EXIT", '0', "Exit"));
        menu.addMenuListener(carSharing::handleMainMenuEvents);
        return menu;
    }

    private Menu buildCompanyMenu() {
        Menu menu = new Menu();
        menu.addMenuItem(new MenuItem("LIST_COMPANIES", '1', "Company list"));
        menu.addMenuItem(new MenuItem("CREATE_COMPANY", '2', "Create a company"));
        menu.addMenuItem(new MenuItem("COMPANY_MENU_BACK", '0', "Back"));
        menu.addMenuListener(carSharing::handleCompanyMenuEvents);
        return menu;
    }

    private Menu buildCarMenu() {
        Menu menu = new Menu();
        menu.addMenuItem(new MenuItem("LIST_CARS", '1', "Car list"));
        menu.addMenuItem(new MenuItem("CREATE_CAR", '2', "Create a car"));
        menu.addMenuItem(new MenuItem("CAR_MENU_BACK", '0', "Back"));
        menu.addMenuListener(carSharing::handleCarMenuEvents);
        return menu;
    }

    private Menu buildCustomerMenu() {
        Menu menu = new Menu();
        menu.addMenuItem(new MenuItem("RENT_CAR", '1', "Rent a car"));
        menu.addMenuItem(new MenuItem("RETURN_CAR", '2', "Return a rented car"));
        menu.addMenuItem(new MenuItem("SHOW_RENTED_CAR", '3', "My rented car"));
        menu.addMenuItem(new MenuItem("CUSTOMER_MENU_BACK", '0', "Back"));
        menu.addMenuListener(carSharing::handleCustomerMenuEvents);
        return menu;
    }

    public void buildCustomerListMenu(List<Customer> customerList) {
        this.customerListMenu = new Menu("\nCustomer list:");
        int index = 1;
        for (Customer customer : customerList) {
            customerListMenu.addMenuItem(new MenuItem(String.valueOf(index), String.valueOf(index).charAt(0), customer.getName()));
            index++;
        }
        customerListMenu.addMenuItem(new MenuItem("CUSTOMER_LIST_MENU_BACK", '0', "Back"));
        customerListMenu.addMenuListener(carSharing::handleCustomerListMenuEvents);
    }

    public void buildCompanyListMenu(List<Company> companyList) {
        this.companyListMenu = new Menu("\nChoose the company:");
        int index = 1;
        for (Company company : companyList) {
            companyListMenu.addMenuItem(new MenuItem(String.valueOf(index), String.valueOf(index).charAt(0), company.getName()));
            index++;
        }
        companyListMenu.addMenuItem(new MenuItem("COMPANY_LIST_MENU_BACK", '0', "Back"));
        companyListMenu.addMenuListener(carSharing::handleCompanyListMenuEvents);
    }

    public void buildRentalCompanyListMenu(List<Company> companyList) {
        int index = 1;
        this.rentalCompanyListMenu = new Menu("Choose a company:");
        for (Company company : companyList) {
            this.rentalCompanyListMenu.addMenuItem(new MenuItem(String.valueOf(index), String.valueOf(index).charAt(0), company.getName()));
            index++;
        }
        this.rentalCompanyListMenu.addMenuItem(new MenuItem("RENTAL_COMPANY_LIST_MENU_BACK", '0', "Back"));
        this.rentalCompanyListMenu.addMenuListener(carSharing::handleRentalCompanyListMenu);
    }

    public void buildRentalCarListMenu(List<Car> carList) {
        int index = 1;
        this.rentalCarListMenu = new Menu("Choose a car!");
        for (Car car : carList) {
            this.rentalCarListMenu.addMenuItem(new MenuItem(String.valueOf(index), String.valueOf(index).charAt(0), car.getName()));
            index++;
        }
        this.rentalCarListMenu.addMenuItem(new MenuItem("RENTAL_CAR_LIST_MENU_BACK", '0', "Back"));
        this.rentalCarListMenu.addMenuListener(carSharing::handleRentalCarListMenu);
    }

    public Menu getMainMenu() {
        return mainMenu;
    }

    public Menu getCompanyMenu() {
        return companyMenu;
    }

    public Menu getCustomerMenu() {
        return customerMenu;
    }

    public Menu getCarMenu() {
        return carMenu;
    }

    public Menu getCompanyListMenu() {
        return companyListMenu;
    }

    public Menu getCustomerListMenu() {
        return customerListMenu;
    }

    public Menu getRentalCompanyListMenu() {
        return rentalCompanyListMenu;
    }

    public Menu getRentalCarListMenu() {
        return rentalCarListMenu;
    }
}
