package edu.uark.mgashler.NeuralNet;


import java.util.List;
import java.util.Random;

public class LayerTanh extends Layer {

    /// General-purpose constructor
    public LayerTanh(int nodes) {
        super(LayerTypes.TANH,nodes);
    }

    /// Copy constructor
//    public LayerTanh(LayerTanh that) {
//        super(that);
//    }

    /// Unmarshal from a JSON DOM
//    public LayerTanh(Json n) {
//        super(n);
//    }

    /// Marshal into a JSON DOM
//    public Json marshal() {
//        Json ob = Json.newObject();
//        ob.add("type", LayerTypes.TANH.ordinal());
//        ob.add("units", (long)outputCount()); // required in all layers
//        return ob;
//    }

    public void initWeights(Random r) {
//        double dev = Math.max(0.3, 1.0 / weights.numRows());
//        for(int i = 0; i < weights.numRows(); i++) {
//            List<Double> row = weights.row(i);
//            for(int j = 0; j < weights.numCols(); j++) {
//                row.add(j,dev * r.nextGaussian());
//            }
//        }
//        for(int j = 0; j < weights.numCols(); j++) {
//            biasArray.add(dev * r.nextGaussian());
//        }
//        weightsGradient.setAll(0.0);
//        biasGradientArray = DoubleStream.of(0.0).limit(weights.numCols()).boxed().collect(Collectors.toList());
    }

    public List<Double> forwardProp(List<Double> in) {
        if(in.size() != outputCount()) {
            //throw new IllegalArgumentException("size mismatch. " + Integer.toString(in.size()) + " != " + Integer.toString(outputCount()));
        }
        for(int i = 0; i < outputCount(); i++) {
            if (getActivationArray().size() > i) {
                getActivationArray().set(i, Math.tanh(in.get(i)));
            } else {
                getActivationArray().add(Math.tanh(in.get(i)));
            }
        }
        return getActivationArray();
    }

    public void backProp(Layer upStream) {
        if(upStream.outputCount() != outputCount()) {
            throw new IllegalArgumentException("size mismatch");
        }
        for(int i = 0; i < getActivationArray().size(); i++) {
            Double val = (1.0 - getActivationArray().get(i) * getActivationArray().get(i));
            if (upStream.getErrorArray().size() > i) {
                val *= upStream.getErrorArray().get(i);
                upStream.getErrorArray().set(i,val);
            } else {
                upStream.getErrorArray().add(val);
            }
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