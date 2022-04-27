package menu;

public class MenuItem {
    private final String id;
    private final char selection;
    private final String text;

    public MenuItem(String id, char selection, String text) {
        this.id = id;
        this.selection = selection;
        this.text = text;
    }

    public String getId() {
        return this.id;
    }
    public char getSelection() {
        return selection;
    }

    public String getText() {
        return text;
    }
}
