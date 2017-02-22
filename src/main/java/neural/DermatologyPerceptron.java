package neural;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.FileReader;

import static java.lang.System.out;

/**
 * @author Dmytro_Kovalskyi.
 * @since 22.02.2017.
 */
public class DermatologyPerceptron {
    private Instances trainingInstances;
    private Instances testingInstances;
    private MultilayerPerceptron mlp;

    public MultilayerPerceptron getMlp() {
        return mlp;
    }

    public void train() {
        String trainingFileName = "D:\\github\\data-science\\datasets\\dermatologyTraining.arff";
        String testingFileName = "D:\\github\\data-science\\datasets\\dermatologyTesting.arff";
        try (FileReader trainingReader = new FileReader(trainingFileName);
             FileReader testingReader = new FileReader(testingFileName)) {
            trainingInstances = new Instances(trainingReader);
            trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
            testingInstances = new Instances(testingReader);
            testingInstances.setClassIndex(testingInstances.numAttributes() - 1);
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
    }

    private Evaluation evaluate() throws Exception {
        Evaluation evaluation = new Evaluation(trainingInstances);
        evaluation.evaluateModel(mlp, testingInstances);
        out.println(evaluation.toSummaryString());
        return evaluation;
    }

    private void configure() throws Exception {
        mlp = new MultilayerPerceptron();
        mlp.setLearningRate(0.1);
        mlp.setMomentum(0.2);
        mlp.setTrainingTime(2000);
        mlp.setHiddenLayers("3");
        mlp.buildClassifier(trainingInstances);
    }

    public void findError() throws Exception {
        for (int i = 0; i < testingInstances.numInstances(); i++) {
            double result = mlp.classifyInstance(testingInstances.instance(i));
            if (result != testingInstances.instance(i).value(testingInstances.numAttributes() - 1)) {
                out.println("Classify result: " + result + " Correct: " + testingInstances.instance(i)
                        .value(testingInstances.numAttributes() - 1));
                Instance incorrectInstance = testingInstances.instance(i);
                incorrectInstance.setDataset(trainingInstances);
                double[] distribution = mlp.distributionForInstance(incorrectInstance);
                out.println("Probability of being positive: " + distribution[0]);
                out.println("Probability of being negative: " + distribution[1]);

            }
        }

    }

    public static void main(String[] args) throws Exception {
        DermatologyPerceptron perceptron = new DermatologyPerceptron();
        perceptron.train();
        perceptron.configure();
        perceptron.evaluate();
        perceptron.findError();
        SerializationHelper.write("mlpModel", perceptron.mlp);

    }
}
