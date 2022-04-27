package budget;

import menu.Menu;
import menu.MenuItem;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BudgetManager {

    private static final String SAVE_FILE_NAME = "purchases.txt";
    private final MenuSystem menuSystem;
    private Map<String, List<Purchase>> purchases = new HashMap<>();
    private double balance = 0.0d;
    private Menu currentMenu;


    public BudgetManager() {
        this.menuSystem = new MenuSystem(this);
    }

    public void runApp() {
        this.currentMenu = this.menuSystem.getMainMenu();
        while (currentMenu != null) {
            this.currentMenu.showMenu();
            this.currentMenu.selectMenuItem();
        }
    }

    private void addIncome() {
        System.out.println("\nEnter income:");
        String amount = this.getUserInput();
        this.balance += Double.parseDouble(amount);
        System.out.println("Income was added!");
    }

    private void addPurchase(String category) {
        System.out.println("\nEnter purchase name:");
        String item = this.getUserInput();
        System.out.println("Enter its price:");
        double price = Double.parseDouble(this.getUserInput());
        Purchase purchase = new Purchase(item, price);
        List<Purchase> purchaseList = this.purchases.getOrDefault(category, new ArrayList<>());
        this.purchases.put(category, purchaseList);
        purchaseList.add(purchase);
        this.balance -= price;
        if (this.balance < 0.0d) {
            this.balance = 0.0d;
        }
        System.out.println("Purchase was added!");
    }

    private void listPurchases(String category) {
        System.out.printf("\n%s:", category);
        List<Purchase> purchaseList = new ArrayList<>();
        if (category.equals("All")) {
            for (String categoryKey : this.purchases.keySet()) {
                purchaseList.addAll(this.purchases.getOrDefault(categoryKey, new ArrayList<>()));
            }
        } else {
            purchaseList = this.purchases.getOrDefault(category, new ArrayList<>());
        }
        displayPurchaseList(purchaseList);
    }

    private void showBalance() {
        System.out.printf("\nBalance: $%.2f\n", this.balance);
    }

    private String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public void handleMainMenuEvents(MenuItem menuItem) {
        String id = menuItem.getId();
        switch (id) {
            case "ADD_INCOME":
                this.addIncome();
                break;
            case "ADD_PURCHASE":
                this.currentMenu = this.menuSystem.getCategoryAddPurchaseMenu();
                break;
            case "LIST_PURCHASES":
                this.currentMenu = this.menuSystem.getCategoryListPurchasesMenu();
                break;
            case "SHOW_BALANCE":
                this.showBalance();
                break;
            case "SAVE":
                this.saveBudget();
                break;
            case "LOAD":
                this.loadBudget();
                break;
            case "ANALYZE":
                this.currentMenu = this.menuSystem.getAnalyzeMenu();
                break;
            case "EXIT":
                System.out.println("\nBye!");
                this.currentMenu = null;
                break;
        }
    }

    private void saveBudget() {
        try (FileWriter fw = new FileWriter(SAVE_FILE_NAME);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(this.balance);
            for (String category : this.purchases.keySet()) {
                for (Purchase purchase : this.purchases.get(category)) {
                    pw.printf("%s~%s~%.2f\n", category, purchase.getItem(), purchase.getPrice());
                }
            }
            System.out.println("\nPurchases were saved!");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void loadBudget() {
        this.purchases = new HashMap<>();
        try (FileReader fr = new FileReader(SAVE_FILE_NAME);
             BufferedReader br = new BufferedReader(fr)) {
            String balanceStr = br.readLine();
            if (balanceStr != null) {
                this.balance = Double.parseDouble(balanceStr);
            }
            String purchaseStr;
            while ((purchaseStr = br.readLine()) != null) {
                String[] parsedPurchase = purchaseStr.split("~");
                String category = parsedPurchase[0];
                String item = parsedPurchase[1];
                double price = Double.parseDouble(parsedPurchase[2]);
                List<Purchase> purchaseList = this.purchases.getOrDefault(category, new ArrayList<>());
                this.purchases.put(category, purchaseList);
                purchaseList.add(new Purchase(item, price));
            }
            System.out.println("\nPurchases were loaded!");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void analyzeAll() {
        System.out.print("\nAll:");
        List<Purchase> purchaseList = new ArrayList<>();
        for (String categoryKey : this.purchases.keySet()) {
            purchaseList.addAll(this.purchases.getOrDefault(categoryKey, new ArrayList<>()));
        }
        purchaseList = purchaseList.stream().sorted().collect(Collectors.toList());
        displayPurchaseList(purchaseList);
    }

    private void displayPurchaseList(List<Purchase> purchaseList) {
        if (purchaseList.size() == 0) {
            System.out.println("\nThe purchase list is empty");
        } else {
            double total = 0.0d;
            System.out.println();
            for (Purchase purchase : purchaseList) {
                String item = purchase.getItem();
                double price = purchase.getPrice();
                System.out.printf("%s $%.2f\n", item, price);
                total += price;
            }
            System.out.printf("Total sum: $%.2f\n", total);
        }
    }

    private void analyzeByTypes() {
        Map<Double, String> sumByCategory = new TreeMap<>(Collections.reverseOrder());
        System.out.println("\nTypes:");
        double total = 0.0d;
        for (String category : this.purchases.keySet()) {
            List<Purchase> purchaseList = this.purchases.getOrDefault(category, new ArrayList<>());
            double sum = purchaseList.stream().mapToDouble(Purchase::getPrice).reduce(0.0d, Double::sum);
            sumByCategory.put(sum, category);
            total += sum;
        }
        for (double sum : sumByCategory.keySet()) {
            System.out.printf("%s - $%.2f\n", sumByCategory.get(sum), sum);
        }
        System.out.printf("Total sum: $%.2f\n", total);
    }

    private void analyzeCertainType(String category) {
        List<Purchase> purchaseList = this.purchases.getOrDefault(category, new ArrayList<>());
        purchaseList = purchaseList.stream().sorted().collect(Collectors.toList());
        System.out.printf("\n%s:", category);
        this.displayPurchaseList(purchaseList);
    }

    public void handleCategoryAddPurchaseMenuEvents(MenuItem menuItem) {
        String id = menuItem.getId();
        if (id.equals("CATEGORY_ADD_PURCHASE_MENU_BACK")) {
            this.currentMenu = this.menuSystem.getMainMenu();
        } else {
            this.addPurchase(menuItem.getId());
        }
    }

    public void handleCategoryListPurchasesMenuEvents(MenuItem menuItem) {
        String id = menuItem.getId();
        if (id.equals("CATEGORY_LIST_PURCHASES_MENU_BACK")) {
            this.currentMenu = this.menuSystem.getMainMenu();
        } else {
            this.listPurchases(menuItem.getId());
        }
    }

    public void handleAnalyzeMenuEvents(MenuItem menuItem) {
        String id = menuItem.getId();
        switch (id) {
            case "ALL":
                this.analyzeAll();
                break;
            case "TYPE":
                this.analyzeByTypes();
                break;
            case "CERTAIN_TYPE":
                this.currentMenu = this.menuSystem.getCategoryAnalyzeMenu();
                break;
            case "ANALYZE_MENU_BACK":
                this.currentMenu = this.menuSystem.getMainMenu();
                break;
        }
    }

    public void handleCategoryAnalyzeMenuEvents(MenuItem menuItem) {
        String id = menuItem.getId();
        this.analyzeCertainType(id);
        this.currentMenu = this.menuSystem.getAnalyzeMenu();
    }
}
