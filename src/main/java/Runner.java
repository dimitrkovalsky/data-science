import static generation.CharacterIterator.getRussianCharacterSet;

/**
 * @author dkovalskyi
 * @since 26.05.2017
 */
public class Runner {
    public static void main(String[] args) {
        char[] set = getRussianCharacterSet();
        System.out.println(set);
    }
}
