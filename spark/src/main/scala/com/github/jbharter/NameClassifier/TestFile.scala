package com.github.jbharter.NameClassifier

import java.io._
import java.util.stream.Collectors

import breeze.linalg.DenseMatrix
import breeze.linalg.csvwrite

import scala.collection.JavaConverters._

object TestFile extends App {

//  val config = new SparkConf
//  config.setMaster("local[8]")
//  config.setAppName("remote scala-spark exec jharte")
//
//  val sc = new SparkContext(config)

  val local_file = new BufferedReader(new InputStreamReader(new FileInputStream("resources/processedNames_trimmed.csv"))).lines().collect(Collectors.toList())

//  var headers = local_file.get(0).split(",")
//      headers = headers.slice(1,headers.length) // inconsequential for ml

  // enumerate vals to labeled points
  var parsedData = local_file.subList(1,local_file.size()).asScala
  //var dataRdd = sc.textFile("resources/processedNames_trimmed.csv")//sc.makeRDD(parsedData.map(s => (parsedData.indexOf(s),s)))

  var pd =
    parsedData.map( s => s.split(",").map(_.toInt))
              .map( l => (l.slice(0,33),l.slice(33,l.length)))
              .map( tup => tup._2.indexWhere(d => d == 1) +: tup._1)
              .map( a => a.map(_.toDouble))
              .toList

  var transposed = DenseMatrix(pd:_*)

  csvwrite(new File("resources/procNamesTrimmedTranspose.csv"), transposed, ',')




  //var atomicCounter:AtomicInteger = new AtomicInteger(0)

//  var transposed = sc.parallelize(dataRdd.collect.toSeq.transpose).map(_.mkString)
//
//  var peek = transposed.collect()

  println("here")

//  var normData = dataRdd
//    .map( str => str.split(","))
//    .map( arr => arr.map(_.toDouble))
//      .map( arr => {
//        arr.slice(33,arr.length)
//      })
//    .map( arr => Vectors.dense(arr))
//    .map( vec => LabeledPoint(atomicCounter.getAndIncrement(),vec))
//
//  // store
//  MLUtils.saveAsLibSVMFile(normData,"data/normSVM.txt")
//
//  val splits = normData.randomSplit(Array(0.7, 0.3))
//  val (trainingData, testData) = (splits(0), splits(1))
//
//  val halp = (trainingData.collect(), testData.collect())
//
//  val numClasses = atomicCounter.get() * 4
//  val categoricalFeaturesInfo = Map[Int, Int]((0,135))
//  val numTrees = 3 // Use more in practice.
//  val featureSubsetStrategy = "auto" // Let the algorithm choose.
//  val impurity = "entropy"
//  val maxDepth = 4
//  val maxBins = 135
//
//
//
//  val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
//    numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
//
//  // Evaluate model on test instances and compute test error
//  val labelAndPreds = testData.map { point =>
//    val prediction = model.predict(point.features)
//    (point.label, prediction)
//  }
//  val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
//  println("Test Error = " + testErr)
//  println("Learned classification forest model:\n" + model.toDebugString)

}
