package readability;

import java.io.File;
import java.io.IOException;

public class Gobbledygook extends AbstractReadabilityScore {
    public Gobbledygook(String text) {
        super(text);
    }

    public Gobbledygook(File file) throws IOException {
        super(file);
    }

    @Override
    protected double calculateScore() {
        return 1.043d * Math.sqrt(this.getPolysyllables() * (30.0d / this.getSentences())) + 3.1291d;
    }

    @Override
    protected String getName() {
        return "Simple Measure of Gobbledygook";
    }
}
