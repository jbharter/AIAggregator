package edu.uark.mgashler.NeuralNet;

import edu.uark.mgashler.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class LayerLinear extends Layer {
    private Matrix weights; // numRows are inputs, numCols are outputs
    private Matrix weightsGradient;
    private List<Double> biasArray;
    private List<Double> biasGradientArray;

    public Matrix getWeights() {
        return weights;
    }

    public Matrix getWeightsGradient() {
        return weightsGradient;
    }

    public List<Double> getBiasArray() {
        return biasArray;
    }

    public List<Double> getBiasGradientArray() {
        return this.biasGradientArray;
    }

    /// General-purpose constructor
    public LayerLinear(int inputs, int outputs) {
        super(LayerTypes.LINEAR,outputs,inputs);
        weights = new Matrix();
        weights.setSize(inputs, outputs);
        weightsGradient = new Matrix();
        weightsGradient.setSize(inputs, outputs);
        biasArray = new ArrayList<>(outputs);
        biasGradientArray = new ArrayList<>(outputs);
    }

    /// Copy constructor
//    public LayerLinear(LayerLinear that) {
//        super(that);
//        weights = new Matrix(that.weights);
//        weightsGradient = new Matrix(that.weightsGradient);
//        biasArray = new ArrayList<>(that.biasArray);
//        biasGradientArray = new ArrayList<>(that.biasGradientArray);
//        weightsGradient = new Matrix();
//        weightsGradient.setSize(weights.numRows(), weights.numCols());
//        weightsGradient.setAll(0.0);
//        biasGradientArray = DoubleStream.of(0.0).limit(weights.numCols()).boxed().collect(Collectors.toList());
//    }

    /// Unmarshal from a JSON DOM
//    public LayerLinear(Json n) {
//        super(n);
//        weights = new Matrix(n.get("weights"));
//        biasArray = Vec.unmarshal(n.get("biasArray"));
//        weightsGradient = new Matrix(weights.numRows(), weights.numCols());
//        biasGradientArray = new double[biasArray.length];
//    }

    /// Marshal into a JSON DOM
//    public Json marshal() {
//        Json ob = Json.newObject();
//        ob.add("type", LayerTypes.LINEAR.ordinal());
//        ob.add("units", (long)outputCount()); // required in all layers
//        ob.add("weights", weights.marshal());
//        ob.add("biasArray", Vec.marshal(biasArray));
//        return ob;
//    }

    public void initWeights(Random r) {
        double dev = Math.max(0.3, 1.0 / weights.numRows());
        for(int i = 0; i < weights.numRows(); i++) {
            for(int j = 0; j < weights.numCols(); j++) {
                weights.set(i,j,dev * r.nextGaussian());
            }
        }
        for(int j = 0; j < weights.numCols(); j++) {
            biasArray.add(dev * r.nextGaussian());
        }
        weightsGradient.reset.accept(0.0);
        biasGradientArray = DoubleStream.of(0.0).limit(weights.numCols()).boxed().collect(Collectors.toList());
    }

    private Double getBias(int index) {
        if (biasArray.size() > index) {
            return biasArray.get(index);
        } else {
            return -1D;
        }
    }

//    double[] forwardProp(double[] in)
//    {
//        if(in.length != weights.numRows())
//            throw new IllegalArgumentException("size mismatch. " + Integer.toString(in.length) + " != " + Integer.toString(weights.numRows()));
//        for(int i = 0; i < activation.length; i++)
//            activation[i] = bias[i];
//        for(int j = 0; j < weights.numRows(); j++)
//        {
//            double v = in[j];
//            double[] w = weights.row(j);
//            for(int i = 0; i < weights.numCols(); i++) {
//                if (activation.size()==0) {
//                    activation.add(biasArray.get(i));
//                } else {
//                    activation.add(activation.get(i) + (v * w[i]));
//                }
//            }
//        }
//        return activation;
//    }

    public List<Double> forwardProp(List<Double> in) {
        if(in.size() != getWeights().getMatrix().size()){ //getWeights().numRows()) {
            throw new IllegalArgumentException("size mismatch. " + Integer.toString(in.size()) + " != " + Integer.toString(weights.numRows()));
        }
        for(int i = 0; i < getNumInputs(); i++) {
            getActivationArray().add(getBias(i));
        }
        for(int j = 0; j < getWeights().getMatrix().size(); j++) {
            double v = in.get(j);
            List<Double> w = new ArrayList<>(weights.row(j).values());
            for(int i = 0; i < weights.numCols(); i++) {
                if (getActivationArray().size()==0) {
                    getActivationArray().add(getBias(i));
                } else if (i == 0) {
                    getActivationArray().add(getActivationArray().get(i) + (v * w.get(i)));
                } else {
                    getActivationArray().add(getActivationArray().get(i-1) + (v * w.get(i)));
                }
            }
        }
        return getActivationArray();
    }

    public void backProp(Layer upStream) {
        if(upStream.outputCount() != weights.numRows())
            throw new IllegalArgumentException("size mismatch");
        for(int j = 0; j < weights.numRows(); j++) {
            List<Double> w = new ArrayList<>(weights.row(j).values());
            double d = 0.0;
            for(int i = 0; i < weights.numCols(); i++) {
                d = d + getErrorArray().get(i) * w.get(i);
            }

            if (upStream.getErrorArray().size() > j) {
                upStream.getErrorArray().set(j, d);
            } else {
                upStream.getErrorArray().add(d);
            }
        }
    }

    public void scaleGradient(double momentum) {
        weightsGradient.scale.accept(momentum);
        for(int i = 0; i < biasGradientArray.size(); i++) {
            biasGradientArray.set(i,biasGradientArray.get(i) * momentum);
        }
    }

    public void updateGradient(List<Double> in) {
        for(int i = 0; i < biasArray.size(); i++) {
            if (biasGradientArray.size() > i) {
                biasGradientArray.set(i,biasGradientArray.get(i)+getErrorArray().get(i));
            } else {
                biasGradientArray.add(getErrorArray().get(i));
            }
        }
        for(int j = 0; j < weights.numRows(); j++) {
            double x = in.get(j);
            for(int i = 0; i < weights.numCols(); i++) {
                weightsGradient.row(j).compute(i,(k,v) -> {
                    if (v == null) {
                        return (x * getErrorArray().get(k));
                    } else {
                        return v + (x * getErrorArray().get(k));
                    }
                });
            }
        }
    }

    public void step(double stepSize) {
        weights.addScaled(weightsGradient, stepSize);
        if(biasArray.size() != biasGradientArray.size()) {
            throw new IllegalArgumentException("mismatching sizes");
        }
        for(int i = 0; i < biasArray.size(); i++) {
            biasArray.set(i, biasArray.get(i) + (stepSize * biasGradientArray.get(i)));
        }
    }

    // Applies both L2 and L1 regularization to the weights and biasArray values
//    void regularizeWeights(double lambda) {
//        for(int i = 0; i < weights.numRows(); i++) {
//            double[] row = weights.row(i);
//            for(int j = 0; j < row.length; j++) {
//                row[j] *= (1.0 - lambda);
//                if(row[j] < 0.0) {
//                    row[j] += lambda;
//                } else {
//                    row[j] -= lambda;
//                }
//            }
//        }
//        for(int j = 0; j < biasArray.length; j++) {
//            biasArray[j] *= (1.0 - lambda);
//            if(biasArray[j] < 0.0) {
//                biasArray[j] += lambda;
//            } else {
//                biasArray[j] -= lambda;
//            }
//        }
//    }

//    void copy(LayerLinear src) {
//        if(src.weights.numRows() != weights.numRows() || src.weights.numCols() != weights.numCols()) {
//            throw new IllegalArgumentException("mismatching sizes");
//        }
//        weights.copyBlock(0, 0, src.weights, 0, 0, src.weights.numRows(), src.weights.numCols());
//        for(int i = 0; i < biasArray.length; i++) {
//            biasArray[i] = src.biasArray[i];
//        }
//    }
//    double[] forwardProp2(double[] in1, double[] in2) {
//        if(in1.length + in2.length != weights.numRows())
//            throw new IllegalArgumentException("size mismatch. " + Integer.toString(in1.length) + " + " + Integer.toString(in2.length) + " != " + Integer.toString(weights.numRows()));
//        for(int i = 0; i < activation.length; i++)
//            activation[i] = biasArray[i];
//        for(int j = 0; j < in1.length; j++)
//        {
//            double v = in1[j];
//            double[] w = weights.row(j);
//            for(int i = 0; i < weights.numCols(); i++)
//                activation[i] += v * w[i];
//        }
//        for(int j = 0; j < in2.length; j++)
//        {
//            double v = in2[j];
//            double[] w = weights.row(in1.length + j);
//            for(int i = 0; i < weights.numCols(); i++)
//                activation[i] += v * w[i];
//        }
//        return activation;
//    }
//    void refineInputs(double[] inputs, double learningRate) {
//        if(inputs.length != weights.numRows())
//            throw new IllegalArgumentException("size mismatch");
//        for(int j = 0; j < weights.numRows(); j++) {
//            double[] w = weights.row(j);
//            double d = 0.0;
//            for(int i = 0; i < weights.numCols(); i++)
//            {
//                d += error[i] * w[i];
//            }
//            inputs[j] += learningRate * d;
//        }
//    }
    //    int type() {
//        return t_linear;
//    }

//    int inputCount() {
//        return weights.numRows();
//    }

//    int countWeights() {
//        return weights.numRows() * weights.numCols() + biasArray.length;
//    }

//    int setWeights(double[] w, int start) {
//        int oldStart = start;
//        for(int i = 0; i < biasArray.length; i++) {
//            biasArray[i] = w[start++];
//        }
//        for(int i = 0; i < weights.numRows(); i++) {
//            double[] row = weights.row(i);
//            for(int j = 0; j < weights.numCols(); j++) {
//                row[j] = w[start++];
//            }
//        }
//        return start - oldStart;
//    }

}
