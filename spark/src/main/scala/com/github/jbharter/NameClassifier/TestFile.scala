package com.github.jbharter.NameClassifier

import java.io.{BufferedReader, FileInputStream, InputStreamReader}
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._

object TestFile extends App {

  val config = new SparkConf
  config.setMaster("local[8]")
  config.setAppName("remote scala-spark exec jharte")

  val sc = new SparkContext(config)

  val local_file = new BufferedReader(new InputStreamReader(new FileInputStream("resources/names_form.csv"))).lines().collect(Collectors.toList())

  var headers = local_file.get(0).split(",")
      headers = headers.slice(1,headers.length) // inconsequential for ml

  // enumerate vals to labeled points
  var parsedData = local_file.subList(1,local_file.size()).asScala
  var dataRdd = sc.textFile("resources/names_form_trimmed.csv")//sc.makeRDD(parsedData.map(s => (parsedData.indexOf(s),s)))

  var atomicCounter:AtomicInteger = new AtomicInteger(0)

  var normData = dataRdd
    .map( str => str.split(","))
    .map( arr => arr.slice(1,arr.length))
    .map(cleanArr => {
        //val ar = tuple._2.map(s => if (s.eq("")) "0" else s).map(_.toDouble)
       var t = Array.fill(headers.length) {0:Double}
       for (i <- cleanArr.indices ) {
         if (cleanArr(i).equals("")) { t(i) = 0 }
         else { t(i) = 1 }
       }
      t
    })
    .map( normarr => LabeledPoint(atomicCounter.getAndIncrement(), Vectors.dense(normarr)))

  val splits = normData.randomSplit(Array(0.7, 0.3))
  val (trainingData, testData) = (splits(0), splits(1))

  val halp = (trainingData.collect(), testData.collect())

  val numClasses = 80367
  val categoricalFeaturesInfo = Map[Int, Int]()
  val numTrees = 3 // Use more in practice.
  val featureSubsetStrategy = "auto" // Let the algorithm choose.
  val impurity = "gini"
  val maxDepth = 4
  val maxBins = 135

  val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
    numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

  // Evaluate model on test instances and compute test error
  val labelAndPreds = testData.map { point =>
    val prediction = model.predict(point.features)
    (point.label, prediction)
  }
  val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
  println("Test Error = " + testErr)
  println("Learned classification forest model:\n" + model.toDebugString)

}
