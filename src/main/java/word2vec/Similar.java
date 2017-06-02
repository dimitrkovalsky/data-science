package word2vec;

import lombok.extern.slf4j.Slf4j;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * @author dkovalskyi
 * @since 02.06.2017
 */
@Slf4j
public class Similar {
    public static void main(String[] args) throws Exception {
        String filePath = "C:\\github\\data-science\\text\\sonets.txt";

        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line

        log.info("Load data....");
        SentenceIterator iter = new BasicLineIterator(filePath);
        iter.setPreProcessor((SentencePreProcessor) String::toLowerCase);

        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
            .minWordFrequency(5)
            .iterations(10)
            .layerSize(100)
            .seed(42)
            .windowSize(5)
            .iterate(iter)
            .tokenizerFactory(t)
            .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        // Write word vectors
        WordVectorSerializer.writeWordVectors(vec, "pathToWriteto.txt");

        log.info("Closest Words:");
        Collection<String> lst = vec.wordsNearest("day", 10);
        System.out.println(lst);

        double cosSim = vec.similarity("day", "night");
        System.out.println(cosSim);

        Collection<String> lst3 = vec.wordsNearest("man", 10);
        System.out.println(lst3);
        //output: [director, company, program, former, university, family, group, such, general]
//        UiServer server = UiServer.getInstance();
//        System.out.println("Started on port " + server.getPort());

        //output: [night, week, year, game, season, during, office, until, -]
    }
}
