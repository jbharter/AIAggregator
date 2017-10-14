package edu.uark.mgashler.NeuralNet;

import edu.uark.mgashler.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class LayerLinear extends Layer {
    Matrix weights; // rows are inputs, cols are outputs
    Matrix weightsGradient;
    List<Double> biasArray;
    List<Double> biasGradientArray;

    /// General-purpose constructor
    public LayerLinear(int inputs, int outputs) {
        super(outputs);
        weights = new Matrix();
        weights.setSize(inputs, outputs);
        weightsGradient = new Matrix();
        weightsGradient.setSize(inputs, outputs);
        biasArray = new ArrayList<>(outputs);
        biasGradientArray = new ArrayList<>(outputs);
    }

    /// Copy constructor
    public LayerLinear(LayerLinear that) {
        super(that);
        weights = new Matrix(that.weights);
        weightsGradient = new Matrix(that.weightsGradient);
        biasArray = new ArrayList<>(that.biasArray);
        biasGradientArray = new ArrayList<>(that.biasGradientArray);
        weightsGradient = new Matrix();
        weightsGradient.setSize(weights.rows(), weights.cols());
        weightsGradient.setAll(0.0);
        biasGradientArray = DoubleStream.of(0.0).limit(weights.cols()).boxed().collect(Collectors.toList());
    }

    /// Unmarshal from a JSON DOM
//    public LayerLinear(Json n) {
//        super(n);
//        weights = new Matrix(n.get("weights"));
//        biasArray = Vec.unmarshal(n.get("biasArray"));
//        weightsGradient = new Matrix(weights.rows(), weights.cols());
//        biasGradientArray = new double[biasArray.length];
//    }

    public LayerLinear clone() {
        return new LayerLinear(this);
    }

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
        double dev = Math.max(0.3, 1.0 / weights.rows());
        for(int i = 0; i < weights.rows(); i++) {
            List<Double> row = weights.row(i);
            for(int j = 0; j < weights.cols(); j++) {
                row.add(j,dev * r.nextGaussian());
            }
        }
        for(int j = 0; j < weights.cols(); j++) {
            biasArray.add(dev * r.nextGaussian());
        }
        weightsGradient.setAll(0.0);
        biasGradientArray = DoubleStream.of(0.0).limit(weights.cols()).boxed().collect(Collectors.toList());
    }

//    double[] forwardProp(double[] in)
//    {
//        if(in.length != weights.rows())
//            throw new IllegalArgumentException("size mismatch. " + Integer.toString(in.length) + " != " + Integer.toString(weights.rows()));
//        for(int i = 0; i < activation.length; i++)
//            activation[i] = bias[i];
//        for(int j = 0; j < weights.rows(); j++)
//        {
//            double v = in[j];
//            double[] w = weights.row(j);
//            for(int i = 0; i < weights.cols(); i++) {
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
        if(in.size() != weights.rows()) {
            throw new IllegalArgumentException("size mismatch. " + Integer.toString(in.size()) + " != " + Integer.toString(weights.rows()));
        }
        for(int i = 0; i < activation.size(); i++) {
            activation.add(biasArray.get(i));
        }
        for(int j = 0; j < weights.rows(); j++) {
            double v = in.get(j);
            List<Double> w = weights.row(j);
            for(int i = 0; i < weights.cols(); i++) {
                if (activation.size()==0) {
                    activation.add(biasArray.get(i));
                } else if (i == 0) {
                    activation.add(activation.get(i) + (v * w.get(i)));
                } else {
                    activation.add(activation.get(i-1) + (v * w.get(i)));
                }
            }
        }
        return activation;
    }

    public void backProp(Layer upStream) {
        if(upStream.outputCount() != weights.rows())
            throw new IllegalArgumentException("size mismatch");
        for(int j = 0; j < weights.rows(); j++) {
            List<Double> w = weights.row(j);
            double d = 0.0;
            for(int i = 0; i < weights.cols(); i++) {
                d += error.get(i) * w.get(i);
            }
            upStream.error.set(j,d);
        }
    }

    public void scaleGradient(double momentum) {
        weightsGradient.scale(momentum);
        for(int i = 0; i < biasGradientArray.size(); i++) {
            biasGradientArray.set(i,biasGradientArray.get(i) * momentum);
        }
    }

    public void updateGradient(List<Double> in) {
        for(int i = 0; i < biasArray.size(); i++) {
            biasGradientArray.set(i,biasGradientArray.get(i)+error.get(i));
        }
        for(int j = 0; j < weights.rows(); j++) {
            List<Double> w = weightsGradient.row(j);
            double x = in.get(j);
            for(int i = 0; i < weights.cols(); i++) {
                w.set(i,w.get(i) + (x * error.get(i)));
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
//        for(int i = 0; i < weights.rows(); i++) {
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
//        if(src.weights.rows() != weights.rows() || src.weights.cols() != weights.cols()) {
//            throw new IllegalArgumentException("mismatching sizes");
//        }
//        weights.copyBlock(0, 0, src.weights, 0, 0, src.weights.rows(), src.weights.cols());
//        for(int i = 0; i < biasArray.length; i++) {
//            biasArray[i] = src.biasArray[i];
//        }
//    }
//    double[] forwardProp2(double[] in1, double[] in2) {
//        if(in1.length + in2.length != weights.rows())
//            throw new IllegalArgumentException("size mismatch. " + Integer.toString(in1.length) + " + " + Integer.toString(in2.length) + " != " + Integer.toString(weights.rows()));
//        for(int i = 0; i < activation.length; i++)
//            activation[i] = biasArray[i];
//        for(int j = 0; j < in1.length; j++)
//        {
//            double v = in1[j];
//            double[] w = weights.row(j);
//            for(int i = 0; i < weights.cols(); i++)
//                activation[i] += v * w[i];
//        }
//        for(int j = 0; j < in2.length; j++)
//        {
//            double v = in2[j];
//            double[] w = weights.row(in1.length + j);
//            for(int i = 0; i < weights.cols(); i++)
//                activation[i] += v * w[i];
//        }
//        return activation;
//    }
//    void refineInputs(double[] inputs, double learningRate) {
//        if(inputs.length != weights.rows())
//            throw new IllegalArgumentException("size mismatch");
//        for(int j = 0; j < weights.rows(); j++) {
//            double[] w = weights.row(j);
//            double d = 0.0;
//            for(int i = 0; i < weights.cols(); i++)
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
//        return weights.rows();
//    }

//    int countWeights() {
//        return weights.rows() * weights.cols() + biasArray.length;
//    }

//    int setWeights(double[] w, int start) {
//        int oldStart = start;
//        for(int i = 0; i < biasArray.length; i++) {
//            biasArray[i] = w[start++];
//        }
//        for(int i = 0; i < weights.rows(); i++) {
//            double[] row = weights.row(i);
//            for(int j = 0; j < weights.cols(); j++) {
//                row[j] = w[start++];
//            }
//        }
//        return start - oldStart;
//    }

}
