import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Dmytro_Kovalskyi.
 * @since 26.01.2017.
 */
public class DecisionTree {
    private Instances trainingData;

    public static void main(String[] args) throws Exception {
        DecisionTree decisionTree = new DecisionTree();
        decisionTree.init();
        J48 tree = decisionTree.performTraining();

        Instance testInstance = decisionTree.getTestInstance("sunny", 80, 90, "TRUE");
        int instance = (int) tree.classifyInstance(testInstance);
        String value = decisionTree.trainingData.attribute(4).value(instance);
        System.out.println(testInstance + " => " + value);

        decisionTree.display(tree);
    }


    public void display(J48 tree) throws Exception {
        // display classifier
        final javax.swing.JFrame jf =
                new javax.swing.JFrame("Weka Classifier Tree Visualizer: J48");
        jf.setSize(500,400);
        jf.getContentPane().setLayout(new BorderLayout());
        TreeVisualizer tv = new TreeVisualizer(null,
                tree.graph(),
                new PlaceNode2());

        jf.getContentPane().add(tv, BorderLayout.CENTER);
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });

        jf.setVisible(true);
        tv.fitToScreen();

    }

    public void init() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    "D:\\github\\data-science\\datasets\\weather.arff"));
            trainingData = new Instances(reader);
            trainingData.setClassIndex(trainingData.numAttributes() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private J48 performTraining() {
        J48 j48 = new J48();
        try {
            j48.setOptions(new String[]{"-U"});
            j48.buildClassifier(trainingData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j48;
    }

    private Instance getTestInstance(String outlook, int temperature, int humidity, String windy) {
        Instance instance = new DenseInstance(4);
        instance.setDataset(trainingData);
        instance.setValue(trainingData.attribute(0), outlook);
        instance.setValue(trainingData.attribute(1), temperature);
        instance.setValue(trainingData.attribute(2), humidity);
        instance.setValue(trainingData.attribute(3), windy);
        return instance;
    }
}
