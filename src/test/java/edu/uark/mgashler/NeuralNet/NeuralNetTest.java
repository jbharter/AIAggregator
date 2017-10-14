package edu.uark.mgashler.NeuralNet;

import edu.uark.mgashler.Vec;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetTest extends TestCase {

    public void testMath() {
        NeuralNet nn = new NeuralNet();
        LayerLinear l1 = new LayerLinear(2, 3);
        l1.weights.row(0).set(0,0.1);
        l1.weights.row(0).set(0,0.0);
        l1.weights.row(0).set(0,0.1);
        l1.weights.row(1).set(0,0.1);
        l1.weights.row(1).set(0,0.0);
        l1.weights.row(1).set(0,-0.1);
        l1.biasArray.set(0,0.1);
        l1.biasArray.set(1,0.1);
        l1.biasArray.set(2,0.0);
        nn.layers.add(l1);
        nn.layers.add(new LayerTanh(3));

        LayerLinear l2 = new LayerLinear(3, 2);
        l2.weights.row(0).set(0,0.1);
        l2.weights.row(0).set(1,0.1);
        l2.weights.row(0).set(0,0.1);
        l2.weights.row(0).set(1,0.3);
        l2.weights.row(0).set(0,0.1);
        l2.weights.row(0).set(1,-0.1);
        l2.biasArray.set(0,0.1);
        l2.biasArray.set(1,-0.2);
        nn.layers.add(l2);
        nn.layers.add(new LayerTanh(2));

        System.out.println("l1 weights:" + l1.weights.toString());
        System.out.println("l1 biasArray:" + Vec.toString(l1.biasArray));
        System.out.println("l2 weights:" + l2.weights.toString());
        System.out.println("l2 biasArray:" + Vec.toString(l2.biasArray));

        System.out.println("----Forward prop");
        List<Double> in = new ArrayList<>(2);
        in.set(0, 0.3);
        in.set(1, -0.2);
        List<Double> out = nn.forwardProp(in);
        System.out.println("activation:" + Vec.toString(out));

        System.out.println("----Back prop");
        List<Double> targ = new ArrayList<>(2);
        targ.set(0,0.1);
        targ.set(1,0.0);
        nn.backProp(targ);
        System.out.println("error 2:" + Vec.toString(l2.error));
        System.out.println("error 1:" + Vec.toString(l1.error));

        nn.descendGradient(in, 0.1);
        System.out.println("----Descending gradient");
        System.out.println("l1 weights:" + l1.weights.toString());
        System.out.println("l1 biasArray:" + Vec.toString(l1.biasArray));
        System.out.println("l2 weights:" + l2.weights.toString());
        System.out.println("l2 biasArray:" + Vec.toString(l2.biasArray));

        if(Math.abs(l1.weights.row(0).get(0) - 0.10039573704287) > 0.0000000001) {
            Assert.fail();
        }
        if(Math.abs(l1.weights.row(0).get(1) - 0.0013373814241446) > 0.0000000001) {
            Assert.fail();
        }
        if(Math.abs(l1.biasArray.get(1) - 0.10445793808048) > 0.0000000001) {
            Assert.fail();
        }
        Assert.assertTrue("passed",true);
    }
}
