package markov;

/**
 * @author Dmytro_Kovalskyi.
 * @since 30.01.2017.
 */
import java.util.Iterator;

public class StringChain extends MarkovChain<String> {
    public StringChain(int order) {
        super(order);
    }

    @Override
    public void addItems(Iterator<String> iterator) {
        super.addItems(new IteratorMapper<>(iterator,
                s -> s.length() > 1 ? s.trim() : s)); //trim spaces from words
        //but not from single characters
    }
}