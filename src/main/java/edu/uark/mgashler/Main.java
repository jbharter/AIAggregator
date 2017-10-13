package edu.uark.mgashler;
import edu.uark.mgashler.NeuralNet.LayerLinear;
import edu.uark.mgashler.NeuralNet.LayerTanh;
import edu.uark.mgashler.NeuralNet.NeuralNet;

import java.io.File;
import java.util.Random;

class Main
{
	public static void main(String[] args)
	{
		NeuralNet nn = null;
		if(new File("model.json").exists())
		{
			// Load the existing model from the file
			System.out.println("Loading the model from file...");
			Json node = Json.load("model.json");
			nn = new NeuralNet(node);
		}
		else
		{
			// Make a new model
			System.out.println("Making a new model...");
			nn = new NeuralNet();
			nn.layers.add(new LayerLinear(3, 10));
			nn.layers.add(new LayerTanh(10));
			nn.layers.add(new LayerLinear(10, 8));
			nn.layers.add(new LayerTanh(8));
			nn.layers.add(new LayerLinear(8, 1));
			nn.layers.add(new LayerTanh(1));
			Random rand = new Random(1234);
			nn.init(rand);

			// Train the model to map from <0.1, 0.2, 0.3> to <0.4>
			System.out.println("Training it...");
			double[] in = new double[3];
			in[0] = 0.1;
			in[1] = 0.2;
			in[2] = 0.3;
			double[] target = new double[1];
			target[0] = 0.4;
			double learning_rate = 0.03;
			for(int i = 0; i < 5000; i++)
			{
				nn.trainIncremental(in, target, learning_rate);
			}		

			// Save the model to a file
			System.out.println("Saving it to file...");
			Json node = nn.marshal();
			node.save("model.json");
		}
		
		// Test the model
		System.out.println("Testing the model...");
		double[] in = new double[3];
		in[0] = 0.1;
		in[1] = 0.2;
		in[2] = 0.3;
		double[] prediction = nn.forwardProp(in);
		System.out.println("Prediction: " + Vec.toString(prediction));;
		System.out.println("Goodbye.");
	}
}