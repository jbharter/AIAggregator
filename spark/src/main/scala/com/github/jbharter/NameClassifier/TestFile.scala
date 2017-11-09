package com.github.jbharter.NameClassifier

import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

class ComplexNumber(real: Double, imag: Double) {
  def mag() : Double = {
    math.sqrt(math.pow(real, 2.0) + math.pow(imag, 2.0))
  }
  def flat() :Array[Double] = Array[Double](real,imag)
}

object ComplexNumber {
  def fromString(input: String): ComplexNumber = {
    // cleanse, assume imaginary, remove 'i's
    val str = (if (input.charAt(0) != '-') "+" else "").concat(input).replaceAll("i","")
    val operators = str.split("").zipWithIndex.filter(p => p._1 == "-" || p._1 == "+").map(_._2)
    val exps = str.split("").zipWithIndex.filter(p => p._1 == "e" || p._1 == "E").map(_._2)

    if (operators.length == 2) {
      new ComplexNumber(str.substring(0,operators(1)).toDouble,str.substring(operators(1)).toDouble)
    } else if (operators.length == 3) {
      // lead or lag exp
      val lead = exps.head < str.length - 4
      val left = if (lead) str.substring(0,operators(2)) else str.substring(0,operators(1))
      val right = if (lead) str.substring(operators(2)) else str.substring(operators(2))

      new ComplexNumber(left.toDouble,right.toDouble)
    } else if (operators.length == 4) {
      // double exp
      val left = str.substring(0,operators(2))
      val right = str.substring(operators(2))
      new ComplexNumber(left.toDouble,right.toDouble)
    } else {
      new ComplexNumber(Double.NegativeInfinity,Double.NegativeInfinity)
    }
  }
}

class Row(nameComponents: Array[Double],
          classification : Int,
          ftData : Array[ComplexNumber])
{
  def flatFt() : Array[Double] = {
    ftData.flatMap(_.flat())
  }
  def categorize() : LabeledPoint = {
    LabeledPoint(classification.toDouble,Vectors.dense(nameComponents++flatFt()))
  }
}

object Row {
  def fromProcessedNameStringRows(s:String): Array[Row] = {
    // Get Splits
    val rowArr = s.split(",")
    val nc = rowArr.slice(0,33).map(_.toDouble)
    val ft = rowArr.slice(168,202).map(e => ComplexNumber.fromString(e))

    // Single "row" for each classification
    rowArr.slice(33,168)
      .map(_.toDouble)
      .zipWithIndex.filter(_._1 == 1).map(_._2)
      .map(e => new Row(nc,e,ft))
  }
}

object TestFile extends App {

  val config = new SparkConf
      config.setMaster("local[*]")
      config.setAppName("remote scala-spark exec jharte")

    val sc = new SparkContext(config)

    //val local_file = new BufferedReader(new InputStreamReader(new FileInputStream("resources/procDataMassaged.csv"))).lines().collect(Collectors.toList())

  if (!FileSystem.get(sc.hadoopConfiguration).exists(new Path("resources/massagedMatProcessedNamesSVM"))) {
    println("here")
    // gather ze datas
    var nameCategoryFFTDatas = sc.textFile("resources/matprocessedNames.csv").collect()
    // massage ze datas
    // chop off column headers
    var nameCategoryFFTData =
    sc.parallelize(nameCategoryFFTDatas.slice(1,nameCategoryFFTDatas.length))
      // split on comma
      .flatMap((s:String) => Row.fromProcessedNameStringRows(s).iterator)
      .map(r => r.categorize())

    MLUtils.saveAsLibSVMFile(nameCategoryFFTData,"resources/massagedMatProcessedNamesSVM")
  }

  val data = MLUtils.loadLibSVMFile(sc,"resources/massagedMatProcessedNamesSVM")

//    val data = sc.textFile("resources/procDataMassaged.csv")
//      .map(s => s.split(","))
//      .map( arr => arr.map(_.toDouble))
//      .map(arr => LabeledPoint(arr(0),Vectors.dense(arr.slice(1,arr.length))))


    // store
    //MLUtils.saveAsLibSVMFile(data,"data/normlibSVM.txt")
    val splits = data.randomSplit(Array(0.7, 0.3))
    val (trainingData, testData) = (splits(0), splits(1))

    val numClasses = data.map(lp => lp.label).distinct().count().toInt
    val categoricalFeaturesInfo = Map[Int,Int]()
    val maxBins = numClasses
    val featureSubsetStrategy = "auto"
    val impurity = "gini"
    //for(i <- 100 to 500) {
      val numTrees = 500

      val maxDepth = 8
      val model = RandomForest.trainClassifier(trainingData.toJavaRDD(),numClasses,categoricalFeaturesInfo,numTrees,featureSubsetStrategy,impurity,maxDepth,maxBins,0)

      // Evaluate model on test instances and compute test error
      val labelAndPreds = testData.map { point =>
        val prediction = model.predict(point.features)
        (point.label, prediction)
      }
      val testMSE = labelAndPreds.map{ case(v, p) => math.pow(v - p, 2)}.mean()

      val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
      println("Test Error = " + testErr)
      println("Test MSE = " + testMSE)

      //save model
      model.save(sc, s"models/rfcm-$numTrees-$maxDepth-${BigDecimal(testErr).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble}")

    //}
}
