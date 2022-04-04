package maze;

import java.util.Scanner;

public class Main {
    Maze maze;

    private String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void generateMaze() {
        this.maze = Maze.generateMaze();
        this.displayMaze();
    }

    private void loadMaze() {
        String fileName = getUserInput();
        try {
            this.maze = Maze.loadMaze(fileName);
        } catch (Maze.MazeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void saveMaze() {
        String fileName = getUserInput();
        try {
            this.maze.saveMaze(fileName);
        } catch (Maze.MazeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void displayMaze() {
        this.maze.printMaze();
    }

    private void escapeMaze() {
        this.maze.printSolution();
    }

    private void showMenu() {
        while (true) {
            System.out.println("=== Menu ===");
            System.out.println("1. Generate a new maze.");
            System.out.println("2. Load a maze.");
            if (this.maze != null) {
                System.out.println("3. Save the maze.");
                System.out.println("4. Display the maze.");
                System.out.println("5. Find the escape.");
            }
            System.out.println("0. Exit.");
            int option;
            while (true) {
                try {
                    option = Integer.parseInt(getUserInput());
                    if (option >= 0 && option <= 5) {
                        if (this.maze == null) {
                            if (option <= 2) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Intentionally ignored
                }
                System.out.println("Incorrect option. Please try again");
            }
            switch (option) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    this.generateMaze();
                    break;
                case 2:
                    this.loadMaze();
                    break;
                case 3:
                    this.saveMaze();
                    break;
                case 4:
                    this.displayMaze();
                    break;
                case 5:
                    this.escapeMaze();
                    break;
            }
        }
    }

    private void runApp() {
        this.showMenu();
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.runApp();
    }
}