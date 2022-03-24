//
// This solution has NO matrix for the board.  It only creates two instances of a Player object that makes use of three
// Set objects to track coordinates of the ship locations, hits, and misses.
//
package battleship;

import java.util.*;

// This abstract class keeps track of the Player's name, ships, shipLocations, and the misses and hits from the opponent
abstract class Player {
    // The player's name
    protected final String name;

    // The player's ships
    protected final Ship[] ships = new Ship[]{
            new AircraftCarrier(),
            new BattleShip(),
            new Submarine(),
            new Cruiser(),
            new Destroyer()
    };

    // Every coordinate that contains part of a ship that can be hit
    protected final Set<Coordinate> shipLocations = new HashSet<>();

    // Every coordinate the opponent selected that missed
    protected final Set<Coordinate> misses = new HashSet<>();

    // Every coordinate the opponent selected that hit
    protected final Set<Coordinate> hits = new HashSet<>();

    // Create a new Player object with the given name
    protected Player(String name) {
        this.name = name;
    }

    // Return the players name
    public String getName() {
        return name;
    }

    // Use the distance formula to determine if the player entered coordinates that match the ship's length.
    protected boolean shipLengthInvalid(Coordinate c1, Coordinate c2, Ship ship) {
        return c1.distanceFrom(c2) != (double) ship.getSize() - 1;
    }

    // Determine if the player entered coordinates that overlap with any other placed ships
    protected boolean shipOverlaps(Coordinate c1, Coordinate c2, int shipIndex) {
        for (int i = 0; i < shipIndex; i++) {
            for (Coordinate sc : ships[i].getCoordinates()) {
                if (c1.equals(sc) || c2.equals(sc)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Use the distance formula to determine if the player tried to position a ship directly beside another one. A ship
    // is beside another one, if the distance between any of its coordinates and the coordinates of the previously
    // placed ships is one or less.
    protected boolean shipProximityError(Coordinate c1, Coordinate c2, int shipIndex) {
        for (int i = 0; i < shipIndex; i++) {
            for (Coordinate sc : ships[i].getCoordinates()) {
                if (c1.distanceFrom(sc) <= 1.0d || c2.distanceFrom(sc) <= 1.0d) {
                    return true;
                }
            }
        }
        return false;
    }

    // Determine if the player entered coordinates that are not on a north/south or east/west alignment
    protected boolean shipMisaligned(Coordinate c1, Coordinate c2) {
        if (c1.getX() != c2.getX()) {
            return c1.getY() != c2.getY();
        }
        return false;
    }

    // Determine if all ships are sunk by comparing the length of the "hits" Set to the length of the
    // "shipLocations" Set
    protected boolean allShipsSunk() {
        return hits.size() == shipLocations.size();
    }

    // Determine if a ship is sunk by checking that the coordinate is part of the ship and every one of the ship's other
    // coordinates are in the "hits" Set
    protected boolean shipSunk(Coordinate currentHit) {
        for (Ship s : ships) {
            if (s.isSunk(currentHit, hits)) {
                return true;
            }
        }
        return false;
    }

    // Determine if a ship has been hit by the selected Coordinate
    public boolean isHit(Coordinate c) {
        return shipLocations.contains(c);
    }

    // Place ships
    public abstract void placeShips();

    // Take shot
    public abstract void takeShot();

    // Print the board with fog of war enabled (i.e. hide the ships)
    public void printBoard() {
        printBoard(false);
    }

    // Print the board. If "revealShips" is true, show the location of the ships.
    public abstract void printBoard(boolean revealShips);
}


// This class is an implementation of the Player class for a human player
class HumanPlayer extends Player {
    private static final String[] ROWS_LABELS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private static final String[] COLUMN_LABELS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    private enum Icons {
        FOG_OF_WAR("~"),
        SHIP("O"),
        HIT("X"),
        MISS("M");

        public final String icon;

        Icons(String icon) {
            this.icon = icon;
        }
    }

    public HumanPlayer(String name) {
        super(name);
    }

    // This is the human implementation of abstract Player method printBoard(boolean)
    @Override
    public void printBoard(boolean revealShips) {
        System.out.print("  ");
        for (String column : COLUMN_LABELS) {
            System.out.print(column + " ");
        }
        System.out.println();
        for (int i = 0; i < ROWS_LABELS.length; i++) {
            System.out.print(ROWS_LABELS[i] + " ");
            for (int j = 0; j < COLUMN_LABELS.length; j++) {
                try {
                    Coordinate c = new Coordinate(i, j);
                    if (hits.contains(c)) {
                        System.out.print(Icons.HIT.icon);
                    } else if (misses.contains(c)) {
                        System.out.print(Icons.MISS.icon);
                    } else if (shipLocations.contains(c) && revealShips) {
                        System.out.print(Icons.SHIP.icon);
                    } else {
                        System.out.print(Icons.FOG_OF_WAR.icon);
                    }
                    System.out.print(" ");
                } catch (InvalidCoordinateException e) {
                    // Intentionally ignored
                }
            }
            System.out.println();
        }
    }

    // This the human implementation of the abstract Player method placeShips()
    @Override
    public void placeShips() {
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < ships.length; i++) {
            printBoard(true);
            Ship ship = ships[i];
            System.out.printf("\nEnter the coordinates of the %s (%d cells):\n", ship.getName(), ship.getSize());
            Coordinate c1;
            Coordinate c2;
            while (true) {
                try {
                    c1 = new Coordinate(scanner.next());
                    c2 = new Coordinate(scanner.next());
                    if (shipMisaligned(c1, c2) || shipOverlaps(c1, c2, i)) {
                        System.out.println("\nError! Wrong ship location! Try again:");
                    } else if (shipLengthInvalid(c1, c2, ship)) {
                        System.out.printf("\nError! Wrong length of the %s! Try again:\n", ship.getName());
                    } else if (shipProximityError(c1, c2, i)) {
                        System.out.println("\nError! You placed it too close to another one. Try again:");
                    } else {
                        break;
                    }
                } catch (InvalidCoordinateException e) {
                    System.out.printf("%s %s\n", e.getMessage(), "Try again:");
                }
            }
            System.out.println();
            // Determine the orientation of the ship (i.e. north/south or east/west)being placed and set the
            // delta (dx or dy) to "1" in that direction
            int dx = 0;
            int dy = 0;
            if (c1.getX() != c2.getX()) {
                dx = 1;
            } else {
                dy = 1;
            }
            try {
                Coordinate[] ca = new Coordinate[]{c1, c2};
                Arrays.sort(ca);
                Coordinate startingCoordinate = ca[0];
                for (int j = 0; j < ship.getSize(); j++) {
                    Coordinate nextCoordinate = new Coordinate(startingCoordinate.getX() + (j * dx), startingCoordinate.getY() + (j * dy));
                    ship.addCoordinate(nextCoordinate);
                    shipLocations.add(nextCoordinate);
                }
            } catch (InvalidCoordinateException e) {
                e.printStackTrace();
            }
        }
        printBoard(true);
    }

    // This is the human implementation of the abstract Player method takeShot()
    public void takeShot() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String coordinateStr = scanner.next();
                Coordinate c = new Coordinate(coordinateStr);
                if (isHit(c)) {
                    hits.add(c);
                    if (allShipsSunk()) {
                        System.out.println("\nYou sank the last ship. You won. Congratulations!");
                    } else if (shipSunk(c)) {
                        System.out.println("\nYou sank a ship!");
                    } else {
                        System.out.println("\nYou hit a ship!");
                    }
                } else {
                    misses.add(c);
                    System.out.println("\nYou missed!");
                }
                break;
            } catch (InvalidCoordinateException e) {
                System.out.println("\nError! You entered the wrong coordinates! Try again:");
            }
        }
    }
}

// This class represents is a coordinate in a 2-dimensional space that is 10x10.  Coordinates may be created or
// referenced by X and Y values (0 through 9 for each) or letter/number values (A through J, and 1 through 10)
class Coordinate implements Comparable<Coordinate> {
    private static final String ROW_INPUTS = "ABCDEFGHIJ";
    private static final String COLUMN_INPUTS = "12345678910";

    private final String coordinateStr;
    private final int x;
    private final int y;

    // Create a Coordinate object by its X and Y values
    public Coordinate(int x, int y) throws InvalidCoordinateException {
        if (x < 0 || x > 9 || y < 0 || y > 9) {
            throw new InvalidCoordinateException(String.format("Error! Invalid coordinate: [%d, %d]", x, y));
        }
        this.x = x;
        this.y = y;
        this.coordinateStr = "" + ROW_INPUTS.charAt(x) + COLUMN_INPUTS.charAt(y) + (y == 9 ? "0" : "");
    }

    // Create a Coordinate object by its letter/number value (e.g. "C7")
    public Coordinate(String coordinateStr) throws InvalidCoordinateException {
        this.coordinateStr = coordinateStr.toUpperCase(Locale.ROOT);
        String[] xyStr = this.coordinateStr.split("(?<=[" + ROW_INPUTS + "])");
        int x;
        int y;
        if (xyStr.length == 2 && xyStr[0] != null && xyStr[1] != null) {
            x = ROW_INPUTS.indexOf(xyStr[0]);
            y = COLUMN_INPUTS.indexOf(xyStr[1]);
            if (x == -1 || y == -1) {
                throw new InvalidCoordinateException(String.format("Error! Invalid coordinate: %s", coordinateStr));
            }
        } else {
            throw new InvalidCoordinateException(String.format("Error! Invalid coordinate: %s", coordinateStr));
        }
        this.x = x;
        this.y = y;
    }

    // Get the distance from this Coordinate to the selected Coordinate
    public double distanceFrom(Coordinate c) {
        return Math.sqrt(Math.pow((double) this.x - (double) c.x, 2) + Math.pow((double) this.y - (double) c.y, 2));
    }

    // Return the X value of this Coordinate
    public int getX() {
        return this.x;
    }

    // Return the Y value of this Coordinate
    public int getY() {
        return this.y;
    }

    // Return the letter/number representation of this Coordinate
    @Override
    public String toString() {
        return coordinateStr;
    }

    // Override the equals() method for Coordinate. Two Coordinate objects are equal if their X and Y values
    // are the same.
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate) {
            Coordinate c = (Coordinate) obj;
            return c.x == this.x && c.y == this.y;
        }
        return false;
    }

    // Override the hashCode() method for Coordinate.  The hash code is defined as the hash code of the String
    // representation of the Coordinate object
    @Override
    public int hashCode() {
        return coordinateStr.hashCode();
    }

    // Implementation of the Comparable<Coordinate> interface's compareTo(Coordinate) method.  This Coordinate is less
    // than another one, when first, its X value is less than the other's X value.  If the X values are equal,
    // then it's less if its Y value is less than the other's Y value.  Otherwise, the Coordinates are equal
    @Override
    public int compareTo(Coordinate c) {
        if (this.x == c.x && this.y == c.y) {
            return 0;
        }
        if (this.x < c.x) {
            return -1;
        } else {
            if (this.x == c.x) {
                if (this.y < c.y) {
                    return -1;
                }
            } else {
                return 1;
            }
        }
        return 1;
    }
}

// This class is an abstract representation of a ship with a size and Set of Coordinates that it occupies
abstract class Ship {
    private final int size;
    private final Set<Coordinate> coordinates = new HashSet<>();

    // Create a new Ship object with the specified size
    public Ship(int size) {
        this.size = size;
    }

    // Add a coordinate to the Set of Coordinates that this Ship occupies
    public void addCoordinate(Coordinate c) {
        this.coordinates.add(c);
    }

    // Return the size of this ship
    public int getSize() {
        return this.size;
    }

    // Determine if the specified Coordinate has caused the ship to be sunk.
    public boolean isSunk(Coordinate c, Set<Coordinate> hits) {
        return coordinates.contains(c) && hits.containsAll(coordinates);
    }

    // Return the name of this ship
    public abstract String getName();

    // Return the Set of Coordinates this ship occupies
    public Set<Coordinate> getCoordinates() {
        return this.coordinates;
    }
}

// A concrete Ship representation of an Aircraft Carrier
class AircraftCarrier extends Ship {
    public AircraftCarrier() {
        super(5);
    }

    @Override
    public String getName() {
        return "Aircraft Carrier";
    }
}

// A concrete Ship representation of a Battleship
class BattleShip extends Ship {
    public BattleShip() {
        super(4);
    }

    @Override
    public String getName() {
        return "Battleship";
    }
}

// A concrete Ship representation of a Submarine
class Submarine extends Ship {
    public Submarine() {
        super(3);
    }

    @Override
    public String getName() {
        return "Submarine";
    }
}

// A concrete Ship representation of a Cruiser
class Cruiser extends Ship {
    public Cruiser() {
        super(3);
    }

    @Override
    public String getName() {
        return "Cruiser";
    }
}

// A concrete Ship representation of a Destroyer
class Destroyer extends Ship {
    public Destroyer() {
        super(2);
    }

    @Override
    public String getName() {
        return "Destroyer";
    }
}

// This class is the entire Battleship Game for two players
class BattleShipGame {
    public void run() {
        Player[] players = new Player[]{new HumanPlayer("Player 1"), new HumanPlayer("Player 2")};
        System.out.printf("%s, place your ships on the game field\n\n", players[0].getName());
        players[0].placeShips();
        nextPlayer();
        System.out.printf("%s, place your ships on the game field\n\n", players[1].getName());
        players[1].placeShips();
        nextPlayer();

        int currentPlayer = 1;
        do {
            currentPlayer = (currentPlayer + 1) % 2;
            int opponentPlayer = (currentPlayer + 1) % 2;
            players[opponentPlayer].printBoard();
            System.out.println("---------------------");
            players[currentPlayer].printBoard(true);
            System.out.printf("\n%s, it's your turn:\n\n", players[currentPlayer].getName());
            players[opponentPlayer].takeShot();
            nextPlayer();
        } while (!players[0].allShipsSunk() && !players[1].allShipsSunk());
    }

    // Pause for the next player
    private void nextPlayer() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nPress Enter and pass the move to another player");
        scanner.nextLine();
    }
}

// An exception class for invalid Coordinate objects.
class InvalidCoordinateException extends Exception {
    public InvalidCoordinateException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String[] args) {
        BattleShipGame app = new BattleShipGame();
        app.run();
    }
}
