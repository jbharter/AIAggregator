package edu.uark.mgashler.NeuralNet;

import java.util.ArrayList;
import java.util.List;

abstract class Layer implements LayerInterface {
    private ArrayList<Double> activation;
    private ArrayList<Double> error;
    private LayerTypes layerType;

    private int numOutputs;
    private int numInputs;
    private int numError;

    public List<Double> getActivationArray() {
        return activation;
    }

    public List<Double> getErrorArray() {
        return error;
    }

    protected int getNumError() {
        return numError;
    }

    protected int getNumOutputs() {
        return numOutputs;
    }

    protected int getNumInputs() {
        return numInputs;
    }

    Layer(){}

    Layer(LayerTypes layerType, int nodes) {
        this(layerType,nodes,nodes);
    }

    Layer(LayerTypes layerType, int outputs, int numInputs) {
        this.layerType = layerType;
        this.numOutputs = outputs;
        this.numInputs = numInputs;
        this.numError = numInputs;
        activation = new ArrayList<>(outputs);
        error = new ArrayList<>(outputs);
    }

    /// Copy constructor
//    Layer(Layer that) {
//        this.numOutputs = that.numOutputs;
//        this.layerType = that.layerType;
//        activation = new ArrayList<>(that.activation);
//        error = new ArrayList<>(that.error);
//    }

//    LayerTypes getLayerType() {
//        return this.layerType;
//    }

    /// Unmarshal from a JSON DOM
//    Layer(Json n) {
//        int units = (int)n.getLong("units");
//        activation = new double[units];
//        error = new double[units];
//    }

    void computeError(List<Double> target) {
        if(target.size() != activation.size()) {
            //throw new IllegalArgumentException("size mismatch. " + target.size() + " != " + activation.size());
        }
        for(int i = 0; i < activation.size(); i++) {
            error.add(i,target.get(i) - activation.get(i));
        }
    }

    int outputCount() {
        return this.numOutputs;
    }

//    static Layer unmarshal(Json n) {
//        int t = (int)n.getLong("type");
//        switch(t) {
//            case 0: {
//                return new LayerLinear(n);
//            }
//            case 1: {
//                return new LayerTanh(n);
//            }
//            default: {
//                throw new RuntimeException("Unrecognized type");
//            }
//        }
//    }
}
