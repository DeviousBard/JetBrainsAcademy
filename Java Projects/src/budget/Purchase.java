package budget;

public class Purchase implements Comparable<Purchase> {
    private String item;
    private double price;

    public Purchase(String item, double price) {
        this.item = item;
        this.price = price;
    }

    public String getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int compareTo(Purchase purchase) {
        double diff = this.price - purchase.price;
        return (diff > 0 ? -1 : diff < 0 ? 1 : 0);
    }
}
