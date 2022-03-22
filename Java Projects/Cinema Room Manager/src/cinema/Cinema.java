package cinema;

import java.util.Scanner;

public class Cinema {

    private int rows;
    private int seats;
    private int totalSeats;
    private int firstDiscountRow;
    private int totalSeatsSold = 0;
    private int[][] theaterSeats;

    private void showTheatreSeats() {
        System.out.print("\nCinema:\n  ");
        for (int i = 1; i <= seats; i++) {
            System.out.printf("%d ", i);
        }
        for (int i = 0; i < rows; i++) {
            System.out.printf("\n%d ", i + 1);
            for (int j = 0; j < seats; j++) {
                System.out.printf("%s ", theaterSeats[i][j] == 1 ? "B" : "S");
            }
        }
        System.out.println("\n");
    }

    private void getTheaterSize() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of rows: ");
        rows = scanner.nextInt();
        System.out.print("Enter the number of seats in each row: ");
        seats = scanner.nextInt();
        theaterSeats = new int[rows][seats];
        totalSeats = rows * seats;
        setFirstDiscountRow();
    }

    private void setFirstDiscountRow() {
        if (totalSeats <= 60) {
            firstDiscountRow = rows + 1;
        } else {
            firstDiscountRow = rows / 2 + 1;
        }
    }

    private int calculateTotalPossibleIncome() {
        int normalRows = firstDiscountRow - 1;
        int discountRows = rows - normalRows;
        return (10 * normalRows * seats) + (8 * discountRows * seats);
    }

    private int calculateCurrentIncome() {
        int currentIncome = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < seats; j++) {
                if (theaterSeats[i][j] == 1) {
                    currentIncome += getTicketPrice(i + 1);
                }
            }
        }
        return currentIncome;
    }

    private double calculatePercentageOfSeatsSold() {
        return ((double)totalSeatsSold / (double)totalSeats) * 100.0d;
    }

    private int getTicketPrice(int selectedRow) {
        int price = 10;
        if (selectedRow >= firstDiscountRow) {
            price = 8;
        }
        return price;
    }

    private void selectSeat() {
        Scanner scanner = new Scanner(System.in);
        int selectedRow;
        int selectedSeat;
        while (true) {
            System.out.print("\nEnter a row number: ");
            selectedRow = scanner.nextInt();
            System.out.print("Enter a seat number in that row: ");
            selectedSeat = scanner.nextInt();
            if (selectedRow < 1 || selectedRow > rows || selectedSeat < 1 || selectedSeat > seats) {
                System.out.println("\nWrong input!");
            } else if (theaterSeats[selectedRow - 1][selectedSeat - 1] == 1) {
                System.out.println("\nThat ticket has already been purchased!");
            } else {
                break;
            }
        }
        theaterSeats[selectedRow - 1][selectedSeat - 1] = 1;
        totalSeatsSold++;
        showSeatPrice(selectedRow);
    }

    private void showSeatPrice(int selectedRow) {
        System.out.printf("\nTicket price: $%d\n", this.getTicketPrice(selectedRow));
    }

    private void showStatistics() {
        System.out.printf("\nNumber of purchased tickets: %d\n", totalSeatsSold);
        System.out.printf("Percentage: %.2f%%\n", calculatePercentageOfSeatsSold());
        System.out.printf("Current income: $%d\n", calculateCurrentIncome());
        System.out.printf("Total income: $%d\n", calculateTotalPossibleIncome());
    }

    private void showMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Show the seats");
            System.out.println("2. Buy a ticket");
            System.out.println("3. Statistics");
            System.out.println("0. Exit");
            int selection = scanner.nextInt();
            if (selection == 0) {
                break;
            } else if (selection == 1) {
                showTheatreSeats();
            } else if (selection == 2) {
                selectSeat();
            } else {
                showStatistics();
            }
        }
    }

    private void runApp() {
        getTheaterSize();
        showMenu();
    }

    public static void main(String[] args) {
        Cinema app = new Cinema();
        app.runApp();
    }
}
