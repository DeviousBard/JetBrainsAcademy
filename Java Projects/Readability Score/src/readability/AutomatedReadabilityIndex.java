package readability;

import java.io.*;

public class AutomatedReadabilityIndex extends AbstractReadabilityScore {

    protected AutomatedReadabilityIndex(String text) {
        super(text);
    }

    protected AutomatedReadabilityIndex(File file) throws IOException {
        super(file);
    }

    @Override
    protected double calculateScore() {
        return 4.71d * this.getCharacters() / this.getWords() + 0.5d * this.getWords() / this.getSentences() - 21.43d;
    }

   @Override
   protected String getName() {
        return "Automated Readability Index";
    }
}
