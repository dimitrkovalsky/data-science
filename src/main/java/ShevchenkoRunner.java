import generation.LstmGenerator;

/**
 * @author dkovalskyi
 * @since 26.05.2017
 */
public class ShevchenkoRunner {
    public static void main(String[] args) throws Exception {
        LstmGenerator generator = new LstmGenerator(
            "E:\\github\\data-science\\text\\kobzar-clean.txt",
            "shevchenkoModel",
            "shevchenko-samples.txt", 3000);
        generator.run();
    }
}
