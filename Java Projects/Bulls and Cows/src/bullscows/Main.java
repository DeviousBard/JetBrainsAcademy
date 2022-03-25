package bullscows;

import java.util.*;

class BullsCowsGame {
    public void runApp() {
        int codeLength = getCodeLength();
        int numberOfCodeSymbols = getNumberOfCodeSymbols(codeLength);
        String code = generateCode(codeLength, numberOfCodeSymbols);
        playGame(code);
    }

    private int getCodeLength() {
        Scanner scanner = new Scanner(System.in);
        int codeLength = -1;
        System.out.println("Input the length of the secret code:");
        String input = "";
        try {
            input = scanner.next();
            codeLength = Integer.parseInt(input);
            if (codeLength > 36) {
                System.out.printf("Error: can't generate a secret number with a length of %d because there" +
                        " aren't enough unique digits.\n\n", codeLength);
                System.exit(0);
            }
            if (codeLength < 1) {
                System.out.println("Error: the secret code length must be between 1 and 36.");
                System.exit(0);
            }
        } catch(Exception e) {
            System.out.printf("Error: \"%s\" isn't a valid number.", input);
            System.exit(0);
        }
        return codeLength;
    }

    private int getNumberOfCodeSymbols(int codeLength) {
        Scanner scanner = new Scanner(System.in);
        int numberOfCodeSymbols;
        System.out.println("Input the number of possible symbols in the code:");
        numberOfCodeSymbols = scanner.nextInt();
        if (numberOfCodeSymbols > 36) {
            System.out.println("Error: maximum number of possible symbols in the code is 36 (0-9, a-z).");
            System.exit(0);
        }
        if (numberOfCodeSymbols < codeLength) {
            System.out.printf("Error: it's not possible to generate a code with a" +
                    " length of %d with %d unique symbols.", codeLength, numberOfCodeSymbols);
            System.exit(0);
        }
        return numberOfCodeSymbols;
    }

    private String generateCode(int codeLength, int numberOfCodeSymbols) {
        final String symbols = "0123456789abcdefghijklmnopqrstuvwxyz";
        final String mask = "************************************";
        Random rand = new Random();
        String code;
        Set<String> symbolSet = new HashSet<>();
        while (symbolSet.size() < codeLength) {
            symbolSet.add(Character.toString(symbols.charAt(rand.nextInt(numberOfCodeSymbols))));
        }
        List<String> symbolList = new ArrayList<>(symbolSet);
        Collections.shuffle(symbolList);
        code = String.join("", symbolList);
        String range;
        if (numberOfCodeSymbols < 11) {
            range = String.format("(0-%s)", symbols.charAt(numberOfCodeSymbols - 1));
        } else {
            range = String.format("(0-9, a-%s)", symbols.charAt(numberOfCodeSymbols - 1));
        }

        System.out.printf("The secret is prepared: %s %s.\n", mask.substring(0, codeLength), range);
        System.out.println(code);
        return code;
    }

    public void playGame(String code) {
        Scanner scanner = new Scanner(System.in);
        String[] codeChars = code.split("(?<=.)");
        int codeLength = codeChars.length;
        int turn = 0;
        System.out.println("Okay, let's start a game!");
        while (true) {
            System.out.printf("Turn %d:\n", ++turn);
            String[] input = scanner.nextLine().split("(?<=.)");
            int cows = 0;
            int bulls = 0;
            for (int i = 0; i < input.length; i++) {
                for (int j = 0; j < codeChars.length; j++) {
                    if (input[i].equals(codeChars[j])) {
                        if (i == j) {
                            bulls++;
                        } else {
                            cows++;
                        }
                    }
                }
            }
            System.out.print("Grade: ");
            if (cows == 0 && bulls == 0) {
                System.out.println("None.");
            } else {
                if (bulls > 0) {
                    System.out.printf("%d bull" + (bulls > 1 ? "s" : ""), bulls);
                }
                if (cows > 0 && bulls > 0) {
                    System.out.print(" and ");
                }
                if (cows > 0) {
                    System.out.printf("%d cow" + (cows > 1 ? "s" : ""), cows);
                }
                System.out.print(".\n");
            }
            if (bulls == codeLength) {
                break;
            }
        }
        System.out.println("Congratulations! You guessed the secret code.");
    }
}

public class Main {
    public static void main(String[] args) {
        BullsCowsGame app = new BullsCowsGame();
        app.runApp();
    }
}
