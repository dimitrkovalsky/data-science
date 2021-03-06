import generation.LstmGenerator;

import static generation.CharacterIterator.getRussianCharacterSet;

/**
 * @author dkovalskyi
 * @since 26.05.2017
 */
public class RusSongsRunner {
    public static void main(String[] args) throws Exception {
        LstmGenerator generator = new LstmGenerator(
            "C:\\github\\data-science\\text\\rus-clean.txt",
            "rusModel",
            "rus-samples.txt",30);
        generator.run();
    }
}
