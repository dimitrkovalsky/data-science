package markov;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Plays with StringChain with some hardcoded values.
 */
public class MarkovStringDemo {

    private static final int ORDER = 1;
    private static final int NUM_WORDS = 50;

    /**
     * Regular expression for breaking up words.
     */
    //    private static final String WORD_REGEX = "(?<=\\b\\s)";
    private static final String WORD_REGEX = "(?<=\\w\\W)";

    /**
     * Regular expression for getting individual characters.
     */
    private static final String CHAR_REGEX = "(?<=.)";

    /**
     * Random number generator for picking random items.
     */
    private static final Random rand = new Random();

    public static void main(String[] args) throws IOException {

        String regex = WORD_REGEX;
        String delimeter = "";
        if (regex.equals(WORD_REGEX)) {
            delimeter = " ";
        }

        StringChain chain = new StringChain(ORDER);
        FileInputStream inputStream = new FileInputStream(new File("D:\\github\\data-science\\text\\kapital2.txt"));
        //  Stream<String> lines = Files.lines(Paths.get("D:\\github\\data-science\\text\\kapital.txt"));

//        Files.write(Paths.get("D:\\github\\data-science\\text\\kapital2.txt"),
//                lines.filter(l -> !l.isEmpty()).collect(Collectors.toList()));
        List<String> lines = new ArrayList<>();
        new Scanner(inputStream).useDelimiter("\n\r").forEachRemaining(x -> {
            String[] split = x.split(" ");
            chain.addItems(Arrays.asList(split).iterator());
        });


//
//        // Print out the new result.
//        System.out.println("More gibberish: ");
        for (String word : chain.generate(NUM_WORDS, rand)) {
            System.out.print(word + delimeter);
        }
//        System.out.println();
    }
}