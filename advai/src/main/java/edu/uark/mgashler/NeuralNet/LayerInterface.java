package edu.uark.mgashler.NeuralNet;

import java.util.List;
import java.util.Random;

public interface LayerInterface {

    enum LayerTypes {
        LINEAR,
        TANH
    }


    //Json marshal();
    //int type();
    //int inputCount();
    void initWeights(Random r);
    List<Double> forwardProp(List<Double> in);
    void backProp(Layer upStream);
    void scaleGradient(double momentum);
    void updateGradient(List<Double> in);
    void step(double stepSize);
    //int countWeights();
    //int setWeights(double[] w, int start);
    //void regularizeWeights(double lambda);
}
