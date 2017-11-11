package com.github.jbharter.NameClassifier;

//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import breeze.generic.UFunc;
//import breeze.linalg.*;
//import breeze.math.Complex;
//import breeze.numerics.abs;
//import breeze.signal.*;
//
//import breeze.signal.fourierTr;
//import scala.reflect.ClassTag;
//import scala.collection.JavaConverters.*;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class NameAugmentationPipeline {
//    public static List<Double> transformed(List<String> input) {
//
//        List<Double> list = input.stream().map(Double::parseDouble).collect(Collectors.toList());
//
//        DenseVector<Double> dv = fourierTr.apply(list, doubles -> new DenseVector<>(doubles.toArray(new Double[]{})));
//
//        scala.collection.Iterator<Double> s = dv.valuesIterator();
//        List<Double> list1 = new ArrayList<>();
//
//        while(s.hasNext()) {
//            list.add(s.next());
//        }
//        return list1;
//    }
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.addResource(new Path(args[0]));
        conf.addResource(new Path(args[1]));

        Path pt = new Path("."); // HDFS Path
        FileSystem fs = pt.getFileSystem(conf);

        System.out.println(fs.getHomeDirectory());
    }
}
