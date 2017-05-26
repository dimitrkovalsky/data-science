package generation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
public class MarksGenerator {

    public static final String SAMPLES_TXT = "samples.txt";
    public static final String MODEL_FILE_PATH = "marksModel";
    private static int lstmLayerSize = 200;
    private static int miniBatchSize = 32;
    private static int examplesPerEpoch = 50 * miniBatchSize;
    private static int exampleLength = 100;
    private static int numEpochs = 20;
    private static int nSamplesToGenerate = 4;
    private static int nCharactersToSample = 300;

    public static final String FILE_PATH = "C:\\github\\data-science\\text\\kapital-clean.txt";

    public static void main(String[] args) throws Exception {
        MultiLayerNetwork model;
        String generationInitialization = null;
        Random rng = new Random(12345);
        CharacterIterator iter = getMarksIterator(miniBatchSize, exampleLength, examplesPerEpoch);
        int nOut = iter.totalOutcomes();
        File marksModel = new File("marksModel");
        if (marksModel.exists()) {
            System.out.println("Model exist in saved file. Restoring...");
            model = ModelSerializer.restoreMultiLayerNetwork(marksModel);
        } else {
            System.out.println("Creating new network");
            model = createNetwork(lstmLayerSize, iter, nOut);
        }

        calculateParameters(model);
        for (int i = 0; i < numEpochs; i++) {
            model.fit(iter);
            System.out.println("---------------------");
            System.out.println("Completed epoch " + i);
            System.out.println("Sampling characters from network given initialization \"" + (generationInitialization == null ? "" : generationInitialization) + "\"");
            String[] samples = sampleCharactersFromNetwork(generationInitialization, model, iter, rng, nCharactersToSample, nSamplesToGenerate);
            for (int j = 0; j < samples.length; j++) {
                System.out.println("----- Sample " + j + " -----");
                System.out.println(samples[j]);
                System.out.println();
            }
            saveToFile(i, samples);
            saveModel(model);

            iter.reset();
        }


        saveModel(model);

        System.out.println("\n\nExample complete");
    }

    private static void saveModel(MultiLayerNetwork model) throws IOException {
        File modelFile = new File(MODEL_FILE_PATH);
        ModelSerializer.writeModel(model, modelFile, true);
    }

    private static void saveToFile(int epoch, String[] samples) throws IOException {
        File file = new File(SAMPLES_TXT);
        List<String> list = new ArrayList<>();
        list.add("Completed epoch " + epoch);
        list.addAll(Arrays.asList(samples));
        FileUtils.writeLines(file, list, true);
    }

    private static void calculateParameters(MultiLayerNetwork net) throws IOException {
        Layer[] layers = net.getLayers();
        int totalNumParams = 0;
        for (int i = 0; i < layers.length; i++) {
            int nParams = layers[i].numParams();
            System.out.println("Number of parameters in layer " + i + ": " + nParams);
            totalNumParams += nParams;
        }
        System.out.println("Total number of network parameters: " + totalNumParams);
        ModelSerializer.restoreMultiLayerNetwork(new File("marksModel"));
    }

    private static MultiLayerNetwork createNetwork(int lstmLayerSize, CharacterIterator iter, int nOut) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
            .learningRate(0.1)
            .rmsDecay(0.95)
            .seed(12345)
            .regularization(true)
            .l2(0.001)
            .list()
            .layer(0, new GravesLSTM.Builder().nIn(iter.inputColumns()).nOut(lstmLayerSize)
                .updater(Updater.RMSPROP)
                .activation("tanh").weightInit(WeightInit.DISTRIBUTION)
                .dist(new UniformDistribution(-0.08, 0.08)).build())
            .layer(1, new GravesLSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
                .updater(Updater.RMSPROP)
                .activation("tanh").weightInit(WeightInit.DISTRIBUTION)
                .dist(new UniformDistribution(-0.08, 0.08)).build())
            .layer(2, new RnnOutputLayer.Builder(LossFunction.MCXENT).activation("softmax")        //MCXENT + softmax for classification
                .updater(Updater.RMSPROP)
                .nIn(lstmLayerSize).nOut(nOut).weightInit(WeightInit.DISTRIBUTION)
                .dist(new UniformDistribution(-0.08, 0.08)).build())
            .pretrain(false).backprop(true)
            .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        return net;
    }

    private static CharacterIterator getMarksIterator(int miniBatchSize, int exampleLength, int examplesPerEpoch) throws Exception {

        char[] validCharacters = CharacterIterator.getRussianCharacterSet();
        return new CharacterIterator(FILE_PATH, Charset.forName("UTF-8"),
            miniBatchSize, exampleLength, examplesPerEpoch, validCharacters, new Random(12345), true);
    }

    /**
     * Generate a sample from the network, given an (optional, possibly null) initialization. Initialization
     * can be used to 'prime' the RNN with a sequence you want to extend/continue.<br>
     * Note that the initalization is used for all samples.txt
     *
     * @param initialization     String, may be null. If null, select a random character as initialization for all samples.txt
     * @param charactersToSample Number of characters to sample from network (excluding initialization)
     * @param net                MultiLayerNetwork with one or more GravesLSTM/RNN layers and a softmax output layer
     * @param iter               CharacterIterator. Used for going from indexes back to characters
     */
    private static String[] sampleCharactersFromNetwork(String initialization, MultiLayerNetwork net,
                                                        CharacterIterator iter, Random rng, int charactersToSample, int numSamples) {
        //Set up initialization. If no initialization: use a random character
        if (initialization == null) {
            initialization = String.valueOf(iter.getRandomCharacter());
        }

        //Create input for initialization
        INDArray initializationInput = Nd4j.zeros(numSamples, iter.inputColumns(), initialization.length());
        char[] init = initialization.toCharArray();
        for (int i = 0; i < init.length; i++) {
            int idx = iter.convertCharacterToIndex(init[i]);
            for (int j = 0; j < numSamples; j++) {
                initializationInput.putScalar(new int[] {j, idx, i}, 1.0f);
            }
        }

        StringBuilder[] sb = new StringBuilder[numSamples];
        for (int i = 0; i < numSamples; i++) {
            sb[i] = new StringBuilder(initialization);
        }

        //Sample from network (and feed samples.txt back into input) one character at a time (for all samples.txt)
        //Sampling is done in parallel here
        net.rnnClearPreviousState();
        INDArray output = net.rnnTimeStep(initializationInput);
        output = output.tensorAlongDimension(output.size(2) - 1, 1, 0);    //Gets the last time step output

        for (int i = 0; i < charactersToSample; i++) {
            //Set up next input (single time step) by sampling from previous output
            INDArray nextInput = Nd4j.zeros(numSamples, iter.inputColumns());
            //Output is a probability distribution. Sample from this for each example we want to generate, and add it to the new input
            for (int s = 0; s < numSamples; s++) {
                double[] outputProbDistribution = new double[iter.totalOutcomes()];
                for (int j = 0; j < outputProbDistribution.length; j++) {
                    outputProbDistribution[j] = output.getDouble(s, j);
                }
                int sampledCharacterIdx = sampleFromDistribution(outputProbDistribution, rng);

                nextInput.putScalar(new int[] {s, sampledCharacterIdx}, 1.0f);        //Prepare next time step input
                sb[s].append(iter.convertIndexToCharacter(sampledCharacterIdx));    //Add sampled character to StringBuilder (human readable output)
            }

            output = net.rnnTimeStep(nextInput);    //Do one time step of forward pass
        }

        String[] out = new String[numSamples];
        for (int i = 0; i < numSamples; i++) {
            out[i] = sb[i].toString();
        }
        return out;
    }

    /**
     * Given a probability distribution over discrete classes, sample from the distribution
     * and return the generated class index.
     *
     * @param distribution Probability distribution over classes. Must sum to 1.0
     */
    private static int sampleFromDistribution(double[] distribution, Random rng) {
        double d = rng.nextDouble();
        double sum = 0.0;
        for (int i = 0; i < distribution.length; i++) {
            sum += distribution[i];
            if (d <= sum) {
                return i;
            }
        }
        //Should never happen if distribution is a valid probability distribution
        throw new IllegalArgumentException("Distribution is invalid? d=" + d + ", sum=" + sum);
    }
}