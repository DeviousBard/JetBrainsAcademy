package budget;

import menu.Menu;
import menu.MenuItem;

public class MenuSystem {
    private final BudgetManager budgetManager;
    private final Menu mainMenu;
    private final Menu categoryAddPurchaseMenu;
    private final Menu categoryListPurchasesMenu;
    private final Menu analyzeMenu;
    private final Menu categoryAnalyzeMenu;

    public MenuSystem(BudgetManager budgetManager) {
        this.budgetManager = budgetManager;
        this.mainMenu = buildMainMenu();
        this.categoryAddPurchaseMenu = buildCategoryAddPurchaseMenu();
        this.categoryListPurchasesMenu = buildCategoryListPurchasesMenu();
        this.analyzeMenu = buildAnalyzeMenu();
        this.categoryAnalyzeMenu = buildCategoryAnalyzeMenu();
    }

    private Menu buildMainMenu() {
        Menu menu = new Menu("Choose your action:");
        menu.addMenuItem(new MenuItem("ADD_INCOME", '1', "Add income"));
        menu.addMenuItem(new MenuItem("ADD_PURCHASE", '2', "Add purchase"));
        menu.addMenuItem(new MenuItem("LIST_PURCHASES", '3', "Show list of purchases"));
        menu.addMenuItem(new MenuItem("SHOW_BALANCE", '4', "Balance"));
        menu.addMenuItem(new MenuItem("SAVE", '5', "Save"));
        menu.addMenuItem(new MenuItem("LOAD", '6', "Load"));
        menu.addMenuItem(new MenuItem("ANALYZE", '7', "Analyze (Sort)"));
        menu.addMenuItem(new MenuItem("EXIT", '0', "Exit"));
        menu.addMenuListener(budgetManager::handleMainMenuEvents);
        return menu;
    }

    private Menu buildCategoryAddPurchaseMenu() {
        Menu menu = new Menu("Choose the type of purchase");
        this.addCategoryMenuItems(menu);
        menu.addMenuItem(new MenuItem("CATEGORY_ADD_PURCHASE_MENU_BACK", '5', "Back"));
        menu.addMenuListener(budgetManager::handleCategoryAddPurchaseMenuEvents);
        return menu;
    }

    private Menu buildCategoryListPurchasesMenu() {
        Menu menu = new Menu("Choose the type of purchases");
        this.addCategoryMenuItems(menu);
        menu.addMenuItem(new MenuItem("All", '5', "All"));
        menu.addMenuItem(new MenuItem("CATEGORY_LIST_PURCHASES_MENU_BACK", '6', "Back"));
        menu.addMenuListener(budgetManager::handleCategoryListPurchasesMenuEvents);
        return menu;
    }

    private Menu buildAnalyzeMenu() {
        Menu menu = new Menu("How do you want to sort?");
        menu.addMenuItem(new MenuItem("ALL", '1', "Sort all purchases"));
        menu.addMenuItem(new MenuItem("TYPE", '2', "Sort by type"));
        menu.addMenuItem(new MenuItem("CERTAIN_TYPE", '3', "Sort certain type"));
        menu.addMenuItem(new MenuItem("ANALYZE_MENU_BACK", '4', "Back"));
        menu.addMenuListener(budgetManager::handleAnalyzeMenuEvents);
        return menu;
    }

    private Menu buildCategoryAnalyzeMenu() {
        Menu menu = new Menu("Choose the type of purchase");
        this.addCategoryMenuItems(menu);
        menu.addMenuListener(budgetManager::handleCategoryAnalyzeMenuEvents);
        return menu;
    }

    private void addCategoryMenuItems(Menu menu) {
        menu.addMenuItem(new MenuItem("Food", '1', "Food"));
        menu.addMenuItem(new MenuItem("Clothes", '2', "Clothes"));
        menu.addMenuItem(new MenuItem("Entertainment", '3', "Entertainment"));
        menu.addMenuItem(new MenuItem("Other", '4', "Other"));
    }

    public Menu getMainMenu() {
        return mainMenu;
    }

    public Menu getCategoryAddPurchaseMenu() {
        return categoryAddPurchaseMenu;
    }

    public Menu getCategoryListPurchasesMenu() {
        return categoryListPurchasesMenu;
    }

    public Menu getAnalyzeMenu() {
        return analyzeMenu;
    }

    public Menu getCategoryAnalyzeMenu() {
        return categoryAnalyzeMenu;
    }
}
