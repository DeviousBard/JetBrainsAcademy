package readability;

import java.io.File;
import java.io.IOException;

public class FleschKincaid extends AbstractReadabilityScore {

    public FleschKincaid(String text) {
        super(text);
    }

    public FleschKincaid(File file) throws IOException {
        super(file);
    }

    @Override
    protected double calculateScore() {
        return 0.39d * this.getWords() / this.getSentences() + 11.8 * this.getSyllables() / this.getWords() - 15.59;
    }

    @Override
    protected String getName() {
        return "Flesch–Kincaid readability tests";
    }
}
