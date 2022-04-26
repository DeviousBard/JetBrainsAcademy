package carsharing;

public class Main {

    public static void main(String[] args) {
        String databaseDirectory = "./src/carsharing/db/";
        String databaseFileName = "carsharing.db";
        if (args != null && args.length > 1 && args[0].equals("-databaseFileName")) {
            databaseFileName = args[1];
        }
        CarSharing app = new CarSharing(databaseDirectory, databaseFileName);
        app.runApp();
    }
}
