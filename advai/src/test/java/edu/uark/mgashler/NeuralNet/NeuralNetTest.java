package edu.uark.mgashler.NeuralNet;

import edu.uark.mgashler.NeuralNet.NeuralNet;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class NeuralNetTest extends TestCase {

//    public void testMath() {
//        NeuralNet nn = new NeuralNet();
//        LayerLinear l1 = new LayerLinear(2, 3);
//        l1.getWeights().set(0,0,0.1);
//        l1.getWeights().set(0,0,0.0);
//        l1.getWeights().row(0).add(0,0.1);
//        l1.getWeights().row(1).add(0,0.1);
//        l1.getWeights().row(1).add(0,0.0);
//        l1.getWeights().row(1).add(0,-0.1);
//        l1.getBiasArray().add(0,0.1);
//        l1.getBiasArray().add(1,0.1);
//        l1.getBiasArray().add(2,0.0);
//        nn.layers.add(l1);
//        nn.layers.add(new LayerTanh(3));
//
//        LayerLinear l2 = new LayerLinear(3, 2);
//        l2.getWeights().row(0).add(0,0.1);
//        l2.getWeights().row(0).add(1,0.1);
//        l2.getWeights().row(0).add(0,0.1);
//        l2.getWeights().row(0).add(1,0.3);
//        l2.getWeights().row(0).add(0,0.1);
//        l2.getWeights().row(0).add(1,-0.1);
//        l2.getBiasArray().add(0,0.1);
//        l2.getBiasArray().add(1,-0.2);
//        nn.layers.add(l2);
//        nn.layers.add(new LayerTanh(2));
//
//        System.out.println("l1 weights:" + l1.getWeights().toString());
//        System.out.println("l1 biasArray:" + l1.getBiasArray().toString());
//        System.out.println("l2 weights:" + l2.getWeights().toString());
//        System.out.println("l2 biasArray:" + l2.getBiasArray().toString());
//
//        System.out.println("----Forward prop");
//        List<Double> out = nn.forwardProp(Arrays.asList(0.3,-0.2));
//        System.out.println("activation:" + out.toString());
//
//        System.out.println("----Back prop");
//        List<Double> input = Arrays.asList(0.1,0.0);
//        nn.backProp(input);
//        System.out.println("error 2:" + l2.getErrorArray().toString());
//        System.out.println("error 1:" + l1.getErrorArray().toString());
//
//        nn.descendGradient(input, 0.1);
//        System.out.println("----Descending gradient");
//        System.out.println("l1 weights:" + l1.getWeights().toString());
//        System.out.println("l1 biasArray:" + Vec.toString(l1.getBiasArray()));
//        System.out.println("l2 weights:" + l2.getWeights().toString());
//        System.out.println("l2 biasArray:" + Vec.toString(l2.getBiasArray()));
//
//        if(Math.abs(l1.getWeights().row(0).get(0) - 0.10039573704287) > 0.0000000001) {
//            Assert.fail();
//        }
//        if(Math.abs(l1.getWeights().row(0).get(1) - 0.0013373814241446) > 0.0000000001) {
//            Assert.fail();
//        }
//        if(Math.abs(l1.getBiasArray().get(1) - 0.10445793808048) > 0.0000000001) {
//            Assert.fail();
//        }
//        Assert.assertTrue("passed",true);
//    }
}
