package readability;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private void runApp(String fileName) {
        try {
            IReadabilityScore ari = new AutomatedReadabilityIndex(new File(fileName));
            IReadabilityScore fk = new FleschKincaid(new File(fileName));
            IReadabilityScore smog = new Gobbledygook(new File(fileName));
            IReadabilityScore cli = new ColemanLiauIndex(new File(fileName));

            ari.printAttributes();

            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine().toLowerCase();
            switch (input) {
                case "ari" -> ari.printScoreWithAge();
                case "fk" -> fk.printScoreWithAge();
                case "smog" -> smog.printScoreWithAge();
                case "cl" -> cli.printScoreWithAge();
                case "all" -> {
                    ari.printScoreWithAge();
                    fk.printScoreWithAge();
                    smog.printScoreWithAge();
                    cli.printScoreWithAge();
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.runApp(args[0]);
    }
}
