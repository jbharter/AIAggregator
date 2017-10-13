package edu.uark.mgashler.NeuralNet;

import edu.uark.mgashler.Json;
import edu.uark.mgashler.Vec;

import java.util.Random;

abstract class Layer
{
    double[] activation;
    double[] error;

    static final int t_linear = 0;
    static final int t_tanh = 1;


    /// General-purpose constructor
    Layer(int outputs)
    {
        activation = new double[outputs];
        error = new double[outputs];
    }


    /// Copy constructor
    Layer(Layer that)
    {
        activation = Vec.copy(that.activation);
        error = Vec.copy(that.error);
    }


    /// Unmarshal from a JSON DOM
    Layer(Json n)
    {
        int units = (int)n.getLong("units");
        activation = new double[units];
        error = new double[units];
    }


    void computeError(double[] target)
    {
        if(target.length != activation.length)
            throw new IllegalArgumentException("size mismatch. " + Integer.toString(target.length) + " != " + Integer.toString(activation.length));
        for(int i = 0; i < activation.length; i++)
        {
            error[i] = target[i] - activation[i];
        }
    }


    int outputCount()
    {
        return activation.length;
    }


    static Layer unmarshal(Json n)
    {
        int t = (int)n.getLong("type");
        switch(t)
        {
            case t_linear: return new LayerLinear(n);
            case t_tanh: return new LayerTanh(n);
            default: throw new RuntimeException("Unrecognized type");
        }
    }


    protected abstract Layer clone();
    abstract Json marshal();
    abstract int type();
    abstract int inputCount();
    abstract void initWeights(Random r);
    abstract double[] forwardProp(double[] in);
    abstract void backProp(Layer upStream);
    abstract void scaleGradient(double momentum);
    abstract void updateGradient(double[] in);
    abstract void step(double stepSize);
    abstract int countWeights();
    abstract int setWeights(double[] w, int start);
    abstract void regularizeWeights(double lambda);
}
