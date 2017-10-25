package edu.uark.mgashler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.uark.mgashler.NeuralNet.LayerLinear;
import edu.uark.mgashler.NeuralNet.LayerTanh;
import edu.uark.mgashler.NeuralNet.NeuralNet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

class Main {
	public static void main(String[] args) throws IOException {
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
		NeuralNet nn = null;
		if(new File("model.json").exists()) {
			// Load the existing model from the file
			System.out.println("Loading the model from file...");
			nn = gson.fromJson(new FileReader("model.json"), NeuralNet.class);
		} else {
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
			List<Double> in = new ArrayList<>(3);
			in.add(0, 0.1);
			in.add(1, 0.2);
			in.add(2, 0.3);
            List<Double> target = new ArrayList<>(1);
            target.add(0.4);
			Double learning_rate = 0.03;
			for(int i = 0; i < 5000; i++) {
				nn.trainIncremental(in, target, learning_rate);
			}		

			// Save the model to a file
			System.out.println("Saving it to file...");
			String s = gson.toJson(nn);
            FileWriter fileWriter = new FileWriter("model.json");
            fileWriter.write(s);
            fileWriter.flush();
            fileWriter.close();
		}
		
		// Test the model
		System.out.println("Testing the model...");
		List<Double> in = new ArrayList<>(3);
		in.set(0, 0.1);
		in.set(1, 0.2);
		in.set(2, 0.3);
		List<Double> prediction = nn.forwardProp(in);
        String vec = prediction.stream().map(Object::toString).collect(Collectors.joining(","));
		System.out.println("Prediction: " + vec);
		System.out.println("Goodbye.");
	}
}