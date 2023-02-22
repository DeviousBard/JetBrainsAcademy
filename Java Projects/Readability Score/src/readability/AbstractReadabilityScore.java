package readability;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class AbstractReadabilityScore implements IReadabilityScore {

    private final String text;
    private double words;
    private double sentences;
    private double characters;
    private double syllables;
    private double polysyllables;
    private final double score;

    protected AbstractReadabilityScore(String text) {
        this.text = (text == null ? "" : text);
        this.calculateAttributes();
        this.score = this.calculateScore();
    }

    protected AbstractReadabilityScore(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader fr = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fr.readLine()) != null) {
                sb.append(line);
            }
        }
        this.text = sb.toString();
        this.calculateAttributes();
        this.score = this.calculateScore();
    }

    private void calculateAttributes() {
        String[] sentences = text.split("[.?!]");
        this.sentences = sentences.length;
        String[] words = text.split(" ");
        this.words = words.length;
        for (String word : words) {
            this.characters += word.length();
            String vowelReplace = word.toLowerCase()
                    // Remove all special characters and numbers
                    .replaceAll("[^A-Za-z]", "")
                    // Replace any trailing "e" with the empty string
                    .replaceAll("e\\z", "")
                    // Replace all vowels with a tilde ("~")
                    .replaceAll("[aeiouy]+", "~")
                    // Remove all other characters except the tildes
                    .replaceAll("[^~]", "");
            int wordSyllables = Math.max(vowelReplace.length(), 1);
            this.syllables += wordSyllables;
            this.polysyllables += (wordSyllables > 2 ? 1 : 0);
        }
    }

    protected String getText() {
        return text;
    }

    protected double getWords() {
        return words;
    }

    protected double getSentences() {
        return sentences;
    }

    protected double getCharacters() {
        return characters;
    }

    protected double getSyllables() {
        return syllables;
    }

    protected double getPolysyllables() {
        return polysyllables;
    }

    protected double getScore() {
        return score;
    }

    public void printAttributes() {
        System.out.println("The text is:");
        System.out.println(this.text);
        System.out.println();
        System.out.printf("Words: %.0f%n", this.words);
        System.out.printf("Sentences: %.0f%n", this.sentences);
        System.out.printf("Characters: %.0f%n", this.characters);
        System.out.printf("Syllables: %.0f%n", this.syllables);
        System.out.printf("Polysyllables: %.0f%n", this.polysyllables);
    }

    public void printScoreWithAge() {
        System.out.printf("%n%s: %.2f (about %d-year-olds).", this.getName(), this.getScore(), this.getAge());
    }

    public int getAge() {
        int roundedScore = (int)Math.ceil(this.getScore());
        return (roundedScore >= 14 ? 18 : 5 + roundedScore);
    }

    protected abstract double calculateScore();

    protected abstract String getName();
}
