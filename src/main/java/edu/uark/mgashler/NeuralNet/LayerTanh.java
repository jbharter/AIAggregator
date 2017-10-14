package edu.uark.mgashler.NeuralNet;


import java.util.List;
import java.util.Random;

public class LayerTanh extends Layer {

    /// General-purpose constructor
    public LayerTanh(int nodes) {
        super(nodes);
    }

    /// Copy constructor
    public LayerTanh(LayerTanh that) {
        super(that);
    }

    /// Unmarshal from a JSON DOM
//    public LayerTanh(Json n) {
//        super(n);
//    }

    public LayerTanh clone() {
        return new LayerTanh(this);
    }

    /// Marshal into a JSON DOM
//    public Json marshal() {
//        Json ob = Json.newObject();
//        ob.add("type", LayerTypes.TANH.ordinal());
//        ob.add("units", (long)outputCount()); // required in all layers
//        return ob;
//    }

    public void initWeights(Random r) {

    }

    public List<Double> forwardProp(List<Double> in) {
        if(in.size() != outputCount()) {
            //throw new IllegalArgumentException("size mismatch. " + Integer.toString(in.size()) + " != " + Integer.toString(outputCount()));
        }
        for(int i = 0; i < activation.size(); i++) {
            activation.set(i,Math.tanh(in.get(i)));
        }
        return activation;
    }

    public void backProp(Layer upStream) {
        if(upStream.outputCount() != outputCount()) {
            throw new IllegalArgumentException("size mismatch");
        }
        for(int i = 0; i < activation.size(); i++) {
            upStream.error.set(i,error.get(i) * (1.0 - activation.get(i) * activation.get(i)));
        }
    }

    public void scaleGradient(double momentum) {

    }

    public void updateGradient(List<Double> in) {

    }

    @Override
    public void step(double stepSize) {

    }

//    void copy(LayerTanh src) {
//    }

//    public int type() {
//        return t_tanh;
//    }

//    public int inputCount() {
//        return activation.length;
//    }
//    public void step(double stepSize) {
//
//    }

    // Applies both L2 and L1 regularization to the weights and biasArray values
//    void regularizeWeights(double lambda) {
//
//    }
    //    public int countWeights() {
//        return 0;
//    }

//    public int setWeights(double[] w, int start) {
//        if(w.length != 0) {
//            throw new IllegalArgumentException("size mismatch");
//        }
//        return 0;
//    }

}