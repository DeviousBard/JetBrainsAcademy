package readability;

import java.io.File;
import java.io.IOException;

public class ColemanLiauIndex extends AbstractReadabilityScore {
    protected ColemanLiauIndex(String text) {
        super(text);
    }

    protected ColemanLiauIndex(File file) throws IOException {
        super(file);
    }

    @Override
    protected double calculateScore() {
        return 0.0588d * this.getCharacters() / (this.getWords() / 100.0d) - 0.296 * this.getSentences() / (this.getWords() / 100.0d) - 15.8d;
    }

    @Override
    protected String getName() {
        return "Coleman–Liau index";
    }
}
