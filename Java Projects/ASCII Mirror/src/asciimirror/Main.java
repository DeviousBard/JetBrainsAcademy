package asciimirror;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final Map<Character, Character> MIRRORS = Map.of(
        '<', '>',
        '[', ']',
        '{', '}',
        '(', ')',
        '/', '\\',
        '>', '<',
        ']', '[',
        '}', '{',
        ')', '(',
        '\\', '/'
    );

    private String requestInput(String prompt) {
        System.out.print(prompt);
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    private String[] readAnimalFile(String fileName) throws IOException {
        String[] result;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String lineRead;
            List<String> rows = new ArrayList<>();
            int maxRowLength = -1;
            while ((lineRead = br.readLine()) != null) {
                rows.add(lineRead);
                maxRowLength = Math.max(lineRead.length(), maxRowLength);
            }
            result = normalizeSpace(rows, maxRowLength);
        }
        return result;
    }

    private String[] normalizeSpace(List<String> rows, int lineLength) {
        String[] result = new String[rows.size()];
        String spacer = " ".repeat(lineLength);
        for (int i = 0; i < rows.size(); i++) {
            result[i] = (rows.get(i) + spacer).substring(0, lineLength);
        }
        return result;
    }

    private void mirrorAnimal(String[] animal) {
        for (String animalLine : animal) {
            System.out.println(animalLine + " | " + mirrorString(animalLine));
        }
    }

    private String mirrorString(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            char c = str.charAt(i);
            result.append(MIRRORS.getOrDefault(c, c));
        }
        return result.toString();
    }

    private void runApp() {
        String fileName = requestInput("Input the file path: \n");
        try {
            mirrorAnimal(readAnimalFile(fileName));
        } catch(IOException e) {
            System.out.println("File not found!");
        }
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.runApp();
    }
}
