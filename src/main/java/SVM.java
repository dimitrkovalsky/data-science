import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Dmytro_Kovalskyi.
 * @since 27.01.2017.
 */
public class SVM {
    public static final String FILE_NAME = "D:\\github\\data-science\\datasets\\camping.arff";
    private Instances data;

    public static void main(String[] args) throws Exception {
        SVM svm = new SVM();
        svm.init();
        svm.train();
    }

    public void train() throws Exception {
        Instances trainingData = new Instances(data,0, 14);
        Instances testingData = new Instances(data, 14, 5);

        Evaluation evaluation = new Evaluation(trainingData);
        Classifier smo = new SMO();
        smo.buildClassifier(data);

        evaluation.evaluateModel(smo, testingData);
        System.out.println(evaluation.toSummaryString());
    }

    public void init() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            data = new Instances(reader);
            data.setClassIndex(data.numAttributes() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
