package edu.uark.mgashler.NeuralNet;
// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralNet {
	public ArrayList<Layer> layers = new ArrayList<>();

	/// General-purpose constructor. (Starts with no layers. You must add at least one.)
	public NeuralNet() { }

	/// Copy constructor
//	public NeuralNet(NeuralNet that) {
//		for(int i = 0; i < that.layers.size(); i++) {
//			layers.add(that.layers.get(i).clone());
//		}
//	}

	/// Unmarshals from a JSON DOM.
//	public NeuralNet(Json n) {
//		Json l = n.get("layers");
//		for(int i = 0; i < l.size(); i++) {
//			layers.add(Layer.unmarshal(l.get(i)));
//		}
//	}

	/// Marshal this neural network into a JSON DOM.
//	public Json marshal()
//	{
//		Json ob = Json.newObject();
//		Json l = Json.newList();
//		ob.add("layers", l);
//		for(int i = 0; i < layers.size(); i++)
//			l.add(layers.get(i).marshal());
//		return ob;
//	}

	/// Initializes the weights and biases with small random values
	public void init(Random r) {
		for(int i = 0; i < layers.size(); i++) {
			getLayer(i).initWeights(r);
		}
	}



//    public double[] forwardProp(double[] in)
//    {
//        for(int i = 0; i < layers.size(); i++)
//        {
//            in = layers.get(i).forwardProp(in);
//        }
//        return in;
//    }

    private Layer getLayer(int index) {
	    return layers.get(index);
    }


	/// Feeds "in" into this neural network and propagates it forward to compute predicted outputs.
	public List<Double> forwardProp(List<Double> in) {

	    List<Double> temp = new ArrayList<>(in);
		for(int i = 0; i < layers.size(); i++) {
		    List<Double> t = getLayer(i).forwardProp(in);
		    in.clear();
		    in.addAll(t);
        }

		return temp;
	}

    /// Backpropagates the error to the upstream layer.
    void backProp(List<Double> target) {
        int i = layers.size() - 1;
        Layer l = getLayer(i);
        l.computeError(target);
        for(i--; i >= 0; i--) {
            Layer upstream = getLayer(i);
            l.backProp(upstream);
            l = upstream;
        }
    }

    /// Updates the weights and biases
    void descendGradient(List<Double> in, double learningRate) {
        for(int i = 0; i < layers.size(); i++) {
            Layer l = getLayer(i);
            l.scaleGradient(0.0);
            l.updateGradient(in);
            l.step(learningRate);
            in.clear();
            in.addAll(l.getActivationArray());
        }
    }

    /// Refines the weights and biases with on iteration of stochastic gradient descent.
    public void trainIncremental(List<Double> in, List<Double> target, double learningRate) {
        in = forwardProp(in);
        backProp(target);
        //backPropAndBendHinge(target, learningRate);
        descendGradient(in, learningRate);
    }

	/// Feeds the concatenation of "in1" and "in2" into this neural network and propagates it forward to compute predicted outputs.
//	double[] forwardProp2(double[] in1, double[] in2) {
//		double[] in = ((LayerLinear)layers.get(0)).forwardProp2(in1, in2);
//		for(int i = 1; i < layers.size(); i++) {
//			in = layers.get(i).forwardProp(in);
//		}
//		return in;
//	}




	/// Backpropagates the error from another neural network. (This is used when training autoencoders.)
//	void backPropFromDecoder(NeuralNet decoder) {
//		int i = layers.size() - 1;
//		Layer l = decoder.layers.get(0);
//		Layer upstream = layers.get(i);
//		l.backProp(upstream);
//		l = upstream;
//		for(i--; i >= 0; i--) {
//			upstream = layers.get(i);
//			l.backProp(upstream);
//			l = upstream;
//		}
//	}





	/// Keeps the weights and biases from getting too big
//	void regularize(double amount) {
//		for(int i = 0; i < layers.size(); i++) {
//			Layer lay = layers.get(i);
//			lay.regularizeWeights(amount);
//		}
//	}





	/// Refines "in" with one iteration of stochastic gradient descent.
//	void refineInputs(double[] in, double[] target, double learningRate) {
//		forwardProp(in);
//		backProp(target);
//		((LayerLinear)layers.get(0)).refineInputs(in, learningRate);
//	}

}
