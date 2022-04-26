package menu;

import java.util.*;

public class Menu {
    private final List<MenuItem> menuItems = new ArrayList<>();
    private final Map<Character, Integer> menuItemIndexes = new HashMap<>();
    private final List<IMenuListener> listeners = new ArrayList<>();
    private String title;

    public Menu() {
        this("");
    }

    public Menu(String title) {
        this.title = title;
    }

    public void addMenuItem(MenuItem menuItem) {
        this.menuItems.add(menuItem);
        menuItemIndexes.put(menuItem.getSelection(), menuItems.size() - 1);
    }

    public void showMenu() {
        System.out.println();
        if (this.title != null && !this.title.equals("")) {
            System.out.printf("%s\n", this.title);
        }
        for (MenuItem mi : menuItems) {
            System.out.printf("%s. %s\n", mi.getSelection(), mi.getText());
        }
    }

    public void selectMenuItem() {
        Scanner scanner = new Scanner(System.in);
        MenuItem menuItem = null;
        while (menuItem == null) {
            String input = scanner.nextLine();
            if (input != null && input.length() == 1) {
                char c = input.charAt(0);
                Integer menuItemIndex = menuItemIndexes.get(c);
                if (menuItemIndex != null) {
                    menuItem = menuItems.get(menuItemIndex);
                }
            }
        }
        for (IMenuListener menuListener : listeners) {
            menuListener.menuItemSelected(menuItem);
        }
    }

    public void addMenuListener(IMenuListener menuListener) {
        this.listeners.add(menuListener);
    }

    public void removeMenuListener(IMenuListener menuListener) {
        this.listeners.remove(menuListener);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
