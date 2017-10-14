package edu.uark.mgashler.NeuralNet;

import java.util.ArrayList;
import java.util.List;

abstract class Layer implements LayerInterface {
    ArrayList<Double> activation;
    ArrayList<Double> error;

    /// General-purpose constructor
    Layer(int outputs) {
        activation = new ArrayList<>(outputs);
        error = new ArrayList<>(outputs);
    }

    /// Copy constructor
    Layer(Layer that) {
        activation = new ArrayList<>(that.activation);
        error = new ArrayList<>(that.error);
    }

    /// Unmarshal from a JSON DOM
//    Layer(Json n) {
//        int units = (int)n.getLong("units");
//        activation = new double[units];
//        error = new double[units];
//    }

    protected abstract Layer clone();

    void computeError(List<Double> target) {
        if(target.size() != activation.size()) {
            throw new IllegalArgumentException("size mismatch. " + target.size() + " != " + activation.size());
        }
        for(int i = 0; i < activation.size(); i++) {
            error.add(i,target.get(i) - activation.get(i));
        }
    }

    int outputCount() {
        return activation.size();
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
