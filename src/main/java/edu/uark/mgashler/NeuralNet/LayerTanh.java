package edu.uark.mgashler.NeuralNet;

import edu.uark.mgashler.Json;

import java.util.Random;

public class LayerTanh extends Layer
{
    /// General-purpose constructor
    public LayerTanh(int nodes)
    {
        super(nodes);
    }


    /// Copy constructor
    public LayerTanh(LayerTanh that)
    {
        super(that);
    }


    /// Unmarshal from a JSON DOM
    public LayerTanh(Json n)
    {
        super(n);
    }


    protected LayerTanh clone()
    {
        return new LayerTanh(this);
    }


    /// Marshal into a JSON DOM
    Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("type", t_tanh);
        ob.add("units", (long)outputCount()); // required in all layers
        return ob;
    }


    void copy(LayerTanh src)
    {
    }


    int type() { return t_tanh; }
    int inputCount() { return activation.length; }


    void initWeights(Random r)
    {
    }


    int countWeights()
    {
        return 0;
    }


    int setWeights(double[] w, int start)
    {
        if(w.length != 0)
            throw new IllegalArgumentException("size mismatch");
        return 0;
    }


    double[] forwardProp(double[] in)
    {
        if(in.length != outputCount())
            throw new IllegalArgumentException("size mismatch. " + Integer.toString(in.length) + " != " + Integer.toString(outputCount()));
        for(int i = 0; i < activation.length; i++)
        {
            activation[i] = Math.tanh(in[i]);
        }
        return activation;
    }


    void backProp(Layer upStream)
    {
        if(upStream.outputCount() != outputCount())
            throw new IllegalArgumentException("size mismatch");
        for(int i = 0; i < activation.length; i++)
        {
            upStream.error[i] = error[i] * (1.0 - activation[i] * activation[i]);
        }
    }


    void scaleGradient(double momentum)
    {
    }


    void updateGradient(double[] in)
    {
    }


    void step(double stepSize)
    {
    }


    // Applies both L2 and L1 regularization to the weights and bias values
    void regularizeWeights(double lambda)
    {
    }
}