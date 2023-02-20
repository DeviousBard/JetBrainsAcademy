package chucknorris;

import java.util.Scanner;

public class Main {

    private String getUserInput(String prompt) {
        Scanner s = new Scanner(System.in);
        System.out.print(prompt);
        return s.nextLine();
    }

    private String charToBinary(char c) {
        String binary = Integer.toBinaryString(c);
        return "0".repeat(7 - binary.length()) + binary;
    }

    private String stringToBinary(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append(charToBinary(c));
        }
        return sb.toString();
    }

    private String encodeChuckNorrisUnaryCode(String msg) {
        String binary = stringToBinary(msg);
        String[] firstBlock = {"00", "0"};
        StringBuilder sb = new StringBuilder();
        char last = binary.charAt(0);
        int count = 0;
        for (char c : binary.toCharArray()) {
            if (c == last) {
                count++;
            } else {
                sb.append(firstBlock[last - 48]).append(' ').append("0".repeat(count)).append(' ');
                count = 1;
                last = c;
            }
        }
        sb.append(firstBlock[last - 48]).append(' ').append("0".repeat(count));
        return sb.toString();
    }

    private String decodeChuckNorrisUnaryCode(String msg) throws IllegalStateException {
        StringBuilder sb = new StringBuilder();
        String[] blocks = msg.split(" ");
        if (msg.matches("[^0\\s]") || blocks.length % 2 != 0) {
            // Validate that the "msg" contains only zeroes or spaces and that there are an even number of blocks.
            throw new IllegalStateException("Encoded string is not valid.");
        }
        for (int i = 0; i < blocks.length - 1; i += 2) {
            // Validate that the first block of the block pair is either "0" or "00".
            if (! (blocks[i].equals("0") || blocks[i].equals("00"))) {
                throw new IllegalStateException("Encoded string is not valid.");
            }
            sb.append((blocks[i].equals("0") ? "1" : "0").repeat(blocks[i + 1].length()));
        }
        String[] binaryChars = sb.toString().split("(?<=\\G.{7})");
        sb = new StringBuilder();
        for (String binaryChar : binaryChars) {
            // Validate that there are enough characters for a full byte.
            if (binaryChar.length() < 7) {
                throw new IllegalStateException("Encoded string is not valid.");
            }
            sb.append((char)Integer.parseInt(binaryChar, 2));
        }
        return sb.toString();
    }

    private void runApp() {
        String input;
        while (! (input = getUserInput("\nPlease input operation (encode/decode/exit): ")).equalsIgnoreCase("exit")) {
            if (input.equalsIgnoreCase("encode")) {
                String strToEncode = getUserInput("Input string: ");
                System.out.println("Encoded string:");
                System.out.println(encodeChuckNorrisUnaryCode(strToEncode));
            } else if (input.equalsIgnoreCase("decode")) {
                String strToDecode = getUserInput("Input encoded string: ");
                try {
                    String decodedString = decodeChuckNorrisUnaryCode(strToDecode);
                    System.out.println("Decoded string:");
                    System.out.println(decodedString);
                } catch (IllegalStateException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("There is no '" + input + "' operation");
            }
        }
        System.out.println("Bye!");
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.runApp();
    }
}
